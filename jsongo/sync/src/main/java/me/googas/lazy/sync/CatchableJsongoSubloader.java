package me.googas.lazy.sync;

import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.NonNull;
import me.googas.lazy.cache.Cache;
import me.googas.lazy.cache.Catchable;
import me.googas.lazy.jsongo.query.ElementIdSupplier;
import me.googas.lazy.jsongo.query.Query;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 * A subloader for catchable objects. Children of the loader {@link Jsongo}
 *
 * @param <T> the type of the catchable
 */
public abstract class CatchableJsongoSubloader<T extends Catchable> extends JsongoSubloader<T> {

  @NonNull private final Map<Class<?>, ElementIdSupplier> elementIdSuppliers = new HashMap<>();

  /**
   * Create the subloader.
   *
   * @param parent the parent loader
   * @param collection the collection where the objects will be managed.
   */
  protected CatchableJsongoSubloader(
      @NonNull Jsongo parent, @NonNull MongoCollection<Document> collection) {
    super(parent, collection);
  }

  /**
   * Get a {@link Catchable} from the database. If the object is obtained from the database it will
   * be added to cache
   *
   * @param query the query to match the catchable
   * @param predicate the predicate to match the catchable inside the cache
   * @return a {@link Optional} instance holding the nullable catchable
   */
  @NonNull
  protected Optional<T> get(@NonNull Bson query, @NonNull Predicate<T> predicate) {
    return Optional.ofNullable(
        this.parent
            .getCache()
            .get(this.getTypeClazz(), predicate, true)
            .orElseGet(
                () -> {
                  Optional<T> optional = this.get(query);
                  optional.ifPresent(catchable -> this.parent.getCache().add(catchable));
                  return optional.orElse(null);
                }));
  }

  /**
   * Get a {@link Catchable} from the database. If the object is obtained from the database it will
   * be added to cache
   *
   * @param query the query to match the catchable
   * @param predicate the predicate to match the catchable inside the cache
   * @return a {@link Optional} instance holding the nullable catchable
   */
  protected Optional<T> get(@NonNull Query query, @NonNull Predicate<T> predicate) {
    return this.get(query.build(this.parent.getGson()), predicate);
  }

  /**
   * Get {@link Catchable} from the database. This will first get all elements from the database,
   * then the elements in cache, if there's elements from the database not in cache those will be
   * added to the cache, and the resulting list of cached elements will be returned
   *
   * @param query the query to find the elements from the database
   * @param predicate the predicate to find the elements in cache
   * @return the elements from the database and cache
   */
  @NonNull
  protected List<T> getMany(@NonNull Bson query, @NonNull Predicate<T> predicate) {
    Cache cache = this.parent.getCache();
    Collection<T> inCache = this.getParent().getCache().getMany(this.getTypeClazz(), predicate);
    // Add to query 'not' to get the elements that are not in cache
    if (!inCache.isEmpty()) {
      List<Object> ids =
          inCache.stream()
              .map(catchable -> this.getIdSupplier(catchable).getId(catchable))
              .collect(Collectors.toList());
      query = Query.of("{$and: [{_id: {$nin: #}}, #]}", ids, query).build(this.parent.getGson());
    }
    List<T> inDatabase = this.getMany(query);
    for (T catchable : inDatabase) {
      if (!cache.contains(catchable)) {
        this.getParent().getCache().add(catchable);
        inCache.add(catchable);
      }
    }
    return new ArrayList<>(inCache);
  }

  @NonNull
  private ElementIdSupplier getIdSupplier(@NonNull Catchable catchable) {
    return this.elementIdSuppliers.computeIfAbsent(
        catchable.getClass(), key -> ElementIdSupplier.getSupplier(catchable));
  }

  /**
   * Get {@link Catchable} from the database.
   *
   * @see #getMany(Bson)
   * @param query the query to find the elements from the database
   * @param predicate the predicate to find the elements in cache
   * @return the elements from the database and cache
   */
  @NonNull
  protected Collection<T> getMany(@NonNull Query query, @NonNull Predicate<T> predicate) {
    return this.getMany(query.build(this.parent.getGson()), predicate);
  }
}
