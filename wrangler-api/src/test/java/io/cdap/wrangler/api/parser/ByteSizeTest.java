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

public class ByteSizeTest {

  @Test
  public void testValidByteSizes() {
    Assert.assertEquals(1024L, ByteSize.parse("1KB").toBytes());
    Assert.assertEquals(1024L * 1024L, ByteSize.parse("1MB").toBytes());
    Assert.assertEquals(1024L * 1024L * 1024L, ByteSize.parse("1GB").toBytes());
    Assert.assertEquals(1024L * 1024L * 1024L * 1024L, ByteSize.parse("1TB").toBytes());
  }

  @Test
  public void testDecimalValues() {
    Assert.assertEquals(1536L, ByteSize.parse("1.5KB").toBytes());
    Assert.assertEquals(1024L * 1024L * 1.5, ByteSize.parse("1.5MB").toBytes(), 0.1);
  }

  @Test
  public void testCaseInsensitive() {
    Assert.assertEquals(1024L, ByteSize.parse("1kb").toBytes());
    Assert.assertEquals(1024L, ByteSize.parse("1KB").toBytes());
    Assert.assertEquals(1024L * 1024L, ByteSize.parse("1mb").toBytes());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidFormat() {
    ByteSize.parse("invalid");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeValue() {
    ByteSize.parse("-1KB");
  }

  @Test
  public void testToString() {
    ByteSize size = ByteSize.parse("1.5GB");
    Assert.assertEquals("1.5GB", size.toString());
  }
} 