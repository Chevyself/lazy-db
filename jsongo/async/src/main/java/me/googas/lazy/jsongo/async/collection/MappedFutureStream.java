package me.googas.lazy.jsongo.async.collection;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import lombok.NonNull;

public class MappedFutureStream<K, V> implements FutureStream<V> {

  @NonNull private final Function<K, V> mapper;
  @NonNull private final FutureStream<V> delegate;

  public MappedFutureStream(@NonNull Function<K, V> mapper) {
    this.mapper = mapper;
    this.delegate = new AccumulatorFutureStream<>(0);
  }

  public void acceptUnmapped(K k) {
    this.delegate.onNext(k == null ? null : this.mapper.apply(k));
  }

  @Override
  public void forEach(@NonNull Consumer<V> consumer) {
    this.delegate.forEach(consumer);
  }

  @Override
  public @NonNull <R, A> CompletableFuture<R> collect(
      @NonNull Collector<? super V, A, R> collector) {
    return this.delegate.collect(collector);
  }

  @Override
  public @NonNull <T> FutureStream<T> map(@NonNull Function<V, T> mapper) {
    return this.delegate.map(mapper);
  }

  @Override
  public void onNext(V apply) {
    this.delegate.onNext(apply);
  }

  @Override
  public void onError(@NonNull Throwable throwable) {
    this.delegate.onError(throwable);
  }

  @Override
  public void onComplete() {
    this.delegate.onComplete();
  }
}
