package me.googas.lazy.jsongo;

import java.util.ArrayList;
import lombok.NonNull;

/** This builder is used to build a {@link AbstractJsongo} instance in a neat way. */
public abstract class AbstractJsongoBuilder<
        B extends IJsongoSubloaderBuilder<T, I>,
        T extends AbstractJsongo<I>,
        I extends IJsongoSubloader<?>>
    extends Configuration<B, T, I> {

  @NonNull protected final String uri;
  @NonNull protected final String database;
  protected int timeout;
  protected boolean ssl;
  protected boolean ping;

  protected AbstractJsongoBuilder(@NonNull String uri, @NonNull String database) {
    super(new ArrayList<>());
    this.uri = uri;
    this.database = database;
    this.timeout = 300;
    this.ssl = false;
  }

  /**
   * Set the max time to wait until the client responds.
   *
   * @param timeout the maximum time to wait in millis
   * @return this same instance
   */
  @NonNull
  public AbstractJsongoBuilder<B, T, I> timeout(int timeout) {
    this.timeout = timeout;
    return this;
  }

  /**
   * Set whether the client should use ssl.
   *
   * @param ssl whether the client should use ssl
   * @return this same instance
   */
  @NonNull
  public AbstractJsongoBuilder<B, T, I> setSsl(boolean ssl) {
    this.ssl = ssl;
    return this;
  }

  /**
   * Set whether to ping the client on initialization.
   *
   * @param ping whether to ping the client
   * @return this same instance
   */
  @NonNull
  public AbstractJsongoBuilder<B, T, I> setPing(boolean ping) {
    this.ping = ping;
    return this;
  }
}
