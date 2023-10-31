package me.googas.hibernate.models;

import me.googas.hibernate.LazyHibernate;
import org.hibernate.cfg.Configuration;

public class Testing {

  public static void main(String[] args) {
    Configuration configuration =
        new Configuration()
            .setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver")
            .setProperty(
                "hibernate.connection.url", "jdbc:mysql:// db4free.net:3306/testing_googas")
            .setProperty("hibernate.connection.username", args[0])
            .setProperty("hibernate.connection.password", args[1])
            .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect")
            .addAnnotatedClass(HibernateFreelancer.class)
            .configure();
    LazyHibernate lazyHibernate =
        LazyHibernate.using(configuration).add(new FreelancerSubloader.Builder()).build();
    FreelancerSubloader subloader = lazyHibernate.getSubloader(FreelancerSubloader.class);
    HibernateFreelancer created = subloader.create(1, 1);
    HibernateFreelancer freelancer = subloader.getFreelancer(1, 1);
    System.out.println(freelancer);
  }
}
