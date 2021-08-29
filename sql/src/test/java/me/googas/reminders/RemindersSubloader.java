package me.googas.reminders;

import java.util.Collection;
import lombok.NonNull;
import me.googas.lazy.Subloader;

public interface RemindersSubloader extends Subloader {

  Collection<? extends Reminder> getReminders(@NonNull User user);

  @NonNull
  Reminder create(@NonNull User user, @NonNull String message);
}
