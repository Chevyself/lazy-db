package me.googas.jsongo.subloader;

import java.util.List;
import lombok.NonNull;
import me.googas.jsongo.models.Note;
import me.googas.jsongo.models.User;
import me.googas.jsongo.util.Randomizer;
import me.googas.lazy.jsongo.Jsongo;
import me.googas.lazy.jsongo.JsongoSubloader;
import org.bson.Document;

public class NoteSubloader extends JsongoSubloader<Note> {

  public NoteSubloader(@NonNull Jsongo parent) {
    super(parent, parent.getDatabase().getCollection("notes"));
  }

  @NonNull
  public Note create(@NonNull User user, @NonNull String value) {
    Note note = new Note(Randomizer.nextInt(), user.getId(), value);
    this.save(new Document("_id", note.getId()), note);
    return note;
  }

  @NonNull
  public List<Note> getNotes(@NonNull User user) {
    return this.getMany(new Document());
  }

  @Override
  public Class<Note> getTypeClazz() {
    return Note.class;
  }
}
