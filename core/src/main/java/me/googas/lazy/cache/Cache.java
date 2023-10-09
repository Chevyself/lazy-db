package me.googas.lazy.cache;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;

/**
 * An object which represents a way to manage cache. This stores the catchables objects and the time
 * for their removal
 */
public interface Cache extends Runnable {

  /**
   * Get an object from cache. This will refresh the object inside of cache use {@link #get(Class,
   * Predicate, boolean)} to not refresh it
   *
   * @param clazz the clazz of the catchable for casting
   * @param predicate the predicate to match the catchable
   * @param <T> the type of the catchable
   * @return a {@link Optional} instance containing the catchable if found else null
   */
  @NonNull
  default <T extends Catchable> Optional<T> get(
      @NonNull Class<T> clazz, @NonNull Predicate<T> predicate) {
    return this.get(clazz, predicate, true);
  }

  /**
   * Create a {@link Stream} of filtered {@link Catchable} using the predicate.
   *
   * @param clazz the class of catchables to get
   * @param predicate the predicate to filter the catchables
   * @param <T> the type of the catchable
   * @return the {@link Stream} of filtered catchables
   */
  @NonNull
  <T extends Catchable> Stream<T> filter(@NonNull Class<T> clazz, @NonNull Predicate<T> predicate);
  /**
   * Get an object from cache and select whether to refresh it.
   *
   * @param clazz the clazz of the catchable for casting
   * @param predicate the predicate to match the catchable
   * @param refresh whether to refresh the object. By refreshing means that the time of the object
   *     inside the cache will be extended to its initial value
   * @param <T> the type of the catchable
   * @return a {@link Optional} instance containing the catchable if found else null
   */
  @NonNull
  default <T extends Catchable> Optional<T> get(
      @NonNull Class<T> clazz, @NonNull Predicate<T> predicate, boolean refresh) {
    Optional<T> optional = this.filter(clazz, predicate).findFirst();
    if (refresh) optional.ifPresent(this::refresh);
    return optional;
  }

  /**
   * Get an object from cache and refresh it or return a default value in case the object is not
   * found inside the cache.
   *
   * @param clazz the clazz of the catchable for casting
   * @param predicate the predicate to match the catchable
   * @param def the default value to provide if the catchable is not found in cache
   * @param <T> the type of the catchable
   * @return the catchable if found else the default value
   */
  @NonNull
  @Deprecated
  default <T extends Catchable> T getOr(
      @NonNull Class<T> clazz, @NonNull T def, @NonNull Predicate<T> predicate) {
    return this.get(clazz, predicate).orElse(def);
  }

  /**
   * Get an object from cache and refresh it or return a default value supplied by a {@link
   * Supplier} in case the object is not found inside the cache.
   *
   * @param clazz the clazz of the catchable for casting
   * @param predicate the predicate to match the catchable
   * @param supplier the supplier to get the default value to provide if not found in cache
   * @param <T> the type of the catchable
   * @return the catchable if found else the default value provided by the supplier
   */
  @Deprecated
  default <T extends Catchable> T getOrSupply(
      @NonNull Class<T> clazz, @NonNull Predicate<T> predicate, @NonNull Supplier<T> supplier) {
    return this.get(clazz, predicate).orElseGet(supplier);
  }

  /**
   * Get a list of catchables matching a predicate. This will not refresh the objects use {@link
   * #refresh(Catchable)} to refresh them
   *
   * @param clazz the clazz of catchables for casting
   * @param predicate the predicate to match the catchables
   * @param <T> the type of the catchables
   * @return the list of catchables this will not be null, but it could be empty
   */
  @NonNull
  default <T extends Catchable> Collection<T> getMany(
      @NonNull Class<T> clazz, @NonNull Predicate<T> predicate) {
    return this.filter(clazz, predicate).collect(Collectors.toList());
  }

  /**
   * Checks whether an object is inside the cache.
   *
   * @param catchable the object to check if it is inside the cache
   * @return true if the object is inside the cache
   */
  boolean contains(@NonNull Catchable catchable);

  /**
   * Adds an object to the cache.
   *
   * @param catchable the object to be added
   * @throws IllegalStateException if there's an instance of the object in cache already
   */
  void add(@NonNull Catchable catchable);

  /**
   * Get the time left of an object inside of cache as milliseconds.
   *
   * @param catchable the object to check the time
   * @return the time of the object inside of cache. If the object is null it will return 0
   *     milliseconds
   */
  long getTimeLeft(@NonNull Catchable catchable);

  /**
   * Removes an object from cache.
   *
   * @param catchable the object to be removed
   * @return whether the object was removed from cache
   */
  boolean remove(@NonNull Catchable catchable);

  /**
   * Refreshes a catchable object.
   *
   * @param catchable the object to be cached
   */
  void refresh(@NonNull Catchable catchable);

  /** Closes the cache and removes all the objects inside of it. */
  void close();

  /**
   * Get the time in which an object must be removed.
   *
   * @param catchable the object to get the removal time
   * @return the removal time of the object
   */
  default long getTimeToRemove(@NonNull Catchable catchable) {
    return System.currentTimeMillis() + catchable.getToRemove();
  }

  /**
   * This consumer for {@link Throwable} is for try and catches that may be used in for {@link
   * Catchable#onRemove()}.
   *
   * @return the consumer handler for {@link Throwable}
   */
  @NonNull
  Consumer<Throwable> getHandler();
}
