package me.googas.jsongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.googas.lazy.jsongo.Query;
import org.junit.jupiter.api.Test;

public class QueryTest {

  @Test
  public void create() {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Query query = Query.of("{id: #, name: #}", 1, "John");
    System.out.println(query.build(gson));
    System.out.println(query.getBuilt());
  }
}
