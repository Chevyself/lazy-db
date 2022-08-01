package me.googas.lazy.builders;

public interface Builder<T> {

  /**
   * Build the requested type object.
   *
   * @return the built object
   */
  T build();
}
