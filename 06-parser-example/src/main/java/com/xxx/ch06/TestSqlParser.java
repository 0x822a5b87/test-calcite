package com.xxx.ch06;

import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;

public class TestSqlParser {
    public static void main(String[] args) throws SqlParseException {
        String sql = "select * from t_user where id = 1";

        // 使用mysql语法解析器
        SqlParser.Config mysqlConfig = SqlParser.Config.DEFAULT.withLex(Lex.MYSQL);

        SqlParser parser = SqlParser.create(sql, mysqlConfig);

        SqlNode sqlNode = parser.parseQuery();

        System.out.println(sqlNode.toString());
    }
}
