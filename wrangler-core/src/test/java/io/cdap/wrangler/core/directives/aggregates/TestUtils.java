package io.cdap.wrangler.core.directives.aggregates;

import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.HashMap;
import java.util.Map;

public class TestUtils {
    public static Arguments createArgs(String byteSizeColumn, String timeDurationColumn, 
                                     String totalSizeColumn, String totalTimeColumn) {
        return new Arguments() {
            @Override
            public int size() {
                return 4;
            }

            @Override
            public boolean contains(String name) {
                return name.equals("byteSizeColumn") || name.equals("timeDurationColumn") ||
                       name.equals("totalSizeColumn") || name.equals("totalTimeColumn");
            }

            @Override
            public <T> T value(String name) {
                switch (name) {
                    case "byteSizeColumn":
                        return (T) new ColumnName(byteSizeColumn);
                    case "timeDurationColumn":
                        return (T) new ColumnName(timeDurationColumn);
                    case "totalSizeColumn":
                        return (T) new Text(totalSizeColumn);
                    case "totalTimeColumn":
                        return (T) new Text(totalTimeColumn);
                    default:
                        return null;
                }
            }
        };
    }

    public static ExecutorContext createContext(boolean isLast) {
        return new ExecutorContext() {
            @Override
            public String getNamespace() {
                return "test";
            }

            @Override
            public Environment getEnvironment() {
                return Environment.TESTING;
            }

            @Override
            public StageMetrics getMetrics() {
                return null;
            }

            @Override
            public String getContextName() {
                return "test";
            }

            @Override
            public Map<String, String> getProperties() {
                return new HashMap<>();
            }

            @Override
            public URL getService(String applicationId, String serviceId) {
                return null;
            }

            @Override
            public TransientStore getTransientStore() {
                return null;
            }

            @Override
            public boolean isLast() {
                return isLast;
            }
        };
    }
} 