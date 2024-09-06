import static io.quarkus.scheduler.Scheduled.ConcurrentExecution.SKIP;
import static java.lang.String.join;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class MemoryCache {

  private final EntityManager em;

  private final Map<UUID, Code> codeCache = new ConcurrentHashMap<>();

  public MemoryCache(EntityManager em) {
    this.em = em;
  }

  @Startup
  public void buildCache() {
    try {
      em
        .createQuery("from Code c", Code.class)
        .getResultStream()
        .forEach(code -> codeCache.put(code.getResourceIdentifier(), code));
    } catch (Exception e) {
      throw new IllegalStateException("Failed to complete build of cache", e);
    }
    Log.infof("Built cache with %d codes", codeCache.size());
    Log.debug("Done with startat event");
  }

  @Scheduled(every = "5s", delayed = "5s", concurrentExecution = SKIP)
  public void updateCache() {
    var codesUUID = codeCache.keySet().stream().toList();
    var findRemovedCodes = new StringBuilder("(values"); // https://stackoverflow.com/questions/17209460/select-those-not-found-in-in-list
    // var findRemovedCodes = new StringBuilder("(select tmp.resource_id from (values("); // https://stackoverflow.com/questions/17209460/select-those-not-found-in-in-list
    for (int i = 0; i < codeCache.size(); i++) {
      findRemovedCodes.append("('%s'\\:\\:uuid)".formatted(codesUUID.get(i)));
      if (i < codeCache.size() - 1) {
        findRemovedCodes.append(",");
      }
    }
    // findRemovedCodes.append(") as tmp(resource_id)");
    findRemovedCodes.append("except all select t.resource_id from code t)");
    var findAddedCodes =
      "union select x.resource_id from code x where x.resource_id not in :resource_id";
    var query = join(" ", findRemovedCodes.toString(), findAddedCodes);

    em
      .createNativeQuery(query, UUID.class)
      .setParameter("resource_id", codesUUID)
      .getResultList();
  }
}
