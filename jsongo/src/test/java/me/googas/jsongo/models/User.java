package me.googas.jsongo.models;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class User {

  @SerializedName("_id")
  @NonNull
  @Getter
  private final String id;

  @NonNull @Getter @Setter private String username;
  @NonNull private final LocalDateTime createdAt;

  public User(@NonNull String id, @NonNull String username, @NonNull LocalDateTime createdAt) {
    this.id = id;
    this.username = username;
    this.createdAt = createdAt;
  }

  public User() {
    this("", "", LocalDateTime.now());
  }

  @Override
  public String toString() {
    return "User{"
        + "id='"
        + id
        + '\''
        + ", username='"
        + username
        + '\''
        + ", createdAt="
        + createdAt
        + '}';
  }
}
