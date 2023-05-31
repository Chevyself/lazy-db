package me.googas.jsongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;
import java.util.Date;
import me.googas.lazy.jsongo.adapters.DateAdapter;
import me.googas.lazy.jsongo.adapters.LocalDateTimeAdapter;
import me.googas.lazy.jsongo.adapters.LongAdapter;
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
}
