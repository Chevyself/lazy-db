package me.googas.hibernate;

import java.util.function.Consumer;
import java.util.function.Function;
import lombok.NonNull;
import me.googas.lazy.Subloader;
import org.hibernate.Session;

public abstract class HibernateSubloader implements Subloader {

  @NonNull protected final LazyHibernate lazyHibernate;

  protected HibernateSubloader(@NonNull LazyHibernate lazyHibernate) {
    this.lazyHibernate = lazyHibernate;
  }

  protected void onTransaction(@NonNull Consumer<Session> consumer) {
    Session session = this.lazyHibernate.getSessionFactory().openSession();
    session.beginTransaction();
    consumer.accept(session);
    session.getTransaction().commit();
    session.close();
  }

  protected <T> T getOnTransaction(@NonNull Function<Session, T> function) {
    Session session = this.lazyHibernate.getSessionFactory().openSession();
    session.beginTransaction();
    T result = function.apply(session);
    session.getTransaction().commit();
    session.close();
    return result;
  }

  protected <T> T withSession(@NonNull Function<Session, T> function) {
    Session session = this.lazyHibernate.getSessionFactory().openSession();
    T result = function.apply(session);
    session.close();
    return result;
  }
}
