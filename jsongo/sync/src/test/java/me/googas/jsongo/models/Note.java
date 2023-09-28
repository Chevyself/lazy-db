package me.googas.jsongo.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public class Note {

  @NonNull
  @SerializedName("_id")
  private final int id;

  @NonNull
  @SerializedName("user")
  @Getter
  private final String userId;

  @Getter @Setter private String value;

  public Note(int id, @NonNull String userId, String value) {
    this.id = id;
    this.userId = userId;
    this.value = value;
  }
}
