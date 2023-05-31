package me.googas.jsongo.models.poly;

public class Dog implements Animal {
  public Dog() {}

  @Override
  public void sound() {
    System.out.println("Woof");
  }
}
