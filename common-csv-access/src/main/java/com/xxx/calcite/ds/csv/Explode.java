package com.xxx.calcite.ds.csv;

import org.apache.calcite.adapter.java.AbstractQueryableTable;
import org.apache.calcite.linq4j.BaseQueryable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.linq4j.Queryable;
import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.QueryableTable;
import org.apache.calcite.schema.SchemaPlus;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;

public class Explode {
    // init method
    public static final Method UDTF_METHOD =
            Types.lookupMethod(Explode.class, "explode", String.class, String.class);

    public static QueryableTable explode(final String str, final String regex) {
        // UDTF return a table
        return new AbstractQueryableTable(String.class) {
            // return row type, type should match field type in SQL
            @Override
            public RelDataType getRowType(RelDataTypeFactory typeFactory) {
                return typeFactory.createStructType(Collections.singletonList(typeFactory.createJavaType(String.class)),
                                                    Collections.singletonList("x"));
            }


            // convert this table into a QueryTable
            @Override
            public <T> Queryable<T> asQueryable(QueryProvider queryProvider,
                                                SchemaPlus schema, String tableName) {

                BaseQueryable<String> queryable = new BaseQueryable<String>(queryProvider, String.class, null) {
                    @Override
                    public Enumerator<String> enumerator() {
                        return new Enumerator<String>() {
                            int      i   = -1;
                            String[] res = null;

                            @Override
                            public String current() {
                                return res[i];
                            }

                            @Override
                            public boolean moveNext() {
                                if (i == -1) {
                                    res = str.split(regex);
                                }
                                if (i < res.length - 1) {
                                    i++;
                                    return true;
                                } else
                                    return false;
                            }

                            @Override
                            public void reset() {
                                i = -1;
                            }

                            @Override
                            public void close() {
                            }
                        };
                    }
                };
                // noinspection unchecked
                return (Queryable<T>) queryable;
            }
        };
    }
}
