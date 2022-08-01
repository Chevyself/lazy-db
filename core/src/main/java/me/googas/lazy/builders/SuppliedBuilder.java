package me.googas.lazy.builders;

import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.NonNull;

public interface SuppliedBuilder<T, O> {

  /**
   * Build the object.
   *
   * @param t the parameter that takes the builder to build the object
   * @return the built object
   */
  O build(@NonNull T t);

  /**
   * Build the object or get a default instance in case that {@link #build(Object)} is null.
   *
   * @param t the parameter that takes the builder to build the object
   * @param def the default instance of the type to build in case that the object built is null
   * @return the object built if it is not null else the default parameter object
   */
  @NonNull
  default O buildOr(@NonNull T t, @NonNull O def) {
    O built = this.build(t);
    return built == null ? def : built;
  }

  /**
   * Build the object or get a default instance using a {@link Supplier} in case that {@link
   * #build(Object)} is null.
   *
   * @param t the parameter that takes the builder to build the object
   * @param supplier the supplier of the default instance of the type to build in case that the
   *     object built is null
   * @return the object built if it is not null else the object given by the supplier parameter
   */
  @NonNull
  default O buildOrGet(@NonNull T t, @NonNull Supplier<O> supplier) {
    O built = this.build(t);
    return built == null ? supplier.get() : built;
  }

  /**
   * Build the object and if it is not null the consumer will accept the object.
   *
   * @param t the parameter that takes the builder to build the object
   * @param consumer the consumer which accepts the object if it is present
   * @return the object built if it is not null else the object given by the supplier parameter
   */
  default O ifBuildPresent(@NonNull T t, @NonNull Consumer<O> consumer) {
    O build = this.build(t);
    if (build != null) {
      consumer.accept(build);
    }
    return build;
  }
}
