package me.googas.lazy.sql;

/** Represents an object that is stored in sql and may have its id updated. */
public interface SQLElement {

  /**
   * Set the id of the element.
   *
   * @param id the new id
   */
  void setId(int id);

  /**
   * Get the id of the element.
   *
   * @return the id
   */
  int getId();
}
