package me.googas.lazy.jsongo.async.collection;

public interface FutureStreamOperation<T> {
  void onNext(T t);

  default void onComplete() {}
}
