package me.googas.jsongo.subloader;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.jsongo.models.Note;
import me.googas.jsongo.models.User;
import me.googas.jsongo.util.Randomizer;
import me.googas.lazy.sync.CatchableJsongoSubloader;
import me.googas.lazy.sync.Jsongo;
import me.googas.lazy.jsongo.query.Query;

public class NoteSubloader extends CatchableJsongoSubloader<Note> {

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
    return new ArrayList<>(this.getMany(Query.empty(), note -> note.getUserId().equals(user.getId())));
  }

  @Override
  public @NonNull Class<Note> getTypeClazz() {
    return Note.class;
  }
}
