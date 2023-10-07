package me.googas.lazy.jsongo.async;

import java.util.function.Function;
import lombok.NonNull;
import org.reactivestreams.Publisher;

final class Streams {
  public static <K, V> ReactiveStreamBuilder<K, V> of(
      @NonNull Publisher<K> publisher, @NonNull Function<K, V> function) {
    return new ReactiveStreamBuilder<>(publisher, function);
  }
}
