package me.googas.jsongo.subloader;

import java.util.List;
import lombok.NonNull;
import me.googas.jsongo.models.Note;
import me.googas.jsongo.models.User;
import me.googas.jsongo.util.Randomizer;
import me.googas.lazy.jsongo.Jsongo;
import me.googas.lazy.jsongo.JsongoSubloader;
import me.googas.lazy.jsongo.Query;

public class NoteSubloader extends JsongoSubloader<Note> {

  public NoteSubloader(@NonNull Jsongo parent) {
    super(parent, parent.getDatabase().getCollection("notes"));
  }

  @NonNull
  public Note create(@NonNull User user, @NonNull String value) {
    int id = Randomizer.nextInt();
    Note note = new Note(id, user.getId(), value);
    this.save(Query.of("{_id:#}", id), note);
    return note;
  }

  @NonNull
  public List<Note> getNotes(@NonNull User user) {
    return this.getMany(Query.empty());
  }

  @Override
  public Class<Note> getTypeClazz() {
    return Note.class;
  }
}
