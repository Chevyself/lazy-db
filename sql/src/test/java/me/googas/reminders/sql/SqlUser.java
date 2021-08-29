package me.googas.reminders.sql;

import java.util.List;
import java.util.StringJoiner;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.lazy.sql.SQLElement;
import me.googas.reminders.Testing;
import me.googas.reminders.User;

public class SqlUser implements User, SQLElement {

  @NonNull @Getter private final String name;
  @Getter @Setter private int id;

  public SqlUser(int id, @NonNull String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public List<SqlReminder> getReminders() {
    return Testing.loader.getSubloader(SqlRemindersSubloader.class).getReminders(this);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", SqlUser.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("name='" + name + "'")
        .toString();
  }
}
