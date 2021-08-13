package me.googas.lazy.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.NonNull;
import me.googas.io.StarboxFile;
import me.googas.lazy.Loader;
import me.googas.lazy.LoaderCouldNotBeClosedException;
import me.googas.lazy.Subloader;
import me.googas.net.cache.Cache;
import me.googas.net.cache.MemoryCache;
import me.googas.starbox.builders.Builder;

/**
 * LazySQL is a loader that uses a SQL driver. This provides {@link LazySQLSubloader} which are
 * equipped with methods to easily prepare statements. This does also handle {@link SQLException}
 */
public class LazySQL implements Loader {

  private final String url;
  @NonNull @Getter private final List<LazySQLSubloader> subloaders;
  @NonNull @Getter private final Cache cache;
  @NonNull @Getter private final Consumer<SQLException> handler;
  private Connection connection;

  /**
   * Create the loader.
   *
   * @param url the url to start the connection
   * @param subloaders the subloaders available for querying
   * @param cache cache instance to prevent objects from being always loading from the database
   * @param handler the handler for {@link SQLException}
   * @param connection an optional already stabilised connection.
   */
  protected LazySQL(
      String url,
      @NonNull List<LazySQLSubloader> subloaders,
      @NonNull Cache cache,
      @NonNull Consumer<SQLException> handler,
      Connection connection) {
    this.url = url;
    this.subloaders = subloaders;
    this.cache = cache;
    this.handler = handler;
    this.connection = connection;
  }

  @Override
  public <S extends Subloader> @NonNull S getSubloader(@NonNull Class<S> clazz) {
    return this.subloaders.stream()
        .filter(subloader -> clazz.isAssignableFrom(subloader.getClass()))
        .map(clazz::cast)
        .findFirst()
        .orElseThrow(() -> new NullPointerException("Could not find subloader for " + clazz));
  }

  @Override
  public void close() throws LoaderCouldNotBeClosedException {
    if (this.connection != null) {
      try {
        this.connection.close();
      } catch (SQLException e) {
        throw new LoaderCouldNotBeClosedException(e);
      }
    }
  }

  /**
   * Starts the connection. If not {@link #hasConnection()} and has the {@link #url} is not null
   *
   * @return this same instance
   * @throws SQLException if {@link #hasConnection()} could not be check or {@link
   *     LazySQLSubloader#createTable()} could not be completed
   */
  @NonNull
  public LazySQL start() throws SQLException {
    if (!this.hasConnection() && url != null) {
      this.connection = DriverManager.getConnection(this.url);
      for (LazySQLSubloader subloader : this.subloaders) {
        subloader.createTable();
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
  public boolean hasConnection() throws SQLException {
    return this.connection != null && !this.connection.isClosed();
  }

  /**
   * Get the connection.
   *
   * @return the connection
   * @throws NullPointerException if there's no active connection
   */
  @NonNull
  public Connection getConnection() {
    return Objects.requireNonNull(connection);
  }

  /**
   * Start a builder.
   *
   * @param file the file to save the elements at
   * @param driver the driver to use in the file
   * @return a new builder
   */
  @NonNull
  public static LazySQLBuilder at(@NonNull StarboxFile file, @NonNull String driver) {
    return new LazySQLBuilder(
        "jdbc:" + file.getAbsoluteFile().toURI().toString().replaceFirst("^file", driver));
  }

  /**
   * Start a builder.
   *
   * @param url the url to stabilise the connection
   * @return a new builder
   */
  @NonNull
  public static LazySQLBuilder at(@NonNull String url) {
    return new LazySQLBuilder(url);
  }

  /**
   * Start a builder.
   *
   * @param connection an already initialized connection
   * @return a new builder
   */
  @NonNull
  public static LazySQLBuilder of(@NonNull Connection connection) {
    return new LazySQLBuilder(connection);
  }

  /** This builder is used to build a {@link LazySQL} instance in a neat way. */
  public static class LazySQLBuilder implements Builder<LazySQL> {

    private final String url;
    private final Connection connection;
    @NonNull private final List<LazySQLSubloaderBuilder> subloaders = new ArrayList<>();
    @NonNull private Cache cache = new MemoryCache();
    @NonNull private Consumer<SQLException> handler = Throwable::printStackTrace;

    private LazySQLBuilder(@NonNull String url) {
      this.url = url;
      this.connection = null;
    }

    private LazySQLBuilder(@NonNull Connection connection) {
      this.url = null;
      this.connection = connection;
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

    @Override
    public @NonNull LazySQL build() {
      LazySQL sql = new LazySQL(url, new ArrayList<>(), cache, handler, this.connection);
      subloaders.forEach(
          builder -> {
            LazySQLSubloader subloader = builder.build(sql);
            if (subloader != null) sql.getSubloaders().add(subloader);
          });
      return sql;
    }
  }
}
