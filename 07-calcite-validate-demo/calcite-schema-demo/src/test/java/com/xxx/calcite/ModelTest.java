package com.xxx.calcite;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.Lex;
import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.model.JsonRoot;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.server.CalciteServerStatement;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.util.SqlOperatorTables;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.tools.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ModelTest {
    final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
            .configure(JsonParser.Feature.ALLOW_COMMENTS, true);

    @Test
    void testModelRead() throws IOException {
        final JsonRoot jsonRoot = JSON_MAPPER.readValue(new File("src/main/resources/model.json"), JsonRoot.class);
        Assertions.assertEquals(jsonRoot.defaultSchema, "MYSQL");
        Assertions.assertEquals(jsonRoot.version, "1.0");
    }

    @Test
    void validateView() throws SQLException, SqlParseException {
        String sql = "select * from MYSQL.v_num";
        executeQuery(sql);
    }

    private void executeQuery(String sql) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:model=src/main/resources/model.json")) {
            final Statement statement = connection.createStatement();
            final ResultSet rs = statement.executeQuery(sql);
            final int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                for (int i = 0; i < columnCount; i++) {
                    System.out.println(rs.getObject(i + 1));
                }
            }
        }
    }

    @Test
    void validateViewReal() throws ValidationException, SQLException, SqlParseException {
        String sql = "select * from VIEW.v_user_role";

        String model = "src/main/resources/model.json";
        Properties info = new Properties();
        info.put("model", model);
        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
        CalciteServerStatement statement = connection.createStatement().unwrap(CalciteServerStatement.class);
        CalcitePrepare.Context prepareContext = statement.createPrepareContext();

        final FrameworkConfig config = Frameworks.newConfigBuilder()
                .parserConfig(SqlParser.config().withLex(Lex.MYSQL))
                .defaultSchema(prepareContext.getRootSchema().plus())
                .build();
        final Planner planner = Frameworks.getPlanner(config);
        final SqlNode parse = planner.parse(sql);
        System.out.println(parse);
        final SqlNode validate = planner.validate(parse);
        System.out.println(validate);
    }

    @Test
    void validateFunction() throws SQLException, SqlParseException {
        String sql = "select CSV.test(role) len from CSV.sys_role";
        validateSql(sql);
    }

    @Test
    void validateError() throws SQLException, SqlParseException {
        String sql = "select len from sys_user";
        validateSql(sql);
    }

    private void validateSql(String sql) throws SqlParseException, SQLException {
        SqlParser.Config mysqlConfig = SqlParser.config().withLex(Lex.MYSQL);
        SqlParser parser = SqlParser.create(sql, mysqlConfig);
        SqlNode sqlNode = parser.parseQuery();

        // 构造SqlValidator实例
        Connection connection = DriverManager.getConnection("jdbc:calcite:model=src/main/resources/model.json");
        CalciteServerStatement statement = connection.createStatement().unwrap(CalciteServerStatement.class);
        CalcitePrepare.Context prepareContext = statement.createPrepareContext();

        SqlTypeFactoryImpl factory = new SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        CalciteCatalogReader calciteCatalogReader = new CalciteCatalogReader(
                prepareContext.getRootSchema(),
                prepareContext.getDefaultSchemaPath(),
                factory,
                new CalciteConnectionConfigImpl(new Properties()));

        final SqlStdOperatorTable instance = SqlStdOperatorTable.instance();
        SqlValidator validator = SqlValidatorUtil.newValidator(SqlOperatorTables.chain(instance, calciteCatalogReader),
                calciteCatalogReader, factory, SqlValidator.Config.DEFAULT.withIdentifierExpansion(true));

        // 校验
        final SqlNode validatedSqlNode = validator.validate(sqlNode);
        System.out.println("校验后的SqlNode：");
        System.out.println(validatedSqlNode);
    }
}
