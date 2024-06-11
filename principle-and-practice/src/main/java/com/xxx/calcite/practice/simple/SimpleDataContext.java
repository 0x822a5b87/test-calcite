package com.xxx.calcite.practice.simple;

import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.schema.SchemaPlus;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleDataContext implements DataContext {

    private SchemaPlus schemaPlus;

    public SimpleDataContext(SchemaPlus schemaPlus) {
        this.schemaPlus = schemaPlus;
    }

    @Override
    public SchemaPlus getRootSchema() {
        return schemaPlus;
    }

    @Override
    public JavaTypeFactory getTypeFactory() {
        return new JavaTypeFactoryImpl();
    }

    @Override
    public QueryProvider getQueryProvider() {
        throw new RuntimeException("unsupported operation");
    }

    @Override
    public Object get(String name) {
        if (Variable.CANCEL_FLAG.camelName.equals(name)) {
            return new AtomicBoolean(false);
        }
        return null;
    }
}
