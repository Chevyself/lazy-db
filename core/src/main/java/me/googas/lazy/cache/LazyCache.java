package me.googas.lazy.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Delegate;
import me.googas.lazy.LazyEmpty;
import me.googas.lazy.Loader;
import me.googas.lazy.Subloader;
import me.googas.net.cache.Cache;
import me.googas.starbox.builders.Builder;

/**
 * A loader that finds elements inside {@link Cache}. If no elements are found the {@link #child}
 * can be used to load them
 */
public class LazyCache implements Loader {

  @NonNull @Getter @Delegate private final Cache cache;
  @NonNull @Getter private final List<Subloader> subloaders;
  @NonNull @Getter @Setter private Loader child;

  private LazyCache(
      @NonNull Cache cache, @NonNull Loader child, @NonNull List<Subloader> subloaders) {
    this.cache = cache;
    this.child = child;
    this.subloaders = subloaders;
  }

  /**
   * Create a builder using a {@link Cache} instance.
   *
   * @param cache the cache instance to use
   * @return the new builder
   */
  @NonNull
  public static CacheLoaderBuilder using(@NonNull Cache cache) {
    return new CacheLoaderBuilder(cache, new ArrayList<>(), new LazyEmpty());
  }

  @Override
  public <S extends Subloader> @NonNull S getSubloader(@NonNull Class<S> clazz) {
    return this.subloaders.stream()
        .filter(subloader -> clazz.isAssignableFrom(subloader.getClass()))
        .map(clazz::cast)
        .findFirst()
        .orElseThrow(() -> new NullPointerException("Could not find subloader for " + clazz));
  }

  @Override
  public void close() {
    // Clear cache
  }

  /** This class is used to build a {@link LazyCache}. */
  public static class CacheLoaderBuilder implements Builder<LazyCache> {

    @NonNull private final Cache cache;
    @NonNull private final List<CacheSubloaderBuilder> subloaders;
    @NonNull private Loader child;

    private CacheLoaderBuilder(
        @NonNull Cache cache,
        @NonNull List<CacheSubloaderBuilder> subloaders,
        @NonNull Loader child) {
      this.cache = cache;
      this.subloaders = subloaders;
      this.child = child;
    }

    /**
     * Add many subloader builders to use in the loader.
     *
     * @param builders the subloaders to add
     * @return this same instance
     */
    @NonNull
    public CacheLoaderBuilder add(@NonNull CacheSubloaderBuilder... builders) {
      this.subloaders.addAll(Arrays.asList(builders));
      return this;
    }

    /**
     * Set the child loader to use when the objects are not found inside the cache.
     *
     * @param child the child loader to set
     * @return this same instance
     */
    @NonNull
    public CacheLoaderBuilder setChild(@NonNull Loader child) {
      this.child = child;
      return this;
    }

    @Override
    public @NonNull LazyCache build() {
      LazyCache loader = new LazyCache(this.cache, this.child, new ArrayList<>());
      this.subloaders.forEach(
          builder -> {
            CacheSubloader subloader = builder.build(loader);
            if (subloader != null) loader.getSubloaders().add(subloader);
          });
      return loader;
    }
  }
}
