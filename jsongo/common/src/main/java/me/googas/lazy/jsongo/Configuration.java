package me.googas.lazy.jsongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import me.googas.lazy.builders.Builder;
import me.googas.lazy.cache.Cache;
import me.googas.lazy.jsongo.adapters.ClassAdapter;
import me.googas.lazy.jsongo.adapters.DateAdapter;
import me.googas.lazy.jsongo.adapters.LocalDateTimeAdapter;
import me.googas.lazy.jsongo.adapters.LongAdapter;
import me.googas.lazy.jsongo.adapters.ObjectIdAdapter;

@Getter
abstract class Configuration<
        B extends IJsongoSubloaderBuilder<T, I>,
        T extends AbstractJsongo<I>,
        I extends IJsongoSubloader<T>>
    implements Builder<T> {

  @NonNull private final List<B> subloaders;
  protected Cache cache;
  @NonNull private GsonBuilder gson;

  Configuration(@NonNull List<B> subloaders) {
    this.subloaders = subloaders;
    this.gson = new GsonBuilder();
    this.cache = null;
  }

  @NonNull
  protected T configureSubloadersBuilder(@NonNull T built) {
    this.subloaders.stream()
        .map(builder -> builder.build(built))
        .forEach(built.getSubloaders()::add);
    return built;
  }

  /**
   * Add subloader builds for jsongo.
   *
   * @param builders the builders to be added
   * @return this same instance
   */
  @SafeVarargs
  @NonNull
  public final Configuration<B, T, I> add(@NonNull B... builders) {
    this.subloaders.addAll(Arrays.asList(builders));
    return this;
  }

  public abstract void configureObjectIdAdapter(@NonNull GsonBuilder gson);

  @NonNull
  protected Gson configureGson() {
    this.gson
        .registerTypeAdapter(Class.class, new ClassAdapter())
        .registerTypeAdapter(Date.class, new DateAdapter())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .registerTypeAdapter(Long.class, new LongAdapter())
        .registerTypeAdapter(long.class, new LongAdapter());
    this.configureObjectIdAdapter(this.gson);
    return this.gson.create();
  }

  /**
   * Set the {@link GsonBuilder} instance to build the {@link Gson} for the client. Even if a new
   * instance of {@link GsonBuilder} is set any kind of adapters made for {@link ObjectId} will be
   * overwritten by {@link ObjectIdAdapter} at {@link #build()}
   *
   * @param gson the new gson builder instance
   * @return this same instance
   */
  @NonNull
  public Configuration<B, T, I> setGson(@NonNull GsonBuilder gson) {
    this.gson = gson;
    return this;
  }

  /**
   * Set the cache instance. Cache won't be initialized automatically, and it must be running
   * already
   *
   * @param cache the new cache instance
   * @return this same instance
   */
  @NonNull
  public Configuration<B, T, I> setCache(@NonNull Cache cache) {
    this.cache = cache;
    return this;
  }
}
