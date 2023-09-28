package me.googas.lazy.jsongo;

import java.util.ArrayList;
import lombok.NonNull;

/** This builder is used to build a {@link AbstractJsongo} instance in a neat way. */
public abstract class AbstractCopy<
        B extends IJsongoSubloaderBuilder<T, I>,
        T extends AbstractJsongo<I>,
        I extends IJsongoSubloader<?>>
    extends Configuration<B, T, I> {

  @NonNull protected final T base;

  protected AbstractCopy(@NonNull T base) {
    super(new ArrayList<>());
    this.base = base;
  }
}
