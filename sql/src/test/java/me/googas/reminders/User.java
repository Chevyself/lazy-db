package me.googas.reminders;

import java.util.Collection;
import lombok.NonNull;

public interface User {

  int getId();

  @NonNull
  String getName();

  @NonNull
  Collection<? extends Reminder> getReminders();
}
