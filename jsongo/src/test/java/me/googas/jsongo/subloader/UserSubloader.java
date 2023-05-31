package me.googas.jsongo.subloader;

import java.util.Optional;
import java.util.Random;
import lombok.NonNull;
import me.googas.jsongo.models.User;
import me.googas.jsongo.util.Randomizer;
import me.googas.lazy.jsongo.Jsongo;
import me.googas.lazy.jsongo.JsongoSubloader;
import org.bson.Document;

public class UserSubloader extends JsongoSubloader<User> {

  @NonNull private final Random random;

  public UserSubloader(@NonNull Jsongo parent) {
    super(parent, parent.getDatabase().getCollection("users"));
    this.random = new Random();
  }

  @NonNull
  public User create(@NonNull String username) {
    User user = new User(this.nextId(), username);
    this.save(new Document("_id", user.getId()), user);
    return user;
  }

  @NonNull
  private String nextId() {
    String id = Randomizer.nextString(10);
    if (this.getById(id).isPresent()) return this.nextId();
    return id;
  }

  @NonNull
  private Optional<User> getById(@NonNull String id) {
    return this.get(new Document("_id", id));
  }

  @NonNull
  public Optional<User> getByUsername(String username) {
    return this.get(new Document("username", username));
  }

  @Override
  public Class<User> getTypeClazz() {
    return User.class;
  }
}
