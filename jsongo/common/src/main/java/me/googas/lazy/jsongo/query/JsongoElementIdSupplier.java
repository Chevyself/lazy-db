package me.googas.lazy.jsongo.query;

import lombok.NonNull;

public class JsongoElementIdSupplier implements ElementIdSupplier {

  @Override
  public @NonNull Object getId(@NonNull Object element) {
    return ((JsongoElement) element).getId();
  }
}
