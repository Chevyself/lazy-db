package me.googas.lazy.jsongo.query;

import lombok.NonNull;

public interface ElementIdSupplier {
  @NonNull
  static ElementIdSupplier getSupplier(@NonNull Object element) {
    if (element instanceof JsongoElement) {
      return new JsongoElementIdSupplier();
    } else {
      return ReflectiveElementIdSupplier.from(element);
    }
  }

  @NonNull
  Object getId(@NonNull Object element);
}
