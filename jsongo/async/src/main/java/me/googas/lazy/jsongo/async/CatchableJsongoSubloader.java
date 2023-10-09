package me.googas.lazy.jsongo.async;

import com.mongodb.reactivestreams.client.MongoCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.NonNull;
import me.googas.lazy.cache.Cache;
import me.googas.lazy.cache.Catchable;
import me.googas.lazy.jsongo.async.collection.FutureStream;
import me.googas.lazy.jsongo.query.ElementIdSupplier;
import me.googas.lazy.jsongo.query.Query;
import org.bson.Document;
import org.bson.conversions.Bson;

public abstract class CatchableJsongoSubloader<T extends Catchable> extends JsongoSubloader<T> {

  @NonNull private final Map<Class<?>, ElementIdSupplier> elementIdSuppliers = new HashMap<>();

  protected CatchableJsongoSubloader(
      @NonNull Jsongo parent, @NonNull MongoCollection<Document> collection) {
    super(parent, collection);
  }

  @NonNull
  protected CompletableFuture<Optional<T>> get(
      @NonNull Bson query, @NonNull Predicate<T> predicate) {
    CompletableFuture<Optional<T>> future = new CompletableFuture<>();
    Optional<T> optional = this.parent.getCache().get(this.getTypeClazz(), predicate);
    if (optional.isPresent()) {
      future.complete(optional);
    } else {
      this.get(query)
          .thenAccept(
              dbOptional -> {
                dbOptional.ifPresent(c -> this.parent.getCache().add(c));
                future.complete(dbOptional);
              });
    }
    return future;
  }

  @NonNull
  protected CompletableFuture<Optional<T>> get(
      @NonNull Query query, @NonNull Predicate<T> predicate) {
    return this.get(query.build(this.parent.getGson()), predicate);
  }

  @NonNull
  protected FutureStream<T> getMany(@NonNull Bson query, @NonNull Predicate<T> predicate) {
    Cache cache = this.parent.getCache();
    Collection<T> inCache = cache.getMany(this.getTypeClazz(), predicate);
    // Add to query 'not' to get the elements that are not in cache
    if (!inCache.isEmpty()) {
      List<Object> ids =
          inCache.stream()
              .map(catchable -> this.getIdSupplier(catchable).getId(catchable))
              .collect(Collectors.toList());
      query = Query.of("{$and: [{_id: {$nin: #}}, #]}", ids, query).build(this.parent.getGson());
    }
    return this.getManyBuilder(
            query,
            t -> {
              if (cache.contains(t)) {
                throw new IllegalStateException("Element " + t + " is already in cache");
              } else {
                cache.add(t);
              }
            })
        .asCollection(inCache, Long.MAX_VALUE);
  }

  @NonNull
  protected FutureStream<T> getMany(@NonNull Query query, @NonNull Predicate<T> predicate) {
    return this.getMany(query.build(this.parent.getGson()), predicate);
  }

  @NonNull
  private ElementIdSupplier getIdSupplier(@NonNull Catchable catchable) {
    return this.elementIdSuppliers.computeIfAbsent(
        catchable.getClass(), key -> ElementIdSupplier.getSupplier(catchable));
  }
}
