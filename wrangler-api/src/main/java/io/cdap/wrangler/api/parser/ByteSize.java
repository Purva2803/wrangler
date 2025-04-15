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
 * Class for parsing and representing byte sizes with units (KB, MB, GB, TB).
 */
public class ByteSize {
  private final double bytes;
  private final String originalValue;

  private ByteSize(double bytes, String originalValue) {
    this.bytes = bytes;
    this.originalValue = originalValue;
  }

  /**
   * Parse a string representation of a byte size.
   * Supports units: KB, MB, GB, TB (case insensitive)
   *
   * @param value String representation (e.g., "1.5KB", "2MB")
   * @return ByteSize object
   * @throws IllegalArgumentException if the format is invalid
   */
  public static ByteSize parse(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Value cannot be null or empty");
    }

    String trimmed = value.trim();
    if (trimmed.startsWith("-")) {
      throw new IllegalArgumentException("Negative values are not allowed");
    }

    // Find the position where the number ends and unit begins
    int i = 0;
    while (i < trimmed.length() && (Character.isDigit(trimmed.charAt(i)) || trimmed.charAt(i) == '.')) {
      i++;
    }

    if (i == 0 || i == trimmed.length()) {
      throw new IllegalArgumentException("Invalid format: " + value);
    }

    String numberPart = trimmed.substring(0, i);
    String unit = trimmed.substring(i).toUpperCase();

    double number;
    try {
      number = Double.parseDouble(numberPart);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid number format: " + numberPart);
    }

    double bytes;
    switch (unit) {
      case "KB":
        bytes = number * 1024;
        break;
      case "MB":
        bytes = number * 1024 * 1024;
        break;
      case "GB":
        bytes = number * 1024 * 1024 * 1024;
        break;
      case "TB":
        bytes = number * 1024 * 1024 * 1024 * 1024;
        break;
      default:
        throw new IllegalArgumentException("Invalid unit: " + unit);
    }

    return new ByteSize(bytes, value);
  }

  /**
   * Get the size in bytes.
   *
   * @return number of bytes
   */
  public double toBytes() {
    return bytes;
  }

  @Override
  public String toString() {
    return originalValue;
  }
} 