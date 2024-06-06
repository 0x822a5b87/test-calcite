package com.xxx.calcite.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.util.Pair;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class MyTable extends AbstractTable {

    private final String            name;
    private final List<TableColumn> columns;

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        List<String>      names    = new ArrayList<>();
        List<RelDataType> sqlTypes = new ArrayList<>();
        for (TableColumn column : columns) {
            names.add(column.getName());
            RelDataType type = typeFactory.createSqlType(column.getType());
            sqlTypes.add(type);
        }
        return typeFactory.createStructType(Pair.zip(names, sqlTypes));
    }
}
