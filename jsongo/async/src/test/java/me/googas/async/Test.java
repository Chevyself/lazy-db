package me.googas.async;

import java.util.stream.Collectors;

import me.googas.lazy.cache.MemoryCache;
import me.googas.lazy.jsongo.async.Jsongo;
import me.googas.models.User;

public class Test {

  public static void main(String[] args) {
    Jsongo jsongo =
        Jsongo.join(
                "mongodb+srv://Chevy:Amesias123@googas.zarzxbz.mongodb.net/?retryWrites=true&w=majority",
                "junit")
            .setSsl(true)
            .setPing(true)
            .add(UserSubloader::new, NotesSubloader::new)
            .build();

    UserSubloader users = jsongo.getSubloader(UserSubloader.class);
    NotesSubloader notes = jsongo.getSubloader(NotesSubloader.class);

    users
        .getUserByUsername("Googas")
        .whenComplete(
            (optional, throwable) -> {
              if (throwable != null) {
                throwable.printStackTrace();
              } else {
                System.out.println(optional.orElse(null));
              }
            });

    users
        .getAll()
        .collect(Collectors.toList())
        .whenComplete(
            (user, throwable) -> {
              if (throwable != null) {
                throwable.printStackTrace();
              } else {
                System.out.println(user);
              }
            });

    users.getAll().map(User::getUsername).forEach(System.out::println);

    notes.getAll().collect(Collectors.toList())
      .thenAccept(list -> {
        System.out.println(list.size());
        System.out.println("Done");
        System.out.println("Cache size: " + ((MemoryCache) jsongo.getCache()).size());
      });
  }
}
