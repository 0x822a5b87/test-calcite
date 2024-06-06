package com.xxx.calcite.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.calcite.sql.type.SqlTypeName;

@AllArgsConstructor
@Getter
public class TableColumn {
    private final String      name;
    private final SqlTypeName type;
}
