package me.googas.lazy.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import me.googas.io.StarboxFile;
import me.googas.lazy.Loader;
import me.googas.lazy.Subloader;
import me.googas.net.cache.Cache;
import me.googas.net.cache.MemoryCache;
import me.googas.starbox.builders.Builder;

/**
 * LazySQL is a loader that uses a SQL driver. This provides {@link LazySQLSubloader} which are
 * equipped with methods to easily prepare statements. This does also handle {@link SQLException}
 */
public class LazySQL implements Loader {

  @NonNull private final ConnectionSupplier supplier;
  @NonNull @Getter private final LazySchema schema;
  @NonNull @Getter private final List<LazySQLSubloader> subloaders;
  @NonNull @Getter private final Cache cache;
  @NonNull @Getter private final Consumer<SQLException> handler;
  private final int base;
  private final int max;
  @NonNull private final List<Connection> open = new ArrayList<>();
  @NonNull private final List<Connection> used = new ArrayList<>();

  /**
   * Create the loader.
   *
   * @param supplier the supplier for the connection
   * @param schema the schema of the sql connection
   * @param subloaders the subloaders available for querying
   * @param cache cache instance to prevent objects from being always loading from the database
   * @param handler the handler for {@link SQLException}
   * @param base the base connections that the pool may have
   * @param max the maximum open connections of the pool
   */
  protected LazySQL(
      @NonNull ConnectionSupplier supplier,
      @NonNull LazySchema schema,
      @NonNull List<LazySQLSubloader> subloaders,
      @NonNull Cache cache,
      @NonNull Consumer<SQLException> handler,
      int base,
      int max) {
    this.supplier = supplier;
    this.schema = schema;
    this.subloaders = subloaders;
    this.cache = cache;
    this.handler = handler;
    this.base = base;
    this.max = max;
  }

  /**
   * Start a builder.
   *
   * @param file the file to save the elements at
   * @param driver the driver to use in the file
   * @return a new builder
   */
  @NonNull
  public static LazySQLBuilder at(@NonNull StarboxFile file, @NonNull LazySchema driver) {
    return new LazySQLBuilder(
            "jdbc:"
                + file.getAbsoluteFile()
                    .toURI()
                    .toString()
                    .replaceFirst("^file", driver.getType().getDriver()))
        .setSchema(driver);
  }

  /**
   * Start a builder.
   *
   * @param url the url to stabilise the connection
   * @return a new builder
   */
  @NonNull
  public static LazySQLBuilder at(@NonNull String url) {
    return new LazySQLBuilder(url.startsWith("jdbc:") ? url : "jdbc:" + url);
  }

  @Override
  public <S extends Subloader> @NonNull S getSubloader(@NonNull Class<S> clazz) {
    return this.subloaders.stream()
        .filter(subloader -> clazz.isAssignableFrom(subloader.getClass()))
        .map(clazz::cast)
        .findFirst()
        .orElseThrow(() -> new NullPointerException("Could not find subloader for " + clazz));
  }

  void releaseQuietly(Connection connection) {
    if (connection == null) return;
    try {
      this.release(connection);
    } catch (SQLException e) {
      this.handler.accept(e);
    }
  }

  void releaseQuietly(PreparedStatement statement) {
    if (statement == null) return;
    try {
      statement.close();
    } catch (SQLException e) {
      this.handler.accept(e);
    }
  }

  /**
   * Starts the connection tables.
   *
   * @return this same instance
   * @throws SQLException if the base connections could not be open
   */
  @NonNull
  public LazySQL start() throws SQLException {
    for (int i = 0; i < base; i++) {
      this.open.add(this.supplier.supply());
    }
    for (LazySQLSubloader subloader : this.subloaders) {
      try {
        subloader.createTable();
      } catch (SQLException e) {
        this.handler.accept(e);
      }
    }
    return this;
  }

  /**
   * Check whether this connection is not null or if it is not closed.
   *
   * @return true if this has an active connection
   * @throws SQLException if it could be check that is closed {@link Connection#isClosed()}
   */
  @Deprecated
  public boolean hasConnection() throws SQLException {
    return true;
  }

