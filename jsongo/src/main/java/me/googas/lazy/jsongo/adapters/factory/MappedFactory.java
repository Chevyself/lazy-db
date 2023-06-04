package me.googas.lazy.jsongo.adapters.factory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;

/**
 * Extension of {@link AbstractPolymorphicTypeAdapterFactory} that allows to map classes to
 * identifiers.
 *
 * @param <O> the base type of the polymorphic objects
 */
public class MappedFactory<O> extends AbstractPolymorphicTypeAdapterFactory<O> {
  @NonNull private final Map<String, Class<? extends O>> identifierToClass;
  @NonNull private final Map<Class<? extends O>, String> classToIdentifier;

  /**
   * Create the type adapter factory.
   *
   * @param clazz the base type of the polymorphic objects
   */
  public MappedFactory(@NonNull Class<O> clazz) {
    super(clazz);
    this.identifierToClass = new HashMap<>();
    this.classToIdentifier = new HashMap<>();
  }

  /**
   * Put a class to be mapped to an identifier. This will also put the identifier to be mapped to
   * the class, only if the class is not already mapped to an identifier.
   *
   * @param key the identifier
   * @param clazz the class
   * @return this same instance
   */
  @NonNull
  public MappedFactory<O> put(@NonNull String key, @NonNull Class<? extends O> clazz) {
    this.identifierToClass.put(key, clazz);
    if (!classToIdentifier.containsKey(clazz)) {
      this.classToIdentifier.put(clazz, key);
    }
    return this;
  }

  /**
   * Put a class to be mapped to an identifier. This will use the annotation {@link PolyType} to get
   * the identifier and the aliases.
   *
   * @see #put(String, Class)
   * @param clazz the class
   * @return this same instance
   */
  @NonNull
  public MappedFactory<O> put(@NonNull Class<? extends O> clazz) {
    if (clazz.isAnnotationPresent(PolyType.class)) {
      PolyType annotation = clazz.getAnnotation(PolyType.class);
      this.put(annotation.value(), clazz);
      for (String alias : annotation.aliases()) {
        this.put(alias, clazz);
      }
    } else {
      throw new IllegalArgumentException(
          "Class " + clazz + " does not have the annotation @PolyType");
    }
    return this;
  }

  @NonNull
  public String getIdentifier(@NonNull Class<?> aClass) throws IOException {
    String identifier = this.classToIdentifier.get(aClass);
    if (identifier == null) {
      throw new IOException(
          "Class "
              + aClass
              + " is not registered in the mapping type adapter factory "
              + this.getClass());
    }
    return identifier;
  }

  @NonNull
  public Class<? extends O> getClass(@NonNull String identifier) {
    Class<? extends O> aClass = this.identifierToClass.get(identifier);
    if (aClass == null) {
      throw new IllegalArgumentException(
          "Identifier "
              + identifier
              + " is not registered in the mapping type adapter factory "
              + this.getClass());
    }
    return aClass;
  }
}
