package me.googas.lazy;

import lombok.NonNull;

/**
 * This object is the representation of the database. This contains the subloaders which are
 * actually the objects that handle all the information, this one may be called its parent as it
 * hold information such as the connection.
 *
 * <p>Taking 'Jsongo' as an example: 'Jsongo' holds the client information: cache, gson, mongo
 * client. Which are used by the subloaders as: the cache to get or manipulate objects which are
 * loaded, gson to deserialize the objects which are queried using the mongo client.
 */
public interface Loader {

  /**
   * Get a subloader.
   *
   * @param clazz the class of the subloader or a class that is assignable for it
   * @param <S> the type of the subloader
   * @return the subloader
   * @throws NullPointerException if the subloader could not be found
   */
  @NonNull
  <S extends Subloader> S getSubloader(@NonNull Class<S> clazz);

  /**
   * Safely close the loader.
   *
   * @throws LoaderCouldNotBeClosedException if it cannot be closed successfully
   */
  void close() throws LoaderCouldNotBeClosedException;
}
