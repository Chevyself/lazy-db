package me.googas.lazy.cache;

/** An object which can be stored inside {@link Cache}. */
public interface Catchable {

  /**
   * Called when the cache is ready to remove this object. This will be called in {@link
   * Cache#run()}
   *
   * @throws Throwable in case something goes wrong while unloading this object
   */
  default void onRemove() throws Throwable {}

  /**
   * Get the milliseconds for the object to be removed from cache.
   *
   * @return the time to be removed
   */
  long getToRemove();
}
