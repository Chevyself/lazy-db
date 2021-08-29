package me.googas.reminders.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringJoiner;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.lazy.sql.SQLElement;
import me.googas.reminders.Reminder;
import me.googas.reminders.Testing;
import me.googas.reminders.User;
import me.googas.reminders.UserSubloader;

public class SqlReminder implements Reminder, SQLElement {

  private final int user;
  @NonNull @Getter private final String message;
  @Getter @Setter private int id;

  public SqlReminder(int id, int user, @NonNull String message) {
    this.id = id;
    this.user = user;
    this.message = message;
  }

  @NonNull
  public static SqlReminder of(@NonNull ResultSet query, @NonNull User user) throws SQLException {
    return new SqlReminder(query.getInt("id"), user.getId(), query.getString("message"));
  }

  @Override
  public @NonNull User getUser() {
    return Testing.loader
        .getSubloader(UserSubloader.class)
        .getUser(this.user)
        .orElseThrow(() -> new NullPointerException("No user in this reminder"));
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", SqlReminder.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("user=" + user)
        .add("message='" + message + "'")
        .toString();
  }
}