  /**
   * Releases a connection back to the pool.
   *
   * @param connection the connection to release
   * @throws SQLException if the base connections are filled it will attempt to close the connection
   *     thus if the connection cannot be closed it will be thrown
   */
  public void release(@NonNull Connection connection) throws SQLException {
    if (this.used.contains(connection)) {
      this.used.remove(connection);
      if (this.open.size() < base) {
        this.open.add(connection);
      } else {
        connection.close();
      }
    } else {
      throw new IllegalArgumentException(connection + " is not in this pool");
    }
  }

  /**
   * Get a new connection.
   *
   * @return the new connection
   * @throws SQLException if the connection could no be supplied
   */
  @NonNull
  public Connection getConnection() throws SQLException {
    Connection connection;
    if (this.open.isEmpty() && this.used.size() < this.max) {
      connection = this.supplier.supply();
      this.used.add(connection);
    } else if (!this.open.isEmpty()) {
      connection = this.open.get(0);
      if (connection.isClosed()) {
        this.open.remove(connection);
        connection = this.getConnection();
      } else {
        this.used.add(connection);
        this.open.remove(connection);
      }
    } else {
      throw new SQLException("Maximum connections");
    }
    return connection;
  }

  @Override
  public void close() {
    this.used.forEach(
        connection -> {
          try {
            connection.close();
          } catch (SQLException e) {
            this.handler.accept(e);
          }
        });
    this.open.forEach(
        connection -> {
          try {
            connection.close();
          } catch (SQLException e) {
            this.handler.accept(e);
          }
        });
    this.used.clear();
    this.open.clear();
  }

  interface ConnectionSupplier {
    @NonNull
    Connection supply() throws SQLException;
  }

  /** This builder is used to build a {@link LazySQL} instance in a neat way. */
  public static class LazySQLBuilder implements Builder<LazySQL> {

    @NonNull private final ConnectionSupplier supplier;
    @NonNull private final List<LazySQLSubloaderBuilder> subloaders = new ArrayList<>();

    @NonNull
    private LazySchema schema =
        new LazySchema(
            LazySchema.Type.SQL,
            key -> {
              throw new IllegalStateException("Default lazy schema cannot provide sql");
            });

    @NonNull private Cache cache = new MemoryCache();
    @NonNull private Consumer<SQLException> handler = Throwable::printStackTrace;
    private int base = 3;
    private int max = 10;

    private LazySQLBuilder(@NonNull String url) {
      this.supplier = () -> DriverManager.getConnection(url);
    }

    /**
     * Set the base/minimum connections to have open.
     *
     * @param base the new base connections
     * @return this same instance
     */
    @NonNull
    public LazySQLBuilder setBase(int base) {
      this.base = base;
      return this;
    }

    /**
     * Set the maximum connections.
     *
     * @param max the new maximum connections
     * @return this same instance
     */
    @NonNull
    public LazySQLBuilder setMax(int max) {
      this.max = max;
      return this;
    }

    /**
     * Add subloader builds for sql.
     *
     * @param subloaders the builders to be added
     * @return this same instance
     */
    @NonNull
    public LazySQLBuilder add(@NonNull LazySQLSubloaderBuilder... subloaders) {
      this.subloaders.addAll(Arrays.asList(subloaders));
      return this;
    }

    /**
     * Set the cache instance. Cache wont be initialized automatically and it must be running
     * already
     *
     * @param cache the new cache instance
     * @return this same instance
     */
    @NonNull
    public LazySQLBuilder cache(@NonNull Cache cache) {
      this.cache = cache;
      return this;
    }

    /**
     * Set the handler for {@link SQLException}.
     *
     * @param handler the new handler
     * @return this same instance
     */
    @NonNull
    public LazySQLBuilder handle(@NonNull Consumer<SQLException> handler) {
      this.handler = handler;
      return this;
    }

    /**
     * Set the schema of the loader.
     *
     * @param schema the new schema
     * @return this same instance
     */
    @NonNull
    public LazySQLBuilder setSchema(LazySchema schema) {
      this.schema = schema;
      return this;
    }

    @Override
    public @NonNull LazySQL build() {
      LazySQL sql = new LazySQL(supplier, schema, new ArrayList<>(), cache, handler, base, max);
      subloaders.forEach(
          builder -> {
            LazySQLSubloader subloader = builder.build(sql);
            if (subloader != null) sql.getSubloaders().add(subloader);
          });
      return sql;
    }
  }
}
