package me.googas.models;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.lazy.cache.Catchable;

@Getter
public class Note implements Catchable {

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

  @Override
  public long getToRemove() {
    return 3000;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    Note note = (Note) o;
    return id == note.id && Objects.equals(userId, note.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId);
  }

  @Override
  public String toString() {
    return "Note{" + "id=" + id + ", userId='" + userId + '\'' + ", value='" + value + '\'' + '}';
  }
}
