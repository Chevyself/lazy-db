package me.googas.reminders;

import java.util.Optional;
import lombok.NonNull;
import me.googas.lazy.Subloader;

public interface UserSubloader extends Subloader {
  @NonNull
  Optional<? extends User> getUser(int id);

  @NonNull
  User createUser(@NonNull String name);
}
