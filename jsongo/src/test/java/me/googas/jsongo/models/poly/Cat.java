package me.googas.jsongo.models.poly;

public class Cat implements Animal {
  @Override
  public void sound() {
    System.out.println("Meow");
  }
}
