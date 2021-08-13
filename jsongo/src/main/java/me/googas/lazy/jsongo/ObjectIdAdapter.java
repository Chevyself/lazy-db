package me.googas.lazy.jsongo;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bson.Document;
import org.bson.types.ObjectId;

/** Adapter for de/serializing {@link ObjectId} from {@link Document#toJson()}. */
public class ObjectIdAdapter implements JsonDeserializer<ObjectId>, JsonSerializer<ObjectId> {

  @Override
  public JsonElement serialize(ObjectId src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject object = new JsonObject();
    object.addProperty("$oid", src.toHexString());
    return object;
  }

  @Override
  public ObjectId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.isJsonObject()) {
      JsonObject object = json.getAsJsonObject();
      return new ObjectId(object.get("$oid").getAsString());
    }
    return null;
  }
}
