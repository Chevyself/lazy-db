package me.googas.lazy.jsongo.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/** Adapter for longs in mongo format. */
public class LongAdapter implements JsonDeserializer<Long>, JsonSerializer<Long> {
  @Override
  public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.addProperty("$numberLong", src);
    return object;
  }

  @Override
  public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.isJsonObject()) {
      return json.getAsJsonObject().get("$numberLong").getAsLong();
    }
    return json.getAsLong();
  }
}
