package me.googas.hibernate.models;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "freelancers")
public class HibernateFreelancer {

  @Id
  @Column(name = "id", nullable = false)
  private final long id;

  @Id
  @Column(name = "guild", nullable = false)
  private final long guild;

  @Column(name = "uuid", nullable = false)
  private final UUID uuid;

  public HibernateFreelancer(long id, long guild, UUID uuid) {
    this.id = id;
    this.guild = guild;
    this.uuid = uuid;
  }

  public HibernateFreelancer() {
    this(0, 0, null);
  }
}
