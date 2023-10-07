package me.googas.lazy.jsongo.async;

import com.google.gson.Gson;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.lazy.LoaderCouldNotBeClosedException;
import me.googas.lazy.cache.Cache;
import me.googas.lazy.jsongo.AbstractJsongo;
import org.bson.Document;

public class Jsongo extends AbstractJsongo<JsongoSubloader<?>> {

  @NonNull private final MongoClient client;
  @NonNull @Getter private final MongoDatabase database;

  public Jsongo(
      @NonNull MongoClient client,
      @NonNull MongoDatabase database,
      @NonNull Cache cache,
      @NonNull Gson gson,
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

  void addSubloader(JsongoSubloader<?> builtSubloader) {
    this.subloaders.add(builtSubloader);
  }

  @Override
  public @NonNull Jsongo ping() {
    Streams.of(
            this.database.runCommand(Document.parse("{ping: 1}")),
            document -> {
              System.out.println("Ping result: " + document.toJson());
              return 0;
            })
        .asSingleton()
        .join();
    return this;
  }

  @Override
  public void close() throws LoaderCouldNotBeClosedException {}
}
