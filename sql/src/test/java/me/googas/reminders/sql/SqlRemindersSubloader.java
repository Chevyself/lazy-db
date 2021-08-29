package me.googas.reminders.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.lazy.sql.LazySQL;
import me.googas.lazy.sql.LazySQLSubloader;
import me.googas.lazy.sql.LazySQLSubloaderBuilder;
import me.googas.reminders.Reminder;
import me.googas.reminders.RemindersSubloader;
import me.googas.reminders.User;

public class SqlRemindersSubloader extends LazySQLSubloader implements RemindersSubloader {

  /**
   * Start the subloader.
   *
   * @param parent the sql parent
   */
  protected SqlRemindersSubloader(@NonNull LazySQL parent) {
    super(parent);
  }

  @Override
  public @NonNull SqlRemindersSubloader createTable() throws SQLException {
    this.statementWithKey("reminders.create-table").execute(PreparedStatement::execute);
    return this;
  }

  @Override
  public List<SqlReminder> getReminders(@NonNull User user) {
    return this.statementWithKey("reminders.from-user")
        .execute(
            statement -> {
              statement.setInt(1, user.getId());
              List<SqlReminder> reminders = new ArrayList<>();
              ResultSet query = statement.executeQuery();
              while (query.next()) {
                reminders.add(SqlReminder.of(query, user));
              }
              return reminders;
            })
        .orElseGet(ArrayList::new);
  }

  @Override
  public @NonNull Reminder create(@NonNull User user, @NonNull String message) {
    SqlReminder reminder = new SqlReminder(-1, user.getId(), message);
    this.statementWithKey("reminders.create")
        .execute(
            statement -> {
              statement.setInt(1, user.getId());
              statement.setString(2, message);
              statement.executeUpdate();
              this.parent.getSchema().updateId(statement, reminder);
              return null;
            });
    return reminder;
  }

  public static class Builder implements LazySQLSubloaderBuilder {

    @Override
    public SqlRemindersSubloader build(@NonNull LazySQL parent) {
      return new SqlRemindersSubloader(parent);
    }
  }
}
