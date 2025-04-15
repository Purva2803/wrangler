# Wrangler Enhancement: Byte Size and Time Duration Units Parsers

## Overview
Enhanced the CDAP Wrangler library with native support for parsing and utilizing byte size and time duration units within recipes.

## Implementation Files

### 1. Grammar Modifications
- `wrangler-core/src/main/antlr4/io/cdap/wrangler/parser/Directives.g4`
  - Added BYTE_SIZE and TIME_DURATION lexer rules
  - Added BYTE_UNIT and TIME_UNIT fragments
  - Modified parser rules for new tokens

### 2. API Classes
- `wrangler-api/src/main/java/io/cdap/wrangler/api/parser/ByteSize.java`
- `wrangler-api/src/main/java/io/cdap/wrangler/api/parser/TimeDuration.java`

### 3. Core Implementation
- `wrangler-core/src/main/java/io/cdap/wrangler/parser/AggregateStatsDirective.java`

### 4. Test Files
- `wrangler-core/src/test/java/io/cdap/wrangler/parser/ByteSizeTest.java`
- `wrangler-core/src/test/java/io/cdap/wrangler/parser/TimeDurationTest.java`
- `wrangler-core/src/test/java/io/cdap/wrangler/parser/AggregateStatsDirectiveTest.java`

## Features

### Supported Units
#### Byte Sizes
- B (Bytes)
- KB (Kilobytes)
- MB (Megabytes)
- GB (Gigabytes)
- TB (Terabytes)

#### Time Durations
- ns (Nanoseconds)
- ms (Milliseconds)
- s (Seconds)
- m (Minutes)
- h (Hours)

### New Directive: aggregate-stats

#### Usage
```
aggregate-stats :data_size :response_time total_size_mb total_time_sec
```

#### Example
Input:
```
data_size | response_time
10MB      | 1.5s
20MB      | 2s
30MB      | 500ms
```

Output:
```
total_size_mb | total_time_sec
60.0         | 4.0
```

## Testing
All test files include comprehensive test cases for:
- Valid and invalid inputs
- Edge cases
- Unit conversions
- Aggregation scenarios 