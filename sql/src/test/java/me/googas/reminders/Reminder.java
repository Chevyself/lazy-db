package me.googas.reminders;

import lombok.NonNull;

public interface Reminder {

  int getId();

  @NonNull
  User getUser();

  @NonNull
  String getMessage();
}
