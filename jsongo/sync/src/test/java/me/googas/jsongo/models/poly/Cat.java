package me.googas.jsongo.models.poly;

import lombok.NonNull;

public class Cat implements Animal {

  @NonNull private final String name;

  public Cat(@NonNull String name) {
    this.name = name;
  }

  @Override
  public void sound() {
    System.out.printf("Meow! My name is %s%n", this.name);
  }
}
