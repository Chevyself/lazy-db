package me.googas.lazy.jsongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.lazy.Loader;
import me.googas.lazy.Subloader;
import me.googas.lazy.builders.Builder;
import me.googas.lazy.cache.Cache;
import me.googas.lazy.cache.MemoryCache;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Jsongo is a loader that uses <a>MongoDB</a> and {@link Gson} to deserialize objets. Mongo returns
 * {@link Document} that to be read require to go thru each element, but it also does have the
 * method {@link Document#toJson()} which is used to then be read using {@link Gson#fromJson(String,
 * Type)}.
 *
 * <p>{@link JsongoSubloader} uses the database to get the collection an query objects.
 */
public class Jsongo implements Loader {

  @NonNull @Getter private final MongoClient client;
  @NonNull @Getter private final MongoDatabase database;
  @NonNull @Getter private final Set<JsongoSubloader<?>> subloaders;
  @NonNull @Getter private final Gson gson;
  @NonNull @Getter private final Cache cache;

  /**
   * Create the loader.
   *
   * @param client the mongo client for the connection
   * @param database the database that subloaders will use for querying
   * @param subloaders the subloaders available for querying
   * @param gson the gson instance for de/serializing objects
   * @param cache cache instance to prevent objects from being always loading from the database
   */
  protected Jsongo(
      @NonNull MongoClient client,
      @NonNull MongoDatabase database,
      @NonNull Set<JsongoSubloader<?>> subloaders,
      @NonNull Gson gson,
      @NonNull Cache cache) {
    this.client = client;
    this.database = database;
    this.subloaders = subloaders;
    this.gson = gson;
    this.cache = cache;
  }

  /**
   * Send a ping to the mongo server. This ping is to check that the connection has been successful
   * and there's no errors
   *
   * @return this same instance
   */
  @NonNull
  public Jsongo ping() {
    this.database.runCommand(Document.parse("{serverStatus: 1}"));
    return this;
  }

  /**
   * Add subloaders to the loader.
   *
   * @param subloaders the subloaders to add
   */
  public void addSubloaders(@NonNull JsongoSubloader<?>... subloaders) {
    for (JsongoSubloader<?> subloader : subloaders) {
      this.addSubloader(subloader);
    }
  }

  /**
   * Add a subloader to the loader.
   *
   * @param subloader the subloader to add
   */
  public void addSubloader(@NonNull JsongoSubloader<?> subloader) {
    this.subloaders.add(subloader);
  }

  @Override
  public <S extends Subloader> @NonNull S getSubloader(@NonNull Class<S> clazz) {
    return this.subloaders.stream()
        .filter(subloader -> clazz.isAssignableFrom(subloader.getClass()))
        .map(clazz::cast)
        .findFirst()
        .orElseThrow(() -> new NullPointerException("Could not find subloader for " + clazz));
  }

  @Override
  public void close() {
    client.close();
  }

  /**
   * Start a builder.
   *
   * @param uri the uri to which the client will connect
   * @param database the database for the subloaders
   * @return the builder
   */
  @NonNull
  public static JsongoBuilder join(@NonNull String uri, @NonNull String database) {
    return new JsongoBuilder(uri, database);
  }

  /** This builder is used to build a {@link Jsongo} instance in a neat way. */
  public static class JsongoBuilder implements Builder<Jsongo> {

    @NonNull private final String uri;
    @NonNull private final String database;
    @NonNull @Getter private final List<JsongoSubloaderBuilder> subloaders;
    private int timeout;
    @NonNull private GsonBuilder gson;
    @NonNull private Cache cache;

    private JsongoBuilder(@NonNull String uri, @NonNull String database) {
      this.uri = uri;
      this.database = database;
      this.subloaders = new ArrayList<>();
      this.timeout = 300;
      this.gson = new GsonBuilder();
      this.cache = new MemoryCache();
    }

    /**
     * Add subloader builds for jsongo.
     *
     * @param builders the builders to be added
     * @return this same instance
     */
    @NonNull
    public JsongoBuilder add(@NonNull JsongoSubloaderBuilder... builders) {
      this.subloaders.addAll(Arrays.asList(builders));
      return this;
    }

    /**
     * Set the max time to wait until the client responds.
     *
     * @param timeout the maximum time to wait in millis
     * @return this same instance
     */
    @NonNull
    public JsongoBuilder timeout(int timeout) {
      this.timeout = timeout;
      return this;
    }

    /**
     * Set the {@link GsonBuilder} instance to build the {@link Gson} for the client. Even if a new
     * instance of {@link GsonBuilder} is set any kind of adapters made for {@link ObjectId} will be
     * overwritten by {@link ObjectIdAdapter} at {@link #build()}
     *
     * @param gson the new gson builder instance
     * @return this same instance
     */
    @NonNull
    public JsongoBuilder setGson(@NonNull GsonBuilder gson) {
      this.gson = gson;
      return this;
    }

    /**
     * Set the cache instance. Cache wont be initialized automatically and it must be running
     * already
     *
     * @param cache the new cache instance
     * @return this same instance
     */
    @NonNull
    public JsongoBuilder setCache(@NonNull Cache cache) {
      this.cache = cache;
      return this;
    }

    @Override
    public @NonNull Jsongo build() {
      MongoClientOptions.Builder options =
          new MongoClientOptions.Builder().connectTimeout(this.timeout).sslEnabled(true);
      MongoClientURI uri = new MongoClientURI(this.uri, options);
      MongoClient client = new MongoClient(uri);
      Jsongo jsongo =
          new Jsongo(
                  client,
                  client.getDatabase(this.database),
                  new HashSet<>(),
                  this.gson.registerTypeAdapter(ObjectId.class, new ObjectIdAdapter()).create(),
                  this.cache)
              .ping();
      this.subloaders.forEach(
          builder -> {
            JsongoSubloader<?> subloader = builder.build(jsongo);
            if (subloader != null) jsongo.getSubloaders().add(subloader);
          });
      return jsongo;
    }
  }
}
