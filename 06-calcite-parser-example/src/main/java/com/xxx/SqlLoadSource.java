package com.xxx;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.calcite.sql.SqlIdentifier;

/**
 * {@link SqlLoad load} 方法中的数据，由一个 {@link org.apache.calcite.sql.SqlIdentifier} 以及一个 {@link String}　构成
 *
 * @author 0x822a5b87
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SqlLoadSource {
    /**
     * LOAD 数据的类型，可能是 mysql，hdfs 或者任意其他支持的数据源
     */
    private SqlIdentifier type;
    /**
     * LOAD 数据的信息
     */
    private String        obj;
}
