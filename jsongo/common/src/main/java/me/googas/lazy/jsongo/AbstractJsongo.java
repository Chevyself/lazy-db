package me.googas.lazy.jsongo;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.googas.lazy.Loader;
import me.googas.lazy.Subloader;
import me.googas.lazy.cache.Cache;

/**
 * Jsongo is a loader that uses <a>MongoDB</a> and {@link Gson} to deserialize objets. Mongo returns
 * 'Document that to be read require to go through each element, but it also does have the method to
 * turn into json which is used to then be read using {@link Gson#fromJson(String, Type)}.
 *
 * <p>{@link IJsongoSubloader} uses the database to get the collection a query objects.
 */
@Getter
@RequiredArgsConstructor
public abstract class AbstractJsongo<S extends IJsongoSubloader<?>> implements Loader {

  @NonNull protected final Cache cache;
  @NonNull protected final Gson gson;
  @NonNull protected final Set<S> subloaders;

  /**
   * Send a ping to the mongo server. This ping is to check that the connection has been successful
   * and there's no errors
   *
   * @return this same instance
   */
  @NonNull
  public abstract AbstractJsongo<S> ping();

  /**
   * Get an unmodifiable set of the subloaders. This set is unmodifiable to prevent the user from
   * adding subloaders after the loader has been initialized.
   *
   * @return the unmodifiable set of subloaders
   */
  @NonNull
  public Set<S> getSubloaders() {
    return Collections.unmodifiableSet(subloaders);
  }

  @Override
  public <T extends Subloader> @NonNull T getSubloader(@NonNull Class<T> clazz) {
    return this.subloaders.stream()
        .filter(subloader -> clazz.isAssignableFrom(subloader.getClass()))
        .map(clazz::cast)
        .findFirst()
        .orElseThrow(() -> new NullPointerException("Could not find subloader for " + clazz));
  }
}
