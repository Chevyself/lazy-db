package me.googas.async;

import java.util.stream.Collectors;
import me.googas.lazy.cache.MemoryCache;
import me.googas.lazy.jsongo.async.Jsongo;

public class Test {

  public static void main(String[] args) {
    Jsongo jsongo =
        Jsongo.join(
                "mongodb+srv://Chevy:Amesias123@googas.zarzxbz.mongodb.net/?retryWrites=true&w=majority",
                "junit")
            .timeout(3000)
            .setSsl(true)
            .setPing(true)
            .add(UserSubloader::new, NotesSubloader::new)
            .build();

    NotesSubloader notes = jsongo.getSubloader(NotesSubloader.class);

    for (int i = 0; i < 5; i++) {
      System.out.println("Sending " + i);
      int finalI = i;
      notes
          .getAll()
          .collect(Collectors.toList())
          .whenComplete(
              (list, t) -> {
                if (t != null) t.printStackTrace();
                System.out.println("Completed " + finalI);
                System.out.println(list.size());
                System.out.println("Done");
                System.out.println("Cache size: " + ((MemoryCache) jsongo.getCache()).size());
              });
    }
  }
}
