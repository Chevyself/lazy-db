package me.googas.lazy.jsongo.adapters.factory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import lombok.NonNull;

/**
 * Extend this class to create a polymorhic type adapter factory. This will allow you to serialize
 * and deserialize polymorphic objects. This is useful when you have an interface or abstract class
 * that has multiple implementations.
 *
 * <p>For example, if you have an interface called {@code Animal} and two implementations {@code
 * Dog} and {@code Cat}, you can use this class to serialize and deserialize {@code Dog} and {@code
 * Cat} objects.
 *
 * @param <O> the base type of the polymorphic objects
 */
public abstract class AbstractPolymorphicTypeAdapterFactory<O> implements TypeAdapterFactory {

  @NonNull private final Class<O> clazz;

  /**
   * Create the type adapter factory.
   *
   * @param clazz the base type of the polymorphic objects
   */
  protected AbstractPolymorphicTypeAdapterFactory(@NonNull Class<O> clazz) {
    this.clazz = clazz;
  }

  /**
   * Get how a class can be identified. This is used in serialization, to store a way to know which
   * class to deserialize
   *
   * @param aClass the class to get the identifier from
   * @return the identifier of the class
   * @throws IOException if the identifier could not be retrieved
   */
  @NonNull
  public abstract String getIdentifier(@NonNull Class<?> aClass) throws IOException;

  /**
   * Get a class by its identifier. This is used in deserialization, to know which class to
   * deserialize.
   *
   * @see #getIdentifier(Class)
   * @param identifier the identifier of the class
   * @return the class
   * @throws IOException if the class could not be retrieved
   */
  @NonNull
  public abstract Class<? extends O> getClass(@NonNull String identifier) throws IOException;

  /**
   * Get the key that is used to store the identifier in the json object. For example, if the
   * identifier is stored in the json object as {@code {"_type": "dog", "value": {}}}, the key would
   * be {@code "_type"}.
   *
   * @see #getIdentifier(Class)
   * @return the key that is used to store the identifier in the json object
   */
  @NonNull
  public String getIdentifierKey() {
    return "_type";
  }

  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    Class<? super T> raw = type.getRawType();
    if (this.clazz.isAssignableFrom(raw)) {
      return new PolymorphicAdapter<>(gson);
    }
    return null;
  }

  /**
   * Implementation for the polymorphic type adapter.
   *
   * @param <T> the type of the polymorphic object
   */
  private class PolymorphicAdapter<T> extends TypeAdapter<T> {
    @NonNull private final Gson gson;

    public PolymorphicAdapter(@NonNull Gson gson) {
      this.gson = gson;
    }

    @Override
    public void write(JsonWriter jsonWriter, T t) throws IOException {
      String identifier = AbstractPolymorphicTypeAdapterFactory.this.getIdentifier(t.getClass());
      @SuppressWarnings("unchecked")
      TypeAdapter<T> delegate =
          this.gson.getDelegateAdapter(
              AbstractPolymorphicTypeAdapterFactory.this,
              (TypeToken<T>) TypeToken.get(t.getClass()));
      jsonWriter
          .beginObject()
          .name(AbstractPolymorphicTypeAdapterFactory.this.getIdentifierKey())
          .value(identifier)
          .name("value");
      delegate.write(jsonWriter, t);
      jsonWriter.endObject();
    }

    @Override
    public T read(JsonReader jsonReader) throws IOException {
      JsonObject object = JsonParser.parseReader(jsonReader).getAsJsonObject();
      String identifier =
          object.get(AbstractPolymorphicTypeAdapterFactory.this.getIdentifierKey()).getAsString();
      @SuppressWarnings("unchecked")
      Class<T> actualClass =
          (Class<T>) AbstractPolymorphicTypeAdapterFactory.this.getClass(identifier);
      TypeAdapter<T> delegate =
          this.gson.getDelegateAdapter(
              AbstractPolymorphicTypeAdapterFactory.this, TypeToken.get(actualClass));
      return delegate.fromJsonTree(object.get("value"));
    }
  }
}
