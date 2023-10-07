package me.googas.async;

import com.mongodb.client.model.Filters;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import me.googas.lazy.jsongo.async.Jsongo;
import me.googas.lazy.jsongo.async.JsongoSubloader;
import me.googas.lazy.jsongo.async.collection.FutureStream;
import me.googas.lazy.jsongo.query.Query;
import me.googas.models.User;

public class UserSubloader extends JsongoSubloader<User> {
  protected UserSubloader(@NonNull Jsongo parent) {
    super(parent, parent.getDatabase().getCollection("users"));
  }

  @NonNull
  public CompletableFuture<Optional<User>> getUserById(@NonNull String id) {
    return this.get(Query.of("{_id: #}", id));
  }

  @NonNull
  public CompletableFuture<Optional<User>> getUserByUsername(@NonNull String username) {
    return this.get(Query.of("{username: #}", username));
  }

  @NonNull
  public CompletableFuture<Optional<User>> getUser(@NonNull Query query) {
    return this.get(query);
  }

  @NonNull
  public FutureStream<User> getAll() {
    return this.getMany(Filters.empty());
  }

  @Override
  public @NonNull Class<User> getTypeClazz() {
    return User.class;
  }
}
