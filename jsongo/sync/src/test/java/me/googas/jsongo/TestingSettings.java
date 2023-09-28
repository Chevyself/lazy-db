package me.googas.jsongo;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class TestingSettings {

  @NonNull private final String uri;
  @NonNull @Getter private final String database;

  public TestingSettings(@NonNull String uri, @NonNull String database) {
    this.uri = uri;
    this.database = database;
  }

  public TestingSettings() {
    this("mongodb+srv://", "junit");
  }
}
