package me.googas.jsongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.googas.jsongo.models.poly.Animal;
import me.googas.jsongo.models.poly.Cat;
import me.googas.jsongo.models.poly.Dog;
import me.googas.lazy.jsongo.adapters.ClassAdapter;
import me.googas.lazy.jsongo.adapters.factory.MappedFactory;
import org.junit.jupiter.api.Test;

public class PolymorphismAdapterTest {

  @Test
  public void test() {
    Gson gson =
        new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Class.class, new ClassAdapter())
            // .registerTypeAdapterFactory(new PolymorphicTypeAdapterFactory(Animal.class))

            .registerTypeAdapterFactory(
                new MappedFactory<>(Animal.class).put("doggy", Dog.class).put("kitty", Cat.class))
            // .registerTypeAdapterFactory(new PolymorphicFactory<>(Animal.class))
            .create();
    Animal dog = new Dog("Googas");
    Animal cat = new Cat("Guido");
    String dogJson = gson.toJson(dog);
    String catJson = gson.toJson(cat);
    System.out.println(dogJson);
    System.out.println(catJson);
    Animal dogFromJson = gson.fromJson(dogJson, Dog.class);
    Animal catFromJson = gson.fromJson(catJson, Cat.class);
    // Dog dog2 = gson.fromJson("{_type=\"doggy\", value={}}", Dog.class);
    dogFromJson.sound();
    catFromJson.sound();
    // System.out.println(dog2);
  }
}
