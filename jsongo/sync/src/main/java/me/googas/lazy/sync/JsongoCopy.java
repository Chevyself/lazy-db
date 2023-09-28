package me.googas.lazy.sync;

import com.google.gson.GsonBuilder;
import java.util.Collections;
import java.util.HashSet;
import lombok.NonNull;
import me.googas.lazy.cache.Cache;
import me.googas.lazy.jsongo.AbstractCopy;
import me.googas.lazy.sync.adapters.ObjectIdAdapter;
import org.bson.types.ObjectId;

public class JsongoCopy extends AbstractCopy<JsongoSubloaderBuilder, Jsongo, JsongoSubloader<?>> {
  public JsongoCopy(@NonNull Jsongo base) {
    super(base);
  }

  @Override
  protected void addSubloader(@NonNull Jsongo built, @NonNull JsongoSubloader<?> builtSubloader) {
    built.addSubloader(builtSubloader);
  }

  @Override
  public @NonNull JsongoCopy add(@NonNull JsongoSubloaderBuilder... builders) {
    return (JsongoCopy) super.add(builders);
  }

  @Override
  public @NonNull JsongoCopy setGson(@NonNull GsonBuilder gson) {
    return (JsongoCopy) super.setGson(gson);
  }

  @Override
  public @NonNull JsongoCopy setCache(@NonNull Cache cache) {
    return (JsongoCopy) super.setCache(cache);
  }

  @Override
  public void configureObjectIdAdapter(@NonNull GsonBuilder gson) {
    gson.registerTypeAdapter(ObjectId.class, new ObjectIdAdapter());
  }

  @Override
  @NonNull
  public Jsongo build() {
    Jsongo built =
        new Jsongo(
            this.base.getClient(),
            this.base.getDatabase(),
            this.configureGson(),
            this.cache == null ? this.base.getCache() : this.cache,
            Collections.synchronizedSet(new HashSet<>(this.base.getSubloaders())));
    return this.configureSubloadersBuilder(built);
  }
}
