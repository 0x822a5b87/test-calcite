package com.xxx.calcite.ds.csv;

import org.apache.calcite.linq4j.tree.Types;
import org.apache.calcite.schema.ScalarFunction;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.TableFunction;
import org.apache.calcite.schema.impl.AggregateFunctionImpl;
import org.apache.calcite.schema.impl.ScalarFunctionImpl;
import org.apache.calcite.schema.impl.TableFunctionImpl;

import java.lang.reflect.Method;
import java.util.Map;

public class CsvSchemaFactory implements SchemaFactory {

    public static final Method SUB_STRING =
            Types.lookupMethod(UDF.class, "mySubString2", String.class, Integer.class, Integer.class);

    @Override
    public Schema create(SchemaPlus parentSchema, String name, Map<String, Object> operand) {
        final ScalarFunction table = ScalarFunctionImpl.create(SUB_STRING);
        parentSchema.add("MY_SUB_STRING2", table);
        parentSchema.add("MY_SPLIT", ScalarFunctionImpl.create(UDF.class, "mySplit"));
        parentSchema.add("COLLECT_LIST", AggregateFunctionImpl.create(CollectList.class));

        parentSchema.add("EXPLODE", TableFunctionImpl.create(Explode.UDTF_METHOD));

        return new CsvSchema(operand.get("dataFile").toString());
    }
}
