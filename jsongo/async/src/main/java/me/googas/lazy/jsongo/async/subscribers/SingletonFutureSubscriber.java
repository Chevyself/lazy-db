package me.googas.lazy.jsongo.async.subscribers;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import lombok.NonNull;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SingletonFutureSubscriber<K, V> implements Subscriber<K> {
  @NonNull private final CompletableFuture<V> future;
  @NonNull private final Function<K, V> function;

  public SingletonFutureSubscriber(
      @NonNull CompletableFuture<V> future, @NonNull Function<K, V> function) {
    this.future = future;
    this.function = function;
  }

  @Override
  public void onSubscribe(@NonNull Subscription subscription) {
    subscription.request(1);
  }

  @Override
  public void onNext(@NonNull K k) {
    future.complete(function.apply(k));
  }

  @Override
  public void onError(@NonNull Throwable throwable) {
    future.completeExceptionally(throwable);
  }

  @Override
  public void onComplete() {
    // Do nothing
  }
}
