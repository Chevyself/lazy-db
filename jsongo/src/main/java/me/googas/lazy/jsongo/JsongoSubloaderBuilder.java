package me.googas.lazy.jsongo;

import me.googas.lazy.builders.SuppliedBuilder;

/**
 * Represents a builder for {@link JsongoSubloader}. This is used to centralize an initialize for
 * {@link JsongoSubloader} to be used in {@link Jsongo.JsongoBuilder}
 */
public interface JsongoSubloaderBuilder extends SuppliedBuilder<Jsongo, JsongoSubloader<?>> {}
