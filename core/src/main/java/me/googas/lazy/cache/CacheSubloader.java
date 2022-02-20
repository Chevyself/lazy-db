package me.googas.lazy.cache;

import lombok.NonNull;
import me.googas.lazy.Subloader;

/** A {@link Subloader} used in {@link LazyCache}. */
public abstract class CacheSubloader implements Subloader {

  @NonNull protected final LazyCache parent;

  /**
   * Constructor to be used in {@link CacheSubloaderBuilder}.
   *
   * @param parent the parent of this subloader.1
   */
  protected CacheSubloader(@NonNull LazyCache parent) {
    this.parent = parent;
  }
}
