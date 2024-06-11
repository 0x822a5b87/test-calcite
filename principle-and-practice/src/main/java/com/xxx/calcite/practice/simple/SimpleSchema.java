package com.xxx.calcite.practice.simple;

import com.google.common.base.Preconditions;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

import java.util.HashMap;
import java.util.Map;

public class SimpleSchema extends AbstractSchema {

    private final String             schemaName;
    private       Map<String, Table> tableMap;

    public SimpleSchema(String schemaName, Map<String, Table> tableMap) {
        this.schemaName = schemaName;
        this.tableMap   = tableMap;
    }

    @Override
    protected Map<String, Table> getTableMap() {
        return tableMap;
    }

    public static Builder newBuilder(String schemaName) {
        return new Builder(schemaName);
    }

    public String getSchemaName() {
        return schemaName;
    }

    public static class Builder {
        private final String             schemaName;
        private final Map<String, Table> tableMap = new HashMap<>();

        public Builder(String schemaName) {
            Preconditions.checkNotNull(schemaName, "schema name can't be null");
            this.schemaName = schemaName;
        }

        public Builder addTable(String tableName, Table table) {
            tableMap.put(tableName, table);
            return this;
        }

        public SimpleSchema build() {
            return new SimpleSchema(schemaName, tableMap);
        }
    }
}
