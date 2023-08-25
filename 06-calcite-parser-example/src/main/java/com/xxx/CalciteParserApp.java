package com.xxx;

import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;

public class CalciteParserApp {
    public static void main(String[] args) throws SqlParseException {
        String sql = "LOAD hdfs:'/data/user.txt' TO mysql:'db.t_user' (name name, age age) SEPARATOR ','";

        SqlParser.Config mysqlConfig = SqlParser.config()
                                           .withParserFactory(CdmSqlParserImpl.FACTORY)
                                           .withLex(Lex.MYSQL);

        SqlParser parser = SqlParser.create(sql, mysqlConfig);
        SqlNode   sqlNode = parser.parseQuery();
        System.out.println(sqlNode.toString());
    }
}
