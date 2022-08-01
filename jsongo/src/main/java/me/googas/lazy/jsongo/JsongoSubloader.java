package me.googas.lazy.jsongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import me.googas.lazy.Subloader;
import me.googas.lazy.cache.Catchable;
import org.bson.Document;

/** Manages objects using a {@link MongoCollection}. Children of the loader {@link Jsongo} */
public abstract class JsongoSubloader implements Subloader {

  @NonNull @Getter protected final Jsongo parent;
  @NonNull protected final MongoCollection<Document> collection;

  /**
   * Create the subloader.
   *
   * @param parent the parent loader
   * @param collection the collection where the objects will be managed.
   */
  protected JsongoSubloader(@NonNull Jsongo parent, @NonNull MongoCollection<Document> collection) {
    this.parent = parent;
    this.collection = collection;
  }

  /**
   * Delete an element.
   *
   * @param query the query to match the object
   * @return whether the element has been deleted
   */
  protected boolean delete(@NonNull Document query) {
    return this.collection.deleteOne(query).getDeletedCount() > 0;
  }

  /**
   * Delete many elements.
   *
   * @param query the query to match the objects
   * @return the amount of elements that were deleted
   */
  protected long deleteMany(@NonNull Document query) {
    return this.collection.deleteMany(query).getDeletedCount();
  }

  /**
   * Save/insert an element into the collection. If the element already exists in the database it
   * will be replaced else it will be inserted, to filter the parameter query will be used
   *
   * @param query the query to check if the element already exists in the database
   * @param object the object to save
   * @return this same instance
   */
  @NonNull
  protected JsongoSubloader save(@NonNull Document query, @NonNull Object object) {
    Document document = Document.parse(this.parent.getGson().toJson(object));
    document.remove("_id");
    Document first = this.collection.find(query).first();
    if (first != null) {
      this.collection.replaceOne(query, document);
    } else {
      this.collection.insertOne(document);
      if (object instanceof JsongoElement)
        ((JsongoElement) object).setObjectId(document.getObjectId("_id"));
    }
    return this;
  }

  /**
   * Get an object from the database.
   *
   * @param typeOfT The class of the object
   * @param query the query to match the object
   * @param <T> the type of the object
   * @return a {@link Optional} instance holding the nullable object
   */
  @NonNull
  protected <T> Optional<T> get(@NonNull Class<T> typeOfT, @NonNull Document query) {
    Document document = this.collection.find(query).first();
    T other = null;
    if (document != null) other = this.parent.getGson().fromJson(document.toJson(), typeOfT);
    return Optional.ofNullable(other);
  }

  /**
   * Get a {@link Catchable} from the database. If the object is obtained from the database it will
   * be added to cache
   *
   * @param typeOfC the class of the catchable
   * @param query the query to match the catchable
   * @param predicate the predicate to match the catchable inside the cache
   * @param <C> the type of the catchable
   * @return a {@link Optional} instance holding the nullable catchable
   */
  @NonNull
  protected <C extends Catchable> Optional<C> get(
      @NonNull Class<C> typeOfC, @NonNull Document query, @NonNull Predicate<C> predicate) {
    return Optional.ofNullable(
        this.parent
            .getCache()
            .get(typeOfC, predicate, true)
            .orElseGet(
                () -> {
                  Optional<C> optional = this.get(typeOfC, query);
                  optional.ifPresent(catchable -> this.parent.getCache().add(catchable));
                  return optional.orElse(null);
                }));
  }

  /**
   * Get many objects.
   *
   * @param typeOfT the type of the objects to get
   * @param query the query to match the objects
   * @param <T> the type of the objects
   * @return a list holding the objects
   */
  @NonNull
  public <T> List<T> getMany(@NonNull Class<T> typeOfT, @NonNull Document query) {
    List<T> list = new ArrayList<>();
    MongoCursor<Document> cursor = this.collection.find(query).cursor();
    while (cursor.hasNext()) {
      T other = this.parent.getGson().fromJson(cursor.next().toJson(), typeOfT);
      if (other != null) list.add(other);
    }
    return list;
  }
}
