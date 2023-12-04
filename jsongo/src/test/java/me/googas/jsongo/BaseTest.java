package me.googas.jsongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import lombok.NonNull;
import me.googas.jsongo.exception.TestSetupException;
import me.googas.jsongo.models.Note;
import me.googas.jsongo.models.User;
import me.googas.jsongo.subloader.NoteSubloader;
import me.googas.jsongo.subloader.UserSubloader;
import me.googas.lazy.jsongo.Jsongo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseTest {
  protected static Gson gson;
  protected static Jsongo jsongo;

  @BeforeAll
  public static void setup() {
    GsonBuilder baseBuilder = new GsonBuilder().setPrettyPrinting();
    BaseTest.gson = baseBuilder.create();
    TestingSettings settings = BaseTest.loadSettings(BaseTest.gson);
    BaseTest.jsongo =
        Jsongo.join(settings.getUri(), settings.getDatabase())
            .timeout(300)
            .setGson(baseBuilder)
            .add(NoteSubloader::new, UserSubloader::new)
            .build();
  }

  @Test
  @Order(0)
  public void setupUser() {
    UserSubloader subloader = BaseTest.jsongo.getSubloader(UserSubloader.class);
    String username = "Googas";
    subloader.getByUsername(username).orElseGet(() -> subloader.create(username));
  }

  @Test
  @Order(1)
  public void getUser() {
    UserSubloader subloader = BaseTest.jsongo.getSubloader(UserSubloader.class);
    String username = "Googas";
    User user = subloader.getByUsername(username).orElseThrow(NullPointerException::new);
    System.out.println("User has been found: " + user);
  }

  @Test
  @Order(2)
  public void saveNotes() {
    NoteSubloader subloader = BaseTest.jsongo.getSubloader(NoteSubloader.class);
    UserSubloader userSubloader = BaseTest.jsongo.getSubloader(UserSubloader.class);
    String username = "Googas";
    User user = userSubloader.getByUsername(username).orElseThrow(NullPointerException::new);
    for (int i = 0; i < 10; i++) {
      subloader.create(user, "Note " + i);
    }
  }

  @Test
  @Order(3)
  public void queryNotes() {
    NoteSubloader subloader = BaseTest.jsongo.getSubloader(NoteSubloader.class);
    UserSubloader userSubloader = BaseTest.jsongo.getSubloader(UserSubloader.class);
    String username = "Googas";
    User user = userSubloader.getByUsername(username).orElseThrow(NullPointerException::new);
    List<Note> notes = subloader.getNotes(user);
  }

  @NonNull
  private static TestingSettings loadSettings(@NonNull Gson gson) {
    File file = new File("settings.json");
    try (Reader reader = new BufferedReader(new FileReader(file))) {
      return gson.fromJson(reader, TestingSettings.class);
    } catch (FileNotFoundException e) {
      BaseTest.writeDefaultSettings(gson, file);
      throw new TestSetupException("Could not find file " + file.getAbsolutePath(), e);
    } catch (IOException e) {
      throw new TestSetupException(e);
    }
  }

  private static void writeDefaultSettings(@NonNull Gson gson, @NonNull File file) {
    try (Writer writer = new BufferedWriter(new FileWriter(file))) {
      gson.toJson(new TestingSettings(), writer);
    } catch (IOException e) {
      throw new TestSetupException("Failed to write default configuration", e);
    }
  }
}
