package me.googas.hibernate.models;

import java.util.UUID;
import lombok.NonNull;
import me.googas.hibernate.HibernateSubloader;
import me.googas.hibernate.HibernateSubloaderBuilder;
import me.googas.hibernate.LazyHibernate;

public class FreelancerSubloader extends HibernateSubloader {

  protected FreelancerSubloader(@NonNull LazyHibernate lazyHibernate) {
    super(lazyHibernate);
  }

  public HibernateFreelancer create(long id, long guild) {
    return this.getOnTransaction(
        session -> {
          HibernateFreelancer freelancer = new HibernateFreelancer(id, guild, UUID.randomUUID());
          session.persist(freelancer);
          return freelancer;
        });
  }

  public HibernateFreelancer getFreelancer(long id, long guildId) {
    return this.withSession(
        session ->
            session.get(HibernateFreelancer.class, new HibernateFreelancer(id, guildId, null)));
  }

  public static class Builder implements HibernateSubloaderBuilder {

    @Override
    public @NonNull HibernateSubloader build(@NonNull LazyHibernate lazyHibernate) {
      return new FreelancerSubloader(lazyHibernate);
    }
  }
}
