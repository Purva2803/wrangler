/*
 * Copyright © 2024 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.api.parser;

/**
 * Class for parsing and representing time durations with units (s, m, h, d).
 */
public class TimeDuration {
  private final long millis;
  private final String originalValue;

  private TimeDuration(long millis, String originalValue) {
    this.millis = millis;
    this.originalValue = originalValue;
  }

  /**
   * Parse a string representation of a time duration.
   * Supports units: s (seconds), m (minutes), h (hours), d (days) - case insensitive
   * Also supports compound durations like "1h1m" or "1h1m1s"
   *
   * @param value String representation (e.g., "1.5s", "2h", "1h30m")
   * @return TimeDuration object
   * @throws IllegalArgumentException if the format is invalid
   */
  public static TimeDuration parse(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Value cannot be null or empty");
    }

    String trimmed = value.trim();
    if (trimmed.startsWith("-")) {
      throw new IllegalArgumentException("Negative values are not allowed");
    }

    long totalMillis = 0;
    int start = 0;
    int i = 0;

    while (i < trimmed.length()) {
      // Find where the number ends
      while (i < trimmed.length() && (Character.isDigit(trimmed.charAt(i)) || trimmed.charAt(i) == '.')) {
        i++;
      }

      if (i == start || i == trimmed.length()) {
        throw new IllegalArgumentException("Invalid format: " + value);
      }

      // Parse the number part
      String numberPart = trimmed.substring(start, i);
      double number;
      try {
        number = Double.parseDouble(numberPart);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid number format: " + numberPart);
      }

      // Get the unit
      char unit = Character.toLowerCase(trimmed.charAt(i));
      long unitMillis;
      switch (unit) {
        case 's':
          unitMillis = 1000;
          break;
        case 'm':
          unitMillis = 60 * 1000;
          break;
        case 'h':
          unitMillis = 60 * 60 * 1000;
          break;
        case 'd':
          unitMillis = 24 * 60 * 60 * 1000;
          break;
        default:
          throw new IllegalArgumentException("Invalid unit: " + unit);
      }

      totalMillis += number * unitMillis;
      i++;
      start = i;
    }

    return new TimeDuration(totalMillis, value);
  }

  /**
   * Get the duration in milliseconds.
   *
   * @return duration in milliseconds
   */
  public long toMillis() {
    return millis;
  }

  @Override
  public String toString() {
    return originalValue;
  }
} 