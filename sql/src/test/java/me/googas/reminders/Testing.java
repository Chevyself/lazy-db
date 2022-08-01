package me.googas.reminders;

import me.googas.lazy.Loader;

public class Testing {

  public static Loader loader;

  /* FIXME
  @NonNull private static final PropertiesContext context = new PropertiesContext();


  public static void main(String[] args) throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");
    Testing.loader =
        LazySQL.at(
                new StarboxFile(StarboxFile.DIR, "database.db"),
                new LazySchema(LazySchema.Type.SQLITE, PropertiesSupplier.load()))
            .add(new SqlRemindersSubloader.Builder(), new SqlUserSubloader.Builder())
            .build()
            .start();
    UserSubloader users = Testing.loader.getSubloader(UserSubloader.class);
    User foo = users.createUser("Foo");
    users
        .getUser(1)
        .ifPresent(
            user -> {
              Arrays.asList("Hello", "World", "!")
                  .forEach(
                      message -> {
                        Testing.loader.getSubloader(RemindersSubloader.class).create(user, message);
                      });
            });
    users
        .getUser(1)
        .ifPresent(
            user -> {
              System.out.println(user);
              System.out.println("user.getReminders() = " + user.getReminders());
            });
  }

  public static class PropertiesSupplier implements LazySchema.SchemaSupplier {

    @NonNull private final Properties properties;

    public PropertiesSupplier(@NonNull Properties properties) {
      this.properties = properties;
    }

    @NonNull
    public static Testing.PropertiesSupplier load() {
      return new PropertiesSupplier(
          Testing.context
              .read(Testing.class.getClassLoader().getResource("sqlite.properties"))
              .provide()
              .orElseGet(Properties::new));
    }

    @Override
    public @NonNull String getSql(@NonNull String key) {
      return Objects.requireNonNull(properties.getProperty(key));
    }
  }
     */
}
