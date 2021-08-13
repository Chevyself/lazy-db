package me.googas.lazy;

import lombok.NonNull;

/** Thrown when a {@link Loader} cannot be {@link Loader#close()}. */
public class LoaderCouldNotBeClosedException extends Exception {

  /**
   * Create the exception.
   *
   * @param cause another exception to why it cannot be closed.
   */
  public LoaderCouldNotBeClosedException(@NonNull Throwable cause) {
    super(cause);
  }
}
