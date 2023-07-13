package me.googas.lazy.cache;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.NonNull;

/** An object which represents a way to manage cache in maps. */
public interface CacheMap extends Cache {

  /**
   * Creates a copy of the current cache.
   *
   * @return the copy of the current cache
   */
  @NonNull
  default Collection<SoftReference<Catchable>> keySetCopy() {
    return new HashSet<>(this.getMap().keySet());
  }

  /**
   * Set the consumer to be used in exceptions.
   *
   * @see #getHandler()
   * @param handler the handler
   * @return this same instance
   */
  @NonNull
  CacheMap handle(@NonNull Consumer<Throwable> handler);

  /**
   * Get a copy of the cache map. To see what is the cache map
   *
   * @see #getMap()
   * @return a copy of the map
   */
  @NonNull
  default Map<SoftReference<Catchable>, Long> getMapCopy() {
    return new HashMap<>(this.getMap());
  }

  /**
   * This map contains the reference to the cache object and the time in millis for the object to be
   * removed.
   *
   * @return the map with the reference and time of the objects
   */
  @NonNull
  Map<SoftReference<Catchable>, Long> getMap();

  @Override
  default <T extends Catchable> @NonNull Stream<T> filter(
      @NonNull Class<T> clazz, @NonNull Predicate<T> predicate) {
    return this.keySetCopy().stream()
        .filter(
            reference -> {
              Catchable catchable = reference.get();
              return catchable != null && clazz.isAssignableFrom(catchable.getClass());
            })
        .map(reference -> clazz.cast(reference.get()))
        .filter(predicate);
  }

  @Override
  default boolean contains(@NonNull Catchable catchable) {
    for (SoftReference<Catchable> reference : this.keySetCopy()) {
      Catchable referencedCatchable = reference.get();
      if (catchable.equals(referencedCatchable)
          || (referencedCatchable != null
              && catchable.hashCode() == referencedCatchable.hashCode())) {
        return true;
      }
    }
    return false;
  }

  @Override
  default void add(@NonNull Catchable catchable) {
    if (this.contains(catchable)) {
      throw new IllegalStateException(
          "There's already an instance of " + catchable + " inside of the cache");
    }
    this.getMap().put(new SoftReference<>(catchable), this.getTimeToRemove(catchable));
  }

  @Override
  default long getTimeLeft(@NonNull Catchable catchable) {
    return this.getMapCopy().entrySet().stream()
        .filter(entry -> catchable.equals(entry.getKey().get()))
        .map(
            entry -> {
              long millis = entry.getValue() - System.currentTimeMillis();
              return millis < 0 ? 0 : millis;
            })
        .findFirst()
        .orElse(0L);
  }

  @Override
  default boolean remove(@NonNull Catchable catchable) {
    return this.getMap()
        .keySet()
        .removeIf(
            reference -> {
              Catchable stored = reference.get();
              return catchable.equals(stored)
                  || (stored != null && catchable.hashCode() == stored.hashCode());
            });
  }

  @Override
  default void refresh(@NonNull Catchable catchable) {
    for (SoftReference<Catchable> reference : this.getMap().keySet()) {
      if (catchable.equals(reference.get())) {
        this.getMap().put(reference, this.getTimeToRemove(catchable));
      }
    }
  }

  @Override
  default void close() {
    this.keySetCopy()
        .forEach(
            reference -> {
              Catchable catchable = reference.get();
              if (catchable != null) {
                // If catchable has not expired yet
                try {
                  catchable.onRemove();
                } catch (Throwable e) {
                  this.getHandler().accept(e);
                }
                this.remove(catchable);
              }
            });
    this.getMap().clear();
  }

  /**
   * This consumer for {@link Throwable} is for try and catches that may be used in for {@link
   * Catchable#onRemove()}.
   *
   * @return the consumer handler for {@link Throwable}
   */
  @NonNull
  Consumer<Throwable> getHandler();

  @Override
  default void run() {
    // Get a copy of the map to avoid concurrent modification exception
    this.getMapCopy()
        .forEach(
            (reference, time) -> {
              if (reference == null) return;
              Catchable catchable = reference.get();
              if (catchable != null && (time == null || System.currentTimeMillis() >= time)) {
                try {
                  catchable.onRemove();
                } catch (Throwable e) {
                  this.getHandler().accept(e);
                }
                reference.clear();
              }
            });
    this.getMap().keySet().removeIf(reference -> reference.get() == null);
  }
}
