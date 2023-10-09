package me.googas.models;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public class User {

  @SerializedName("_id")
  @NonNull
  private final String id;

  @NonNull private final LocalDateTime createdAt;
  @NonNull @Getter @Setter private String username;

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
