package com.xxx.calcite.mysql;

import com.xxx.calcite.common.MyTable;
import com.xxx.calcite.common.TableColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.type.SqlTypeName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class MyMysqlSchemaFactory implements SchemaFactory {

    @Override
    public Schema create(SchemaPlus parentSchema, String name, Map<String, Object> operand) {
        String url  = (String) operand.get("url");
        String user = (String) operand.get("user");
        String pass = (String) operand.get("pass");

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            PreparedStatement stmt   = conn.prepareStatement("SHOW TABLES");
            ResultSet         rs     = stmt.executeQuery();
            List<MyTable>     tables = new ArrayList<>(8);
            while (rs.next()) {
                final String table = rs.getString(1);
                tables.add(new MyTable(table, getColumns(conn, table)));
            }
            return new MyMysqlSchema(name, tables);
        } catch (Exception e) {
            log.error("", e);
        }

        return null;
    }

    private List<TableColumn> getColumns(Connection conn, String table) throws SQLException {
        final Statement   stmt    = conn.createStatement();
        final ResultSet   rs      = stmt.executeQuery("DESC " + table);
        List<TableColumn> columns = new ArrayList<>();
        while (rs.next()) {
            SqlTypeName type = SqlTypeName.get(typeMap(pureType(rs.getString("Type"))));
            columns.add(new TableColumn(rs.getString("Field"), type));
        }
        return columns;
    }

    /**
     * mysql 有的类型和 calcite不一样，需要修改下别名
     */
    private String typeMap(String type) {
        switch (type.toLowerCase()) {
            case "int":
                return "integer";
            default:
                return type;
        }
    }

    /**
     * 传入的type含有类型长度，如 bigint(20), varchar(258)
     * 需要去掉括号
     */
    private String pureType(String type) {
        final int i = type.indexOf('(');
        return i > 0 ? type.substring(0, i) : type;
    }
}
