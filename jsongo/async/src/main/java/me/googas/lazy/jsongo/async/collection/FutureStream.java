package me.googas.lazy.jsongo.async.collection;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import lombok.NonNull;

public interface FutureStream<T> {

  void forEach(@NonNull Consumer<T> consumer);

  @NonNull
  <R, A> CompletableFuture<R> collect(@NonNull Collector<? super T, A, R> collector);

  @NonNull
  <V> FutureStream<V> map(@NonNull Function<T, V> mapper);

  void onNext(T apply);

  default void onNext(@NonNull Collection<? extends T> collection) {
    collection.forEach(this::onNext);
  }

  void onError(@NonNull Throwable throwable);

  void onComplete();
}
