package me.googas.lazy.sql;

import me.googas.lazy.builders.SuppliedBuilder;

/**
 * Represents a builder for {@link LazySQLSubloader}. This is used to centralize an initialize for
 * {@link LazySQLSubloader} to be used in {@link LazySQL.LazySQLBuilder}
 */
public interface LazySQLSubloaderBuilder extends SuppliedBuilder<LazySQL, LazySQLSubloader> {}
