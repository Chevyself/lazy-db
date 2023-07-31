package me.googas.jsongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;
import java.util.Date;
import me.googas.jsongo.models.User;
import me.googas.lazy.jsongo.adapters.DateAdapter;
import me.googas.lazy.jsongo.adapters.LocalDateTimeAdapter;
import me.googas.lazy.jsongo.adapters.LongAdapter;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LocalDateTimeAdapterTest {

  @Test
  public void test() {
    Gson gson =
        new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Long.class, new LongAdapter())
            .registerTypeAdapter(long.class, new LongAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Date.class, new DateAdapter())
            .create();
    System.out.println(gson.toJson(LocalDateTime.now()));
    System.out.println(gson.toJson(new Date(System.currentTimeMillis())));
  }

  @Test
  public void document() {
    Gson gson =
        new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Long.class, new LongAdapter())
            .registerTypeAdapter(long.class, new LongAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Date.class, new DateAdapter())
            .create();
    User googas = new User("1", "Googas", LocalDateTime.now());
    Document parsed = Document.parse(gson.toJson(googas));
    System.out.println(parsed);
    System.out.println(parsed.toJson());
    System.out.println(gson.fromJson(parsed.toJson(), User.class));
  }

  @Test
  public void fromString() {
    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    String json = "{\"$date\":\"2023-07-26T23:14:24.35Z\"}";
    LocalDateTime localDateTime = gson.fromJson(json, LocalDateTime.class);
    Assertions.assertEquals(json, gson.toJson(localDateTime));
    System.out.println(localDateTime);
  }
}
