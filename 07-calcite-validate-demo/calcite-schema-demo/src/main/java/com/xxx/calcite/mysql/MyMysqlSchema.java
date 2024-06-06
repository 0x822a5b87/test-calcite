package com.xxx.calcite.mysql;

import com.xxx.calcite.common.MyTable;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Setter
public class MyMysqlSchema extends AbstractSchema {

    private String name;

    private final List<MyTable> tables;

    @Override
    protected Map<String, Table> getTableMap() {
        Map<String, Table> tableMap = new HashMap<>();
        for (MyTable table : tables) {
            tableMap.put(table.getName(), table);
        }
        return tableMap;
    }

}
