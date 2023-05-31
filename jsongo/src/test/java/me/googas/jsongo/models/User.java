package me.googas.jsongo.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class User {

  @SerializedName("_id")
  @NonNull
  @Getter
  private final String id;

  @NonNull @Getter @Setter private String username;

  public User(@NonNull String id, @NonNull String username) {
    this.id = id;
    this.username = username;
  }
}
