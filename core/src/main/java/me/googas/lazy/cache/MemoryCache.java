package me.googas.lazy.cache;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;

public class MemoryCache extends TimerTask implements CacheMap {

  /** The map required for the cache. */
  @NonNull @Delegate
  private final Map<SoftReference<Catchable>, Long> map = new ConcurrentHashMap<>();

  @NonNull @Getter private Consumer<Throwable> handler = Throwable::printStackTrace;

  /**
   * Register this cache in a {@link Timer}. This will register the timer to be run every second
   *
   * @param timer the timer to register this cache
   * @return this same instance
   */
  @NonNull
  public MemoryCache register(@NonNull Timer timer) {
    timer.schedule(this, 1000, 1000);
    return this;
  }

  @Override
  public void run() {
    CacheMap.super.run();
  }

  @Override
  public @NonNull Map<SoftReference<Catchable>, Long> getMap() {
    return this.map;
  }

  @Override
  public @NonNull CacheMap handle(@NonNull Consumer<Throwable> handler) {
    this.handler = handler;
    return this;
  }
}
