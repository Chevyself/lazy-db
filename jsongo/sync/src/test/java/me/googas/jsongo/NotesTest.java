package me.googas.jsongo;

import me.googas.jsongo.models.User;
import me.googas.jsongo.subloader.NoteSubloader;
import me.googas.jsongo.subloader.UserSubloader;
import org.junit.jupiter.api.Test;

public class NotesTest extends BaseTest {

  @Test
  public void test() {
    NoteSubloader notes = BaseTest.jsongo.getSubloader(NoteSubloader.class);
    User user =
        BaseTest.jsongo
            .getSubloader(UserSubloader.class)
            .getByUsername("Googas")
            .orElseThrow(NullPointerException::new);
    /*
    for (int i = 0; i < 10; i++) {
        notes.create(user, "Hello world: " + i);
        System.out.println("Created note " + i);
    }
     */
    for (int i = 0; i < 5; i++) {
      long init = System.currentTimeMillis();
      System.out.println(notes.getNotes(user).size());
      System.out.println("Took " + (System.currentTimeMillis() - init) + "ms");
    }
  }
}
