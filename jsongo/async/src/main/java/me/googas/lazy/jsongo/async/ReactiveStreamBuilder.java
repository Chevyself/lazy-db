package me.googas.lazy.jsongo.async;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import lombok.NonNull;
import me.googas.lazy.jsongo.async.collection.AccumulatorFutureStream;
import me.googas.lazy.jsongo.async.collection.FutureStream;
import me.googas.lazy.jsongo.async.subscribers.CollectionSubscriber;
import me.googas.lazy.jsongo.async.subscribers.SingletonFutureSubscriber;
import org.reactivestreams.Publisher;

public class ReactiveStreamBuilder<K, V> {
  @NonNull private final Publisher<K> publisher;
  @NonNull private final Function<K, V> function;

  public ReactiveStreamBuilder(@NonNull Publisher<K> publisher, @NonNull Function<K, V> function) {
    this.publisher = publisher;
    this.function = function;
  }

  @NonNull
  public CompletableFuture<V> asSingleton() {
    CompletableFuture<V> future = new CompletableFuture<>();
    this.publisher.subscribe(new SingletonFutureSubscriber<>(future, function));
    return future;
  }

  public @NonNull FutureStream<V> asCollection(Collection<? extends V> initial, long limit) {
    AccumulatorFutureStream<V> stream = new AccumulatorFutureStream<>(initial, limit);
    this.publisher.subscribe(new CollectionSubscriber<>(stream, function));
    return stream;
  }

  public @NonNull FutureStream<V> asCollection() {
    return this.asCollection(null, Long.MAX_VALUE);
  }
}
