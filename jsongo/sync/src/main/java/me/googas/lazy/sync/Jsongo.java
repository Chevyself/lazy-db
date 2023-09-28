package me.googas.lazy.sync;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.lang.reflect.Type;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.lazy.cache.Cache;
import me.googas.lazy.jsongo.AbstractJsongo;
import org.bson.Document;

/**
 * Jsongo is a loader that uses <a>MongoDB</a> and {@link Gson} to deserialize objets. Mongo returns
 * {@link Document} that to be read require to go through each element, but it also does have the
 * method {@link Document#toJson()} which is used to then be read using {@link Gson#fromJson(String,
 * Type)}.
 *
 * <p>{@link JsongoSubloader} uses the database to get the collection a query objects.
 */
@Getter
public class Jsongo extends AbstractJsongo<JsongoSubloader<?>> {

  @NonNull private final MongoClient client;
  @NonNull @Getter private final MongoDatabase database;

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
    super(cache, gson, subloaders);
    this.client = client;
    this.database = database;
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

  void addSubloader(@NonNull JsongoSubloader<?> builtSubloader) {
    this.subloaders.add(builtSubloader);
  }

  /**
   * Start a copy of this loader. This copy will have the same subloaders, the cache and gson may be
   * edited
   *
   * @return the copy
   */
  @NonNull
  public JsongoCopy copy() {
    return new JsongoCopy(this);
  }

  @Override
  public void close() {
    cache.close();
    client.close();
  }

  @Override
  public @NonNull Jsongo ping() {
    this.database.runCommand(Document.parse("{serverStatus: 1}"));
    return this;
  }
}
