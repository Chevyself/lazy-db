package me.googas.jsongo.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.lazy.cache.Catchable;

@Getter
public class Note implements Catchable {

  @NonNull
  @SerializedName("_ida")
  private final int ida;

  @NonNull
  @SerializedName("user")
  @Getter
  private final String userId;

  @Getter @Setter private String value;

  public Note(int ida, @NonNull String userId, String value) {
    this.ida = ida;
    this.userId = userId;
    this.value = value;
  }

  @Override
  public long getToRemove() {
    return 3000;
  }
}
