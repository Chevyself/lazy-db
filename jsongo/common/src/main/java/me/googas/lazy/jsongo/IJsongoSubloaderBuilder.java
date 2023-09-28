package me.googas.lazy.jsongo;

import lombok.NonNull;

public interface IJsongoSubloaderBuilder<
    T extends AbstractJsongo<I>, I extends IJsongoSubloader<?>> {

  /**
   * Build the subloader.
   *
   * @param jsongo the jsongo instance
   * @return the subloader
   */
  @NonNull
  I build(@NonNull T jsongo);
}
