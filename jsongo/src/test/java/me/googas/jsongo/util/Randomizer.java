package me.googas.jsongo.util;

import java.util.Random;
import lombok.NonNull;

public class Randomizer {

  @NonNull
  private static final String CHARS =
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  @NonNull private static final Random random = new Random();

  public static @NonNull String nextString(int length) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      builder.append(Randomizer.CHARS.charAt(Randomizer.random.nextInt(Randomizer.CHARS.length())));
    }
    return builder.toString();
  }

  public static int nextInt() {
    return Randomizer.random.nextInt();
  }

  public static long nextLong() {
    return Randomizer.random.nextLong();
  }
}
