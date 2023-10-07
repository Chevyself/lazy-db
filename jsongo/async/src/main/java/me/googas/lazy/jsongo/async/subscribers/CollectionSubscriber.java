package me.googas.lazy.jsongo.async.subscribers;

import java.util.function.Function;
import lombok.NonNull;
import me.googas.lazy.jsongo.async.collection.AccumulatorFutureStream;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class CollectionSubscriber<K, V> implements Subscriber<K> {
  @NonNull private final AccumulatorFutureStream<V> stream;
  @NonNull private final Function<K, V> function;

  public CollectionSubscriber(
      @NonNull AccumulatorFutureStream<V> stream, @NonNull Function<K, V> function) {
    this.stream = stream;
    this.function = function;
  }

  @Override
  public void onSubscribe(@NonNull Subscription subscription) {
    subscription.request(this.stream.getLimit());
  }

  @Override
  public void onNext(K k) {
    this.stream.onNext(k == null ? null : this.function.apply(k));
  }

  @Override
  public void onError(Throwable t) {
    this.stream.onError(t);
  }

  @Override
  public void onComplete() {
    this.stream.onComplete();
  }
}
