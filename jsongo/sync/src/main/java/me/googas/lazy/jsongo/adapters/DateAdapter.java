package me.googas.lazy.jsongo.adapters;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import lombok.NonNull;

/** Adapter for dates in mongo format. */
public class DateAdapter extends AbstractDateAdapter<Date> {
  @Override
  public ZonedDateTime toUTC(@NonNull Date date) {
    return date.toInstant().atZone(ZoneOffset.UTC);
  }

  @Override
  public @NonNull Date fromZonedDateTime(@NonNull ZonedDateTime zonedDateTime) {
    return Date.from(zonedDateTime.toInstant());
  }
}
