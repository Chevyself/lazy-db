package me.googas.lazy.sync;

import me.googas.lazy.jsongo.IJsongoSubloaderBuilder;

/**
 * Represents a builder for {@link JsongoSubloader}. This is used to centralize an initialize for
 * {@link JsongoSubloader} to be used in {@link JsongoBuilder}
 */
public interface JsongoSubloaderBuilder
    extends IJsongoSubloaderBuilder<Jsongo, JsongoSubloader<?>> {}
