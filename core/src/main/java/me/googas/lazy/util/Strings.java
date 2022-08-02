package me.googas.lazy.util;

import lombok.NonNull;

public final class Strings {

  @NonNull
  public static String format(String string, Object... format) {
    string = string == null ? "null" : string;
    for (int i = 0; i < format.length; i++) {
      Object object = format[i];
      string = string.replace("{" + i + "}", object == null ? "null" : object.toString());
    }
    return string;
  }
}
