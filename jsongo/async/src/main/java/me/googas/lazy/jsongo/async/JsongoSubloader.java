package me.googas.lazy.jsongo.async;

import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import lombok.NonNull;
import me.googas.lazy.jsongo.IJsongoSubloader;
import me.googas.lazy.jsongo.async.collection.FutureStream;
import me.googas.lazy.jsongo.query.Query;
import org.bson.Document;
import org.bson.conversions.Bson;

public abstract class JsongoSubloader<T> implements IJsongoSubloader<Jsongo> {

  @NonNull protected final Jsongo parent;
  @NonNull protected final MongoCollection<Document> collection;

  protected JsongoSubloader(@NonNull Jsongo parent, @NonNull MongoCollection<Document> collection) {
    this.parent = parent;
    this.collection = collection;
  }

  @NonNull
  protected CompletableFuture<Boolean> delete(@NonNull Bson query) {
    return Streams.of(this.collection.deleteOne(query), result -> result.getDeletedCount() > 0)
        .asSingleton();
  }

  @NonNull
  protected CompletableFuture<Boolean> delete(@NonNull Query query) {
    return this.delete(query.build(this.parent.getGson()));
  }

  @NonNull
  protected CompletableFuture<Long> deleteMany(@NonNull Bson query) {
    return Streams.of(this.collection.deleteMany(query), DeleteResult::getDeletedCount)
        .asSingleton();
  }

  @NonNull
  protected CompletableFuture<Boolean> save(@NonNull Bson query, @NonNull Document document) {
    return Streams.of(
            this.collection.replaceOne(query, document, new ReplaceOptions().upsert(true)),
            result -> result.getModifiedCount() > 0)
        .asSingleton();
  }

  @NonNull
  private CompletableFuture<Boolean> save(@NonNull Query query, @NonNull Document document) {
    return this.save(query.build(this.parent.getGson()), document);
  }

  @NonNull
  protected CompletableFuture<Optional<T>> get(@NonNull Bson query) {
    return Streams.of(
            this.collection.find(query),
            document ->
                Optional.ofNullable(
                    document == null
                        ? null
                        : this.parent.getGson().fromJson(document.toJson(), this.getTypeClazz())))
        .asSingleton();
  }

  @NonNull
  protected CompletableFuture<Optional<T>> get(@NonNull Query query) {
    return this.get(query.build(this.parent.getGson()));
  }

  @NonNull
  ReactiveStreamBuilder<Document, T> getManyBuilder(
      @NonNull Bson query, @NonNull Consumer<T> onBuild) {
    return Streams.of(
        this.collection.find(query),
        document -> {
          T t = this.parent.getGson().fromJson(document.toJson(), this.getTypeClazz());
          onBuild.accept(t);
          return t;
        });
  }

  @NonNull
  protected FutureStream<T> getMany(@NonNull Bson query) {
    return this.getManyBuilder(query, t -> {}).asCollection();
  }

  @NonNull
  protected FutureStream<T> getMany(@NonNull Query query) {
    return this.getMany(query.build(this.parent.getGson()));
  }

  /**
   * Get the type of the object that this subloader manages.
   *
   * @return the type of the object
   */
  @NonNull
  public abstract Class<T> getTypeClazz();
}
