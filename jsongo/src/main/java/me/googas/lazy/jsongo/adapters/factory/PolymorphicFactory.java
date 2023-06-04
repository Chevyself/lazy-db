package me.googas.lazy.jsongo.adapters.factory;

import java.io.IOException;
import lombok.NonNull;

/**
 * The must rudimentary implementation of {@link AbstractPolymorphicTypeAdapterFactory}. This will
 * use the class name as the identifier.
 *
 * @param <O> the base type of the polymorphic objects
 */
public class PolymorphicFactory<O> extends AbstractPolymorphicTypeAdapterFactory<O> {

  /**
   * Create the type adapter factory.
   *
   * @param clazz the base type of the polymorphic objects
   */
  public PolymorphicFactory(@NonNull Class<O> clazz) {
    super(clazz);
  }

  @Override
  public @NonNull String getIdentifier(@NonNull Class<?> aClass) {
    return aClass.getName();
  }

  @SuppressWarnings("unchecked")
  @Override
  public @NonNull Class<? extends O> getClass(@NonNull String identifier) throws IOException {
    try {
      return (Class<? extends O>) Class.forName(identifier);
    } catch (ClassNotFoundException e) {
      throw new IOException("Could not find class " + identifier, e);
    }
  }

  @Override
  public @NonNull String getIdentifierKey() {
    return "_class";
  }
}
