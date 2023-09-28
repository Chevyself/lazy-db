package me.googas.jsongo.models.poly;

import lombok.NonNull;

public class Dog implements Animal {

  @NonNull private final String name;

  public Dog(@NonNull String name) {
    this.name = name;
  }

  @Override
  public void sound() {
    System.out.printf("Woof! My name is %s%n", this.name);
  }
}
