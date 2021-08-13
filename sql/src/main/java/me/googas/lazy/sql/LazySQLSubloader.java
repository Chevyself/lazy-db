package me.googas.lazy.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.NonNull;
import me.googas.starbox.Strings;

/** Manages a sql table. Children of the loader {@link LazySQL} */
public abstract class LazySQLSubloader {
  @NonNull protected final LazySQL parent;

  /**
   * Start the subloader.
   *
   * @param parent the sql parent
   */
  protected LazySQLSubloader(@NonNull LazySQL parent) {
    this.parent = parent;
  }

  /**
   * Starts this subloader by creating its table.
   *
   * @return this same instance
   * @throws SQLException in case creating the table or something else fails
   */
  @NonNull
  public abstract LazySQLSubloader createTable() throws SQLException;

  /**
   * Prepare an statement for the parameter sql.
   *
   * @param sql the sql to prepare
   * @return the prepared statement
   * @throws SQLException in case the sql cannot be prepared
   */
  @NonNull
  public PreparedStatement statementOf(@NonNull String sql) throws SQLException {
    return this.parent.getConnection().prepareStatement(sql);
  }

  /**
   * Prepare an statement for the parameter sql and {@link java.sql.Statement}.
   *
   * @param sql the sql to prepare
   * @param statement the {@link java.sql.Statement} to use
   * @return the prepared statement
   * @throws SQLException in case the sql cannot be prepared
   */
  @NonNull
  public PreparedStatement statementOf(@NonNull String sql, int statement) throws SQLException {
    return this.parent.getConnection().prepareStatement(sql, statement);
  }

  /**
   * Prepare an statement for the parameter sql and {@link java.sql.Statement} and format the sql
   * with the objects.
   *
   * @param sql the sql to prepare
   * @param statement the {@link java.sql.Statement} to use
   * @param objects the objects to format the string
   * @return the prepared statement
   * @throws SQLException in case the sql cannot be prepared
   */
  @NonNull
  public PreparedStatement statementOf(@NonNull String sql, int statement, Object... objects)
      throws SQLException {
    return this.parent.getConnection().prepareStatement(Strings.format(sql, objects), statement);
  }

  /**
   * Prepare an statement for the parameter sql adn format the sql with the objects. This is named
   * 'formatStatement' to not clash with {@link #statementOf(String, int, Object...)}
   *
   * @param sql the sql to prepare
   * @param objects the objects to format the string
   * @return the prepared statement
   * @throws SQLException in case the sql cannot be prepared
   */
  @NonNull
  public PreparedStatement formatStatement(@NonNull String sql, Object... objects)
      throws SQLException {
    return this.parent.getConnection().prepareStatement(Strings.format(sql, objects));
  }
}
