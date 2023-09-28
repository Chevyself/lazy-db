package me.googas.lazy.jsongo.adapters;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.NonNull;

/**
 * Adapter for {@link LocalDateTime} in mongo format. This uses the format of mongo for {@link
 * java.util.Date}
 */
public class LocalDateTimeAdapter extends AbstractDateAdapter<LocalDateTime> {
  @Override
  public ZonedDateTime toUTC(@NonNull LocalDateTime localDateTime) {
    return localDateTime.atZone(ZoneOffset.UTC);
  }

  @Override
  public @NonNull LocalDateTime fromZonedDateTime(@NonNull ZonedDateTime zonedDateTime) {
    return zonedDateTime.toLocalDateTime();
  }
}
