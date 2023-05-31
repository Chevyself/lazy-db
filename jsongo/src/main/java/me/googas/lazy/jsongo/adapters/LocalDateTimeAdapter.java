package me.googas.lazy.jsongo.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeAdapter
    implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {
  @Override
  public JsonElement serialize(
      LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.addProperty("$date", src.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    return object;
  }

  @Override
  public LocalDateTime deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject object = json.getAsJsonObject();
    return Instant.ofEpochMilli(object.get("$date").getAsLong())
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();
  }
}
