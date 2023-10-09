package me.googas.lazy.jsongo.async.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import lombok.Getter;
import lombok.NonNull;

public class AccumulatorFutureStream<T> implements FutureStream<T> {

  @NonNull private final List<T> accumulator;
  @NonNull private final List<Throwable> errors;
  @Getter private final long limit;
  @Getter private boolean completed;
  private Consumer<Throwable> error;
  private FutureStreamOperation<T> operation;

  public AccumulatorFutureStream(Collection<? extends T> initial, long limit) {
    this.limit = limit;
    this.accumulator = Collections.synchronizedList(new ArrayList<>());
    this.errors = Collections.synchronizedList(new ArrayList<>());
    this.error = Throwable::printStackTrace;
    if (initial != null) this.accumulator.addAll(initial);
  }

  public void onError(@NonNull Consumer<Throwable> error) {
    this.error = error;
    this.errors.forEach(error);
    this.errors.clear();
  }

  private void consumeNext(T t) {
    this.operation.onNext(t);
  }

  private void setOperation(@NonNull FutureStreamOperation<T> operation) {
    this.operation = operation;
    this.accumulator.forEach(this::consumeNext);
    this.accumulator.clear();
    if (this.completed) this.operation.onComplete();
  }

  @Override
  public void forEach(@NonNull Consumer<T> consumer) {
    if (this.operation != null) throw new IllegalStateException("There's already an operation");
    this.setOperation(consumer::accept);
  }

  @NonNull
  public <V> FutureStream<V> map(@NonNull Function<T, V> mapper) {
    if (this.operation != null) throw new IllegalStateException("There's already an operation");
    MappedFutureStream<T, V> mapped = new MappedFutureStream<>(mapper);
    FutureStreamOperation<T> operation =
        new FutureStreamOperation<T>() {
          @Override
          public void onNext(T t) {
            mapped.acceptUnmapped(t);
          }

          @Override
          public void onComplete() {
            mapped.onComplete();
          }
        };
    this.setOperation(operation);
    return mapped;
  }

  @NonNull
  public <R, A> CompletableFuture<R> collect(@NonNull Collector<? super T, A, R> collector) {
    if (this.operation != null) throw new IllegalStateException("There's already an operation");
    CompletableFuture<R> future = new CompletableFuture<>();
    this.operation =
        new FutureStreamOperation<T>() {
          private final A container = collector.supplier().get();
          private final Function<A, R> finisher = collector.finisher();
          private final Consumer<T> accumulator = t -> collector.accumulator().accept(container, t);

          @Override
          public void onNext(T t) {
            accumulator.accept(t);
          }

          @Override
          public void onComplete() {
            future.complete(finisher.apply(container));
          }
        };
    if (!this.accumulator.isEmpty()) {
      this.accumulator.forEach(this::consumeNext);
      this.accumulator.clear();
    }
    return future;
  }

  public void onNext(T apply) {
    if (this.operation == null) {
      this.accumulator.add(apply);
      return;
    }
    this.consumeNext(apply);
  }

  public void onError(@NonNull Throwable t) {
    if (this.error == null) {
      this.errors.add(t);
      return;
    }
    this.error.accept(t);
  }

  public void onComplete() {
    if (this.completed) throw new IllegalStateException("The stream is already completed");
    if (this.operation == null) {
      this.completed = true;
      return;
    }
    this.operation.onComplete();
  }
}
