package me.googas.lazy;

import lombok.NonNull;

public class LazyEmpty implements Loader {
  @Override
  public <S extends Subloader> @NonNull S getSubloader(@NonNull Class<S> clazz) {
    throw new UnsupportedOperationException("Empty loader must be replaced");
  }

  @Override
  public void close() {}
}
