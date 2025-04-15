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

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.DirectiveParseException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.List;

/**
 * Directive for aggregating byte sizes and time durations.
 */
@Plugin(type = Directive.TYPE)
@Name("aggregate-stats")
@Description("Aggregates byte sizes and time durations from specified columns.")
public class AggregateStatsDirective implements Directive {
  public static final String NAME = "aggregate-stats";
  private String byteSizeColumn;
  private String timeDurationColumn;
  private String totalSizeColumn;
  private String totalTimeColumn;
  private long totalBytes;
  private long totalNanos;
  private int rowCount;

  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder(NAME);
    builder.define("byteSizeColumn", TokenType.COLUMN);
    builder.define("timeDurationColumn", TokenType.COLUMN);
    builder.define("totalSizeColumn", TokenType.COLUMN);
    builder.define("totalTimeColumn", TokenType.COLUMN);
    return builder.build();
  }

  @Override
  public void initialize(Arguments args) throws DirectiveParseException {
    this.byteSizeColumn = args.value("byteSizeColumn");
    this.timeDurationColumn = args.value("timeDurationColumn");
    this.totalSizeColumn = args.value("totalSizeColumn");
    this.totalTimeColumn = args.value("totalTimeColumn");
    this.totalBytes = 0;
    this.totalNanos = 0;
    this.rowCount = 0;
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
    // For the last row in the partition, calculate and return aggregates
    if (context.isLast()) {
      Row result = new Row();
      // Convert total bytes to MB
      double totalMB = totalBytes / (1024.0 * 1024.0);
      // Convert total nanoseconds to seconds
      double totalSeconds = totalNanos / 1_000_000_000.0;
      
      result.add(totalSizeColumn, totalMB);
      result.add(totalTimeColumn, totalSeconds);
      return List.of(result);
    }

    // Process each row and accumulate totals
    for (Row row : rows) {
      Object sizeObj = row.getValue(byteSizeColumn);
      Object timeObj = row.getValue(timeDurationColumn);

      try {
        if (sizeObj != null) {
          ByteSize byteSize = new ByteSize(sizeObj.toString());
          totalBytes += byteSize.getBytes();
        }

        if (timeObj != null) {
          TimeDuration timeDuration = new TimeDuration(timeObj.toString());
          totalNanos += timeDuration.getNanos();
        }

        rowCount++;
      } catch (IllegalArgumentException e) {
        throw new DirectiveExecutionException(
          String.format("Invalid format in row %d: %s", rowCount + 1, e.getMessage()));
      }
    }

    return rows;
  }
} 