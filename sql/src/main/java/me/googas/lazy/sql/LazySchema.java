package me.googas.lazy.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.Getter;
import lombok.NonNull;

/**
 * Provides an schema when a program uses different types of SQL. This attempts to solve the problem
 * when databases may have differences
 */
public class LazySchema {

  @NonNull @Getter private final Type type;
  @NonNull @Getter private final LazySchema.SchemaSupplier provider;

  /**
   * Create the schema.
   *
   * @param type the type of the schema.
   * @param provider a provider of 'sql' for the schema
   */
  public LazySchema(@NonNull Type type, @NonNull LazySchema.SchemaSupplier provider) {
    this.type = type;
    this.provider = provider;
  }

  /**
   * Update the id for an {@link SQLElement}. This must be used in statements when a object is
   * inserted and its keys are generated
   *
   * @param preparedStatement the statement where the element was inserted
   * @param element the inserted element
   * @param columnIndex where the id of the element is located
   * @throws SQLException if the generated key could not be provided by the {@link ResultSet}
   */
  public void updateId(
      @NonNull PreparedStatement preparedStatement, @NonNull SQLElement element, int columnIndex)
      throws SQLException {
    ResultSet resultSet = preparedStatement.getGeneratedKeys();
    if (resultSet.next()) {
      element.setId(resultSet.getInt(columnIndex));
    }
  }

  /**
   * Update the id for an {@link SQLElement} using '1' as the index. This must be used in statements
   * when a object is inserted and its keys are generated
   *
   * @param preparedStatement the statement where the element was inserted
   * @param element the inserted element
   * @throws SQLException if the generated key could not be provided by the {@link ResultSet}
   */
  public void updateId(@NonNull PreparedStatement preparedStatement, @NonNull SQLElement element)
      throws SQLException {
    this.updateId(preparedStatement, element, 1);
  }

  /** Represents an SQL database. */
  public enum Type {
    SQLITE("sqlite"),
    SQL("mysql"),
    H2("h2");

    @NonNull private final String driver;

    Type(@NonNull String driver) {
      this.driver = driver;
    }

    /**
     * Get the driver of the database.
     *
     * @return the driver
     */
    @NonNull
    public String getDriver() {
      return driver;
    }
  }

  /** This supplies the sql with its respective SQL. */
  public interface SchemaSupplier {
    /**
     * Get the sql for a key.
     *
     * @param key the key to match the sql
     * @return the matching sql
     */
    @NonNull
    String getSql(@NonNull String key);
  }
}
