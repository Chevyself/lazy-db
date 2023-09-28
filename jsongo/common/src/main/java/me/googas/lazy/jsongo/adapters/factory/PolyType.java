package me.googas.lazy.jsongo.adapters.factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a polymorphic class. This is used to register a type in {@link MappedFactory} using
 * the method {@link MappedFactory#put(Class)}, making the class identifiable by the value of {@link
 * #value()} and {@link #aliases()}, the value of {@link #value()} is the main identifier.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PolyType {

  /**
   * The main identifier of the class.
   *
   * @return the main identifier
   */
  String value();

  /**
   * The aliases of the class.
   *
   * @return the aliases
   */
  String[] aliases() default {};
}
