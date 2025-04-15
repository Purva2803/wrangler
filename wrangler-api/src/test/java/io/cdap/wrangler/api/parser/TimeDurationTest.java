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

import org.junit.Assert;
import org.junit.Test;

public class TimeDurationTest {

  @Test
  public void testValidDurations() {
    Assert.assertEquals(1000L, TimeDuration.parse("1s").toMillis());
    Assert.assertEquals(60000L, TimeDuration.parse("1m").toMillis());
    Assert.assertEquals(3600000L, TimeDuration.parse("1h").toMillis());
    Assert.assertEquals(86400000L, TimeDuration.parse("1d").toMillis());
  }

  @Test
  public void testDecimalValues() {
    Assert.assertEquals(1500L, TimeDuration.parse("1.5s").toMillis());
    Assert.assertEquals(90000L, TimeDuration.parse("1.5m").toMillis());
  }

  @Test
  public void testCaseInsensitive() {
    Assert.assertEquals(1000L, TimeDuration.parse("1S").toMillis());
    Assert.assertEquals(60000L, TimeDuration.parse("1M").toMillis());
    Assert.assertEquals(3600000L, TimeDuration.parse("1H").toMillis());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidFormat() {
    TimeDuration.parse("invalid");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeValue() {
    TimeDuration.parse("-1s");
  }

  @Test
  public void testToString() {
    TimeDuration duration = TimeDuration.parse("1.5h");
    Assert.assertEquals("1.5h", duration.toString());
  }

  @Test
  public void testCompoundDuration() {
    Assert.assertEquals(3660000L, TimeDuration.parse("1h1m").toMillis());
    Assert.assertEquals(3661000L, TimeDuration.parse("1h1m1s").toMillis());
  }
} 