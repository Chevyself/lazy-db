package me.googas.lazy.jsongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.NonNull;
import me.googas.lazy.Loader;
import me.googas.lazy.Subloader;
import me.googas.lazy.cache.Cache;
import me.googas.lazy.cache.MemoryCache;
import me.googas.lazy.jsongo.adapters.ClassAdapter;
import me.googas.lazy.jsongo.adapters.DateAdapter;
import me.googas.lazy.jsongo.adapters.LocalDateTimeAdapter;
import me.googas.lazy.jsongo.adapters.LongAdapter;
import me.googas.lazy.jsongo.adapters.ObjectIdAdapter;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Jsongo is a loader that uses <a>MongoDB</a> and {@link Gson} to deserialize objets. Mongo returns
 * {@link Document} that to be read require to go through each element, but it also does have the
 * method {@link Document#toJson()} which is used to then be read using {@link Gson#fromJson(String,
 * Type)}.
 *
 * <p>{@link JsongoSubloader} uses the database to get the collection a query objects.
 */
public class Jsongo implements Loader {

  @NonNull @Getter private final MongoClient client;
  @NonNull @Getter private final MongoDatabase database;
  @NonNull @Getter private final Cache cache;
  @NonNull @Getter private final Gson gson;

  @NonNull private final Set<JsongoSubloader<?>> subloaders;

