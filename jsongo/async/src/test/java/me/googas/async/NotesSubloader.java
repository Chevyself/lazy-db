package me.googas.async;

import lombok.NonNull;
import me.googas.lazy.jsongo.async.CatchableJsongoSubloader;
import me.googas.lazy.jsongo.async.Jsongo;
import me.googas.lazy.jsongo.async.collection.FutureStream;
import me.googas.models.Note;
import org.bson.Document;

public class NotesSubloader extends CatchableJsongoSubloader<Note> {
  protected NotesSubloader(@NonNull Jsongo parent) {
    super(parent, parent.getDatabase().getCollection("notes"));
  }

  public @NonNull FutureStream<Note> getAll() {
    return this.getMany(new Document(), note -> true);
  }

  @Override
  public @NonNull Class<Note> getTypeClazz() {
    return Note.class;
  }
}
