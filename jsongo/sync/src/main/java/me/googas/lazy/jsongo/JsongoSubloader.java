package me.googas.lazy.jsongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.ReplaceOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import me.googas.lazy.Subloader;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/** Manages objects using a {@link MongoCollection}. Children of the loader {@link Jsongo} */
@Getter
public abstract class JsongoSubloader<T> implements Subloader {

  @NonNull
  protected final Jsongo parent;
  @NonNull protected final MongoCollection<Document> collection;

  /**
   * Get the type of the object that this subloader manages.
   *
   * @return the type of the object
   */
  @NonNull
  public abstract Class<T> getTypeClazz();

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
  protected boolean delete(@NonNull Bson query) {
    return this.collection.deleteOne(query).getDeletedCount() > 0;
  }

  /**
   * Delete an element.
   *
   * @param query the query to match the object
   * @return whether the element has been deleted
   */
  protected boolean delete(@NonNull Query query) {
    return this.delete(query.build(this.parent.getGson()));
  }

  /**
   * Delete many elements.
   *
   * @param query the query to match the objects
   * @return the amount of elements that were deleted
   */
  protected long deleteMany(@NonNull Bson query) {
    return this.collection.deleteMany(query).getDeletedCount();
  }

  /**
   * Delete many elements.
   *
   * @param query the query to match the objects
   * @return the amount of elements that were deleted
   */
  protected long deleteMany(@NonNull Query query) {
    return this.deleteMany(query.build(this.parent.getGson()));
  }

  /**
   * Whether the field ´_id´ should be removed from the save query. This is useful when the primary
   * key is not the default one as {@link ObjectId}. Although it is recommended to override the
   * ´_id´ field
   *
   * @deprecated unused
   * @return whether the field should be removed
   */
  @Deprecated
  public boolean isRemoveIdFromDocument() {
    return false;
  }

  /**
   * Save/insert an element into the collection. If the element already exists in the database it
   * will be replaced else it will be inserted, to filter the parameter query will be used
   *
   * @param query the query to check if the element already exists in the database
   * @param object the object to save
   * @return whether the replacement was acknowledged
   */
  protected boolean save(@NonNull Bson query, @NonNull T object) {
    Document document = Document.parse(this.parent.getGson().toJson(object));
    Bson first = this.collection.find(query).first();
    return this.collection
        .replaceOne(query, document, new ReplaceOptions().upsert(true))
        .wasAcknowledged();
  }

  /**
   * Save/insert an element into the collection. If the element already exists in the database it
   * will be replaced else it will be inserted, to filter the parameter query will be used
   *
   * @param query the query to check if the element already exists in the database
   * @param object the object to save
   * @return whether the replacement was acknowledged
   */
  protected boolean save(@NonNull Query query, @NonNull T object) {
    return this.save(query.build(this.parent.getGson()), object);
  }

  /**
   * Get an object from the database.
   *
   * @param typeOfT The class of the object
   * @param query the query to match the object
   * @return a {@link Optional} instance holding the nullable object
   */
  @Deprecated
  @NonNull
  protected Optional<T> get(@NonNull Class<T> typeOfT, @NonNull Bson query) {
    return this.get(query);
  }

  /**
   * Get an object from the database.
   *
   * @param query the query to match the object
   * @return a {@link Optional} instance holding the nullable object
   */
  protected Optional<T> get(@NonNull Bson query) {
    Document document = this.collection.find(query).first();
    T other = null;
    if (document != null)
      other = this.parent.getGson().fromJson(document.toJson(), this.getTypeClazz());
    return Optional.ofNullable(other);
  }

  /**
   * Get an object from the database.
   *
   * @param query the query to match the object
   * @return a {@link Optional} instance holding the nullable object
   */
  @NonNull
  protected Optional<T> get(@NonNull Query query) {
    return this.get(query.build(this.parent.getGson()));
  }

  /**
   * Get many objects.
   *
   * @param typeOfT the type of the objects to get
   * @param query the query to match the objects
   * @return a list holding the objects
   */
  @Deprecated
  @NonNull
  protected List<T> getMany(@NonNull Class<T> typeOfT, @NonNull Bson query) {
    return this.getMany(query);
  }

  /**
   * Get many objects from the database.
   *
   * @param query the query to match the objects
   * @return a list holding the objects
   */
  @NonNull
  protected List<T> getMany(@NonNull Bson query) {
    List<T> list = new ArrayList<>();
    try (MongoCursor<Document> cursor = this.collection.find(query).cursor()) {
      while (cursor.hasNext()) {
        T other = this.parent.getGson().fromJson(cursor.next().toJson(), this.getTypeClazz());
        if (other != null) list.add(other);
      }
    }
    return list;
  }

  /**
   * Get many objects from the database.
   *
   * @param query the query to match the objects
   * @return a list holding the objects
   */
  protected List<T> getMany(@NonNull Query query) {
    return this.getMany(query.build(this.parent.getGson()));
  }
}
