package me.googas.lazy.sync;

import com.google.gson.Gson;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NonNull;
import me.googas.lazy.builders.SuppliedBuilder;
import org.bson.Document;

/**
 * Represents a Mongo query in Mongoshell format. This takes a json as a string and replaces the ´#´
 * with the objects passed in the constructor. Objects are converted to json using Gson.
 *
 * <p>For instance:
 *
 * <pre>
 *     Query.of("{name: #}", "John").build(gson);
 *     // {name: "John"}
 *     Query.of("{name: #, age: #}", "John", 18).build(gson);
 *     // {name: "John", age: 18}
 * </pre>
 */
public class Query implements SuppliedBuilder<Gson, Document> {

  @NonNull private final Pattern pattern = Pattern.compile("#");
  @NonNull private final String json;
  @NonNull private final Object[] objects;
  @Getter private String built;

  private Query(@NonNull String json, @NonNull Object[] objects) {
    this.json = json;
    this.objects = objects;
  }

  private Query() {
    this("{}", new Object[0]);
  }

  /**
   * Create a new query.
   *
   * @param json the json to format
   * @param objects the objects to replace the json
   * @return the new query
   */
  @NonNull
  public static Query of(@NonNull String json, @NonNull Object... objects) {
    return new Query(json, objects);
  }

  /**
   * Create a new query.
   *
   * @param json the json to format
   * @return the new query
   */
  @NonNull
  public static Query of(@NonNull String json) {
    return new Query(json, new Object[0]);
  }

  /**
   * Create a new empty query. Empty queries are: <code>{}</code>
   *
   * @return the new query
   */
  @NonNull
  public static Query empty() {
    return new Query();
  }

  @Override
  public Document build(@NonNull Gson gson) {
    if (this.built == null) {
      this.built = this.json;
      Matcher matcher = pattern.matcher(json);
      int index = 0;
      while (matcher.find() && index < objects.length) {
        String group = matcher.group();
        this.built = this.built.replaceFirst(group, gson.toJson(objects[index]));
        index++;
      }
    }
    return Document.parse(this.built);
  }
}
