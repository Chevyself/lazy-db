package me.googas.lazy.jsongo.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 * Json adapter for {@link Class}. This will serialize a class to its name and deserialize it from
 * its name using {@link Class#forName(String)}.
 */
public class ClassAdapter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {
  @Override
  public JsonElement serialize(Class<?> clazz, Type type, JsonSerializationContext context) {
    return context.serialize(clazz.getName());
  }

  @Override
  public Class<?> deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext context)
      throws JsonParseException {
    try {
      return Class.forName(jsonElement.getAsString());
    } catch (ClassNotFoundException e) {
      throw new JsonParseException(e);
    }
  }
}
