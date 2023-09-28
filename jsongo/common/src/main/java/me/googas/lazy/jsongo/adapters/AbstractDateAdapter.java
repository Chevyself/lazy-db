package me.googas.lazy.jsongo.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import lombok.NonNull;

/**
 * Can be extended to deserialize date related classes. The main implementations are for {@link
 * Date} in {@link DateAdapter} and {@link java.time.LocalDateTime} in {@link LocalDateTimeAdapter}.
 *
 * <p>This comes with a formatter to format and parse a date to ISO 8601 format.
 *
 * @param <T> the type of date to deserialize
 */
public abstract class AbstractDateAdapter<T> implements JsonDeserializer<T>, JsonSerializer<T> {

  protected static final DateTimeFormatter ISO_8601 =
      new DateTimeFormatterBuilder()
          .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
          .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
          .appendOffsetId()
          .toFormatter();

  /**
   * Get the date as a UTC date. This should bet the date as {@link ZoneOffset#UTC}
   *
   * @param t the date to get
   * @return the date as a UTC date
   */
  public abstract ZonedDateTime toUTC(@NonNull T t);

  /**
   * Get the date from a {@link ZonedDateTime}. This should be the date as {@link ZoneOffset#UTC}
   *
   * @param zonedDateTime the date to get
   * @return the date
   */
  @NonNull
  public abstract T fromZonedDateTime(@NonNull ZonedDateTime zonedDateTime);

  @Override
  public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.addProperty("$date", LocalDateTimeAdapter.ISO_8601.format(this.toUTC(src)));
    return object;
  }

  @Override
  public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject object = json.getAsJsonObject();
    JsonElement dateElement = object.get("$date");
    if (dateElement.isJsonPrimitive()) {
      if (dateElement.getAsJsonPrimitive().isString()) {
        String dateValue = dateElement.getAsString();
        ZonedDateTime zonedDateTime =
            LocalDateTimeAdapter.ISO_8601.parse(dateValue, ZonedDateTime::from);
        return this.fromZonedDateTime(zonedDateTime);
      } else if (dateElement.getAsJsonPrimitive().isNumber()) {
        // In older versions of MongoDB the date was stored as a number
        // This is not supported anymore but it is still here for backwards compatibility
        long dateValue = dateElement.getAsLong();
        ZonedDateTime zonedDateTime =
            ZonedDateTime.ofInstant(new Date(dateValue).toInstant(), ZoneOffset.UTC);
        return this.fromZonedDateTime(zonedDateTime);
      }
    }

    throw new JsonParseException("Date element is not a primitive");
  }
}
