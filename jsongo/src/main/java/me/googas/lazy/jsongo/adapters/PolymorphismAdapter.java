package me.googas.lazy.jsongo.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import lombok.NonNull;

@Deprecated
public class PolymorphismAdapter<T> implements JsonDeserializer<T>, JsonSerializer<T> {

  @NonNull private final Class<T> clazz;

  public PolymorphismAdapter(@NonNull Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public T deserialize(JsonElement element, Type type, JsonDeserializationContext context)
      throws JsonParseException {
    if (element.isJsonObject()) {
      JsonObject object = element.getAsJsonObject();
      Class<?> clazz = context.deserialize(object.get("_class"), Class.class);
      try {
        return context.deserialize(object.get("value"), clazz);
      } catch (JsonParseException e) {
        throw new JsonParseException("Cannot deserialize " + element + " to " + clazz, e);
      }
    } else if (element.isJsonNull()) {
      return null;
    }
    throw new JsonParseException(
        "Cannot deserialize " + element + " to " + clazz + " as it is not a JsonObject");
  }

  @Override
  public JsonElement serialize(T t, Type type, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.add("_class", context.serialize(t.getClass()));
    object.add("value", context.serialize(t, t.getClass()));
    return object;
  }
}
