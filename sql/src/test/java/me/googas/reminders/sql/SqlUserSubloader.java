package me.googas.reminders.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import lombok.NonNull;
import me.googas.lazy.sql.LazySQL;
import me.googas.lazy.sql.LazySQLSubloader;
import me.googas.lazy.sql.LazySQLSubloaderBuilder;
import me.googas.reminders.User;
import me.googas.reminders.UserSubloader;

public class SqlUserSubloader extends LazySQLSubloader implements UserSubloader {

  /**
   * Start the subloader.
   *
   * @param parent the sql parent
   */
  protected SqlUserSubloader(@NonNull LazySQL parent) {
    super(parent);
  }

  @Override
  public @NonNull Optional<SqlUser> getUser(int id) {
    return this.statementWithKey("users.get-user")
        .execute(
            statement -> {
              statement.setInt(1, id);
              ResultSet resultSet = statement.executeQuery();
              if (resultSet.next()) {
                return new SqlUser(id, resultSet.getString("name"));
              }
              return null;
            });
  }

  @Override
  public @NonNull User createUser(@NonNull String name) {
    SqlUser user = new SqlUser(-1, name);
    this.statementWithKey("users.create", Statement.RETURN_GENERATED_KEYS)
        .execute(
            statement -> {
              statement.setString(1, name);
              statement.executeUpdate();
              this.parent.getSchema().updateId(statement, user);
              return null;
            });
    return user;
  }

  @Override
  public @NonNull SqlUserSubloader createTable() throws SQLException {
    this.statementWithKey("users.create-table").execute(PreparedStatement::execute);
    return this;
  }

  public static class Builder implements LazySQLSubloaderBuilder {

    @Override
    public LazySQLSubloader build(@NonNull LazySQL parent) {
      return new SqlUserSubloader(parent);
    }
  }
}
