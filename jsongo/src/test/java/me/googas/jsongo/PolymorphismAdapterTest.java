package me.googas.jsongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.googas.jsongo.models.poly.Animal;
import me.googas.jsongo.models.poly.Cat;
import me.googas.jsongo.models.poly.Dog;
import me.googas.lazy.jsongo.adapters.ClassAdapter;
import me.googas.lazy.jsongo.adapters.PolymorphismAdapter;
import org.junit.jupiter.api.Test;

public class PolymorphismAdapterTest {

  @Test
  public void test() {
    Gson gson =
        new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Class.class, new ClassAdapter())
            .registerTypeAdapter(Animal.class, new PolymorphismAdapter<>(Animal.class))
            .create();
    Animal dog = new Dog();
    Animal cat = new Cat();
    String dogJson = gson.toJson(dog, Animal.class);
    String catJson = gson.toJson(cat, Animal.class);
    System.out.println(dogJson);
    System.out.println(catJson);
    Animal dogFromJson = gson.fromJson(dogJson, Animal.class);
    Animal catFromJson = gson.fromJson(catJson, Animal.class);
    Dog dog2 = gson.fromJson("{}", Dog.class);
    dogFromJson.sound();
    catFromJson.sound();
    System.out.println(dog2);
  }
}
