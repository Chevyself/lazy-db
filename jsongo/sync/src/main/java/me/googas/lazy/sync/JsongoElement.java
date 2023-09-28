package me.googas.lazy.sync;

import java.util.Optional;
import lombok.NonNull;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 * This interface represents an object which may be able to be identified using a {@link ObjectId}.
 * The id may only be updated once using {@link #setObjectId(ObjectId)} from {@link
 * JsongoSubloader#save(Bson, Object)} then it will be deserialized automatically. It is optional as
 * if the object hasn't been saved in the database its id is not defined yet.
 */
public interface JsongoElement {

  /**
   * Set the new id of the element.
   *
   * @param id the new id
   */
  @NonNull
  void setObjectId(ObjectId id);

  /**
   * Get the id of the element.
   *
   * @return a {@link Optional} holding the nullable id
   */
  @NonNull
  Optional<ObjectId> getObjectId();

  /**
   * Require the id of the element.
   *
   * @return the id of the element
   * @throws NullPointerException if it does not have an id
   */
  @NonNull
  default ObjectId requireObjectId() {
    return this.getObjectId().orElseThrow(NullPointerException::new);
  }
}
