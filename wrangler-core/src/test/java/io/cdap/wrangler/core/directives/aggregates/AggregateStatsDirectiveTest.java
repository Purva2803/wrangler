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

package io.cdap.wrangler.core.directives.aggregates;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class AggregateStatsDirectiveTest {

    @Test
    public void testBasicAggregation() throws Exception {
        AggregateStatsDirective directive = new AggregateStatsDirective();
        List<Row> rows = new ArrayList<>();
        
        // Add test rows with byte sizes and time durations
        Row row1 = new Row();
        row1.add("size", "10MB");
        row1.add("time", "1.5s");
        rows.add(row1);

        Row row2 = new Row();
        row2.add("size", "20MB");
        row2.add("time", "2s");
        rows.add(row2);

        Row row3 = new Row();
        row3.add("size", "30MB");
        row3.add("time", "500ms");
        rows.add(row3);

        // Initialize directive with column names
        directive.initialize(TestUtils.createArgs(
            "size", "time", "total_size_mb", "total_time_sec"
        ));

        // Test non-last partition
        List<Row> result = directive.execute(rows, TestUtils.createContext(false));
        assertEquals(rows, result);

        // Test last partition
        result = directive.execute(rows, TestUtils.createContext(true));
        assertEquals(1, result.size());
        
        Row aggregateRow = result.get(0);
        assertEquals(60.0, aggregateRow.getValue("total_size_mb"));
        assertEquals(4.0, aggregateRow.getValue("total_time_sec"));
    }

    @Test
    public void testMixedUnits() throws Exception {
        AggregateStatsDirective directive = new AggregateStatsDirective();
        List<Row> rows = new ArrayList<>();
        
        // Add test rows with mixed units
        Row row1 = new Row();
        row1.add("size", "1GB");
        row1.add("time", "1h");
        rows.add(row1);

        Row row2 = new Row();
        row2.add("size", "500MB");
        row2.add("time", "30m");
        rows.add(row2);

        Row row3 = new Row();
        row3.add("size", "1.5TB");
        row3.add("time", "1d");
        rows.add(row3);

        // Initialize directive
        directive.initialize(TestUtils.createArgs(
            "size", "time", "total_size_mb", "total_time_sec"
        ));

        // Test last partition
        List<Row> result = directive.execute(rows, TestUtils.createContext(true));
        assertEquals(1, result.size());
        
        Row aggregateRow = result.get(0);
        // Expected: 1GB + 500MB + 1.5TB = 1,536,500 MB
        assertEquals(1536500.0, aggregateRow.getValue("total_size_mb"));
        // Expected: 1h + 30m + 1d = 90,000 seconds
        assertEquals(90000.0, aggregateRow.getValue("total_time_sec"));
    }

    @Test
    public void testNullValues() throws Exception {
        AggregateStatsDirective directive = new AggregateStatsDirective();
        List<Row> rows = new ArrayList<>();
        
        // Add test rows with some null values
        Row row1 = new Row();
        row1.add("size", "10MB");
        row1.add("time", null);
        rows.add(row1);

        Row row2 = new Row();
        row2.add("size", null);
        row2.add("time", "2s");
        rows.add(row2);

        // Initialize directive
        directive.initialize(TestUtils.createArgs(
            "size", "time", "total_size_mb", "total_time_sec"
        ));

        // Test last partition
        List<Row> result = directive.execute(rows, TestUtils.createContext(true));
        assertEquals(1, result.size());
        
        Row aggregateRow = result.get(0);
        assertEquals(10.0, aggregateRow.getValue("total_size_mb"));
        assertEquals(2.0, aggregateRow.getValue("total_time_sec"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidByteSize() throws Exception {
        AggregateStatsDirective directive = new AggregateStatsDirective();
        List<Row> rows = new ArrayList<>();
        
        Row row = new Row();
        row.add("size", "invalid");
        row.add("time", "1s");
        rows.add(row);

        directive.initialize(TestUtils.createArgs(
            "size", "time", "total_size_mb", "total_time_sec"
        ));
        
        directive.execute(rows, TestUtils.createContext(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeDuration() throws Exception {
        AggregateStatsDirective directive = new AggregateStatsDirective();
        List<Row> rows = new ArrayList<>();
        
        Row row = new Row();
        row.add("size", "1MB");
        row.add("time", "invalid");
        rows.add(row);

        directive.initialize(TestUtils.createArgs(
            "size", "time", "total_size_mb", "total_time_sec"
        ));
        
        directive.execute(rows, TestUtils.createContext(true));
    }
} 