  /**
   * Create the loader.
   *
   * @param client the mongo client for the connection
   * @param database the database that subloaders will use for querying
   * @param gson the gson instance for de/serializing objects
   * @param cache cache instance to prevent objects from being always loading from the database
   * @param subloaders the subloaders available for querying
   */
  protected Jsongo(
      @NonNull MongoClient client,
      @NonNull MongoDatabase database,
      @NonNull Gson gson,
      @NonNull Cache cache,
      @NonNull Set<JsongoSubloader<?>> subloaders) {
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
   * Get an unmodifiable set of the subloaders. This set is unmodifiable to prevent the user from
   * adding subloaders after the loader has been initialized, to add subloaders use {@link
   * Jsongo#copy}.
   *
   * @return the unmodifiable set of subloaders
   */
  @NonNull
  public Set<JsongoSubloader<?>> getSubloaders() {
    return Collections.unmodifiableSet(subloaders);
  }

  @Override
  public <S extends Subloader> @NonNull S getSubloader(@NonNull Class<S> clazz) {
    return this.subloaders.stream()
        .filter(subloader -> clazz.isAssignableFrom(subloader.getClass()))
        .map(clazz::cast)
        .findFirst()
        .orElseThrow(() -> new NullPointerException("Could not find subloader for " + clazz));
  }

  /**
   * Start a copy of this loader. This copy will have the same subloaders, the cache and gson may be
   * edited
   *
   * @return the copy
   */
  @NonNull
  public Copy copy() {
    return new Copy(this);
  }

  @Override
  public void close() {
    cache.close();
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
  public static Builder join(@NonNull String uri, @NonNull String database) {
    return new Builder(uri, database);
  }

  /** This builder is used to build a {@link Jsongo} instance in a neat way. */
  public static class Builder extends Configuration {

    @NonNull private final String uri;
    @NonNull private final String database;
    private int timeout;
    private boolean ssl;
    private boolean ping;

    private Builder(@NonNull String uri, @NonNull String database) {
      super(new ArrayList<>());
      this.uri = uri;
      this.database = database;
      this.timeout = 300;
      this.ssl = false;
    }

    @Override
    public @NonNull Builder setGson(@NonNull GsonBuilder gson) {
      return (Builder) super.setGson(gson);
    }

    @Override
    public @NonNull Builder setCache(@NonNull Cache cache) {
      return (Builder) super.setCache(cache);
    }

    @Override
    public @NonNull Builder add(@NonNull JsongoSubloaderBuilder... builders) {
      return (Builder) super.add(builders);
    }

    /**
     * Set whether the client should use ssl.
     *
     * @param ssl whether the client should use ssl
     * @return this same instance
     */
    @NonNull
    public Builder setSsl(boolean ssl) {
      this.ssl = ssl;
      return this;
    }

    /**
     * Set whether to ping the client on initialization.
     *
     * @param ping whether to ping the client
     * @return this same instance
     */
    @NonNull
    public Builder setPing(boolean ping) {
      this.ping = ping;
      return this;
    }

    /**
     * Set the max time to wait until the client responds.
     *
     * @param timeout the maximum time to wait in millis
     * @return this same instance
     */
    @NonNull
    public Builder timeout(int timeout) {
      this.timeout = timeout;
      return this;
    }

    @Override
    public @NonNull Jsongo build() {
      ConnectionString connectionString = new ConnectionString(this.uri);
      MongoClientSettings.Builder settings =
          MongoClientSettings.builder()
              .applyConnectionString(connectionString)
              .applyToSslSettings(builder -> builder.enabled(this.ssl))
              .applyToConnectionPoolSettings(
                  builder -> builder.maxWaitTime(this.timeout, TimeUnit.MILLISECONDS));
      MongoClient client = MongoClients.create(settings.build());
      Jsongo jsongo =
          new Jsongo(
              client,
              client.getDatabase(this.database),
              this.configureGson(),
              this.cache == null ? new MemoryCache() : this.cache,
              new HashSet<>());
      if (ping) {
        jsongo.ping();
      }
      this.configureSubloadersBuilder(jsongo);
      return jsongo;
    }
  }

  /** This builder is used to build a {@link Jsongo} instance in a neat way. */
  public static class Copy extends Configuration {

    @NonNull private final Jsongo base;

    private Copy(@NonNull Jsongo base) {
      super(new ArrayList<>());
      this.base = base;
    }

    @Override
    public @NonNull Configuration setGson(@NonNull GsonBuilder gson) {
      return super.setGson(gson);
    }

    @Override
    public @NonNull Configuration setCache(@NonNull Cache cache) {
      return super.setCache(cache);
    }

    @Override
    public @NonNull Configuration add(@NonNull JsongoSubloaderBuilder... builders) {
      return super.add(builders);
    }

    @Override
    public Jsongo build() {
      Jsongo built =
          new Jsongo(
              this.base.client,
              this.base.database,
              this.configureGson(),
              this.cache == null ? this.base.cache : this.cache,
              Collections.synchronizedSet(new HashSet<>(this.base.subloaders)));
      return this.configureSubloadersBuilder(built);
    }
  }

  private abstract static class Configuration implements me.googas.lazy.builders.Builder<Jsongo> {

    @NonNull @Getter private final List<JsongoSubloaderBuilder> subloaders;
    @NonNull private GsonBuilder gson;
    protected Cache cache;

    private Configuration(@NonNull List<JsongoSubloaderBuilder> subloaders) {
      this.subloaders = subloaders;
      this.gson = new GsonBuilder();
      this.cache = null;
    }

    @NonNull
    protected Jsongo configureSubloadersBuilder(@NonNull Jsongo built) {
      this.subloaders.stream().map(builder -> builder.build(built)).forEach(built.subloaders::add);
      return built;
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
    public Configuration setGson(@NonNull GsonBuilder gson) {
      this.gson = gson;
      return this;
    }

    /**
     * Set the cache instance. Cache won't be initialized automatically, and it must be running
     * already
     *
     * @param cache the new cache instance
     * @return this same instance
     */
    @NonNull
    public Configuration setCache(@NonNull Cache cache) {
      this.cache = cache;
      return this;
    }

    /**
     * Add subloader builds for jsongo.
     *
     * @param builders the builders to be added
     * @return this same instance
     */
    @NonNull
    public Configuration add(@NonNull JsongoSubloaderBuilder... builders) {
      this.subloaders.addAll(Arrays.asList(builders));
      return this;
    }

    @NonNull
    protected Gson configureGson() {
      return this.gson
          .registerTypeAdapter(Class.class, new ClassAdapter())
          .registerTypeAdapter(Date.class, new DateAdapter())
          .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
          .registerTypeAdapter(Long.class, new LongAdapter())
          .registerTypeAdapter(long.class, new LongAdapter())
          .registerTypeAdapter(ObjectId.class, new ObjectIdAdapter())
          .create();
    }
  }
}
