package me.googas.async;

import me.googas.lazy.jsongo.async.collection.AccumulatorFutureStream;

public class FutureStreamTest {

  public static void main(String[] args) {
    AccumulatorFutureStream<String> stream = new AccumulatorFutureStream<>(null, 2);

    stream.onNext("Hello");
    stream.onNext("World");

    stream.onComplete();
  }
}
