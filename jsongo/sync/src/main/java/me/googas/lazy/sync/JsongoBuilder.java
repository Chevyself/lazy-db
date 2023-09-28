package me.googas.lazy.sync;

import com.google.gson.GsonBuilder;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import me.googas.lazy.cache.Cache;
import me.googas.lazy.cache.MemoryCache;
import me.googas.lazy.jsongo.AbstractJsongoBuilder;
import me.googas.lazy.sync.adapters.ObjectIdAdapter;
import org.bson.types.ObjectId;

public class JsongoBuilder
    extends AbstractJsongoBuilder<JsongoSubloaderBuilder, Jsongo, JsongoSubloader<?>> {

  protected JsongoBuilder(@NonNull String uri, @NonNull String database) {
    super(uri, database);
  }

  @Override
  public void configureObjectIdAdapter(@NonNull GsonBuilder gson) {
    gson.registerTypeAdapter(ObjectId.class, new ObjectIdAdapter());
  }

  @Override
  public @NonNull JsongoBuilder timeout(int timeout) {
    return (JsongoBuilder) super.timeout(timeout);
  }

  @Override
  public @NonNull JsongoBuilder setSsl(boolean ssl) {
    return (JsongoBuilder) super.setSsl(ssl);
  }

  @Override
  public @NonNull JsongoBuilder setPing(boolean ping) {
    return (JsongoBuilder) super.setPing(ping);
  }

  @Override
  public @NonNull JsongoBuilder setGson(@NonNull GsonBuilder gson) {
    return (JsongoBuilder) super.setGson(gson);
  }

  @Override
  public @NonNull JsongoBuilder setCache(@NonNull Cache cache) {
    return (JsongoBuilder) super.setCache(cache);
  }

  @Override
  protected void addSubloader(@NonNull Jsongo built, @NonNull JsongoSubloader<?> builtSubloader) {
    built.addSubloader(builtSubloader);
  }

  @Override
  public @NonNull JsongoBuilder add(@NonNull JsongoSubloaderBuilder... builders) {
    return (JsongoBuilder) super.add(builders);
  }

  @Override
  @NonNull
  public Jsongo build() {
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
    if (this.ping) {
      jsongo.ping();
    }
    this.configureSubloadersBuilder(jsongo);
    return jsongo;
  }
}