package me.googas.lazy.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import lombok.NonNull;

/**
 * This wraps a {@link PreparedStatement} to execute and when finish easily close it and return the
 * connection to the {@link LazySQL} pool.
 */
public class LazyStatement {

  @NonNull private final LazySQL parent;
  private final int statement;
  @NonNull private final String sql;

  private LazyStatement(@NonNull LazySQL parent, @NonNull String sql, int statement) {
    this.parent = parent;
    this.sql = sql;
    this.statement = statement;
  }

  /**
   * Start the wrapper.
   *
   * @param parent the parent instance that provides the connection pool
   * @param sql the sql statement
   * @param statement the type of {@link java.sql.Statement}
   * @return the wrapper
   */
  @NonNull
  public static LazyStatement start(@NonNull LazySQL parent, @NonNull String sql, int statement) {
    return new LazyStatement(parent, sql, statement);
  }

  /**
   * Start the wrapper.
   *
   * @param parent the parent instance that provides the connection pool
   * @param sql the sql statement
   * @return the wrapper
   */
  @NonNull
  public static LazyStatement start(@NonNull LazySQL parent, @NonNull String sql) {
    return new LazyStatement(parent, sql, 0);
  }

  /**
   * Execute the statement.
   *
   * @param supplier the supplier of the object to get from the statement execution
   * @param <O> the type of the object from the supplier
   * @return an {@link Optional} holding the nullable object
   */
  @NonNull
  public <O> Optional<O> execute(@NonNull LazyStatement.StatementSupplier<O> supplier) {
    PreparedStatement preparedStatement = null;
    Connection connection = null;
    O o = null;
    try {
      connection = this.parent.getConnection();
      if (statement != 0) {
        preparedStatement = connection.prepareStatement(sql, statement);
      } else {
        preparedStatement = connection.prepareStatement(sql);
      }
      o = supplier.accept(preparedStatement);
    } catch (SQLException e) {
      this.parent.getHandler().accept(e);
    } finally {
      this.parent.releaseQuietly(preparedStatement);
      this.parent.releaseQuietly(connection);
    }
    return Optional.ofNullable(o);
  }

  /**
   * This {@link java.util.function.Supplier} type of interface is used to execute the {@link
   * PreparedStatement} and return an object from it.
   *
   * @param <O> the type of object to supply
   */
  public interface StatementSupplier<O> {
    /**
     * Accept an statement to provide an object.
     *
     * @param statement the statement to accept and get the object from
     * @return the nullable object provided from the statement
     * @throws SQLException if any kind of sql operation goes wrong
     */
    O accept(@NonNull PreparedStatement statement) throws SQLException;
  }
}
