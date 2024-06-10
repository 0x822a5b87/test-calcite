package com.xxx.calcite.stream;

import lombok.SneakyThrows;
import org.apache.calcite.DataContext;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.rel.RelCollations;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Statistics;
import org.apache.calcite.schema.StreamableTable;
import org.apache.calcite.schema.Table;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.type.SqlTypeName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class StreamLogTable implements ScannableTable, StreamableTable {

    private final static String[] LEVELS = new String[]{
            "TRACE",
            "DEBUG",
            "INFO",
            "WARN",
            "ERROR"
    };

    @Override
    public Enumerable<Object[]> scan(DataContext root) {
        return Linq4j.asEnumerable(() -> new Iterator<Object[]>() {

            private final Random r = new Random();

            @Override
            public boolean hasNext() {
                return true;
            }

            @SneakyThrows
            @Override
            public Object[] next() {
                TimeUnit.MICROSECONDS.sleep(r.nextInt(100));
                String level     = LEVELS[r.nextInt(LEVELS.length)];
                long   timestamp = System.currentTimeMillis();
                return new Object[]{timestamp, level, String.format("This is a %s msg on %d", level, timestamp)};
            }
        });
    }

    @Override
    public Table stream() {
        return this;
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        return typeFactory.builder()
                          .add("LOG_TIME", SqlTypeName.TIMESTAMP)
                          .add("LEVEL", SqlTypeName.VARCHAR)
                          .add("MSG", SqlTypeName.VARCHAR)
                          .build();
    }

    @Override
    public Statistic getStatistic() {
        return Statistics.of(100d, new ArrayList<>(1), RelCollations.createSingleton(0));
    }

    @Override
    public Schema.TableType getJdbcTableType() {
        return Schema.TableType.STREAM;
    }

    @Override
    public boolean isRolledUp(String column) {
        return false;
    }

    @Override
    public boolean rolledUpColumnValidInsideAgg(String column, SqlCall call, SqlNode parent,
                                                CalciteConnectionConfig config) {
        return false;
    }
}
