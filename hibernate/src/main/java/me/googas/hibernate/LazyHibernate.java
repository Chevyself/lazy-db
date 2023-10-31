package me.googas.hibernate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.lazy.Loader;
import me.googas.lazy.Subloader;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Getter
public class LazyHibernate implements Loader {

  @NonNull
  private final Set<HibernateSubloader> subloaders;
  @NonNull @Getter private final SessionFactory sessionFactory;

  public LazyHibernate(
      @NonNull Set<HibernateSubloader> subloaders, @NonNull SessionFactory sessionFactory) {
    this.subloaders = subloaders;
    this.sessionFactory = sessionFactory;
  }

  public static LazyHibernate.Builder using(@NonNull Configuration configuration) {
    return new LazyHibernate.Builder(configuration);
  }

  @Override
  public <S extends Subloader> @NonNull S getSubloader(@NonNull Class<S> clazz) {
    return subloaders.stream()
        .filter(subloader -> clazz.isAssignableFrom(subloader.getClass()))
        .map(clazz::cast)
        .findFirst()
        .orElseThrow(() -> new NullPointerException("Could not find subloader for " + clazz));
  }

  @Override
  public void close() {
    sessionFactory.close();
  }

  public static class Builder {

    @NonNull private final Configuration configuration;
    @NonNull private final Set<HibernateSubloaderBuilder> builders = new HashSet<>();

    public Builder(@NonNull Configuration configuration) {
      this.configuration = configuration;
    }

    public Builder add(@NonNull HibernateSubloaderBuilder... builders) {
      this.builders.addAll(Arrays.asList(builders));
      return this;
    }

    public LazyHibernate build() {
      LazyHibernate lazyHibernate =
          new LazyHibernate(new HashSet<>(), this.configuration.buildSessionFactory());
      this.builders.forEach(
          builder -> lazyHibernate.getSubloaders().add(builder.build(lazyHibernate)));
      return lazyHibernate;
    }
  }
}
