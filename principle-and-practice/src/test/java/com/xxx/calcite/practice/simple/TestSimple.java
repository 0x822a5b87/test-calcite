package com.xxx.calcite.practice.simple;

import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.interpreter.Interpreter;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.plan.Contexts;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCostImpl;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.prepare.Prepare;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.impl.SqlParserImpl;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.sql2rel.StandardConvertletTable;
import org.apache.calcite.tools.Program;
import org.apache.calcite.tools.Programs;
import org.apache.calcite.tools.RuleSet;
import org.apache.calcite.tools.RuleSets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Properties;


public class TestSimple {

    private static SimpleTable             users;
    private static SimpleTable             orders;
    private static SimpleSchema            schema;
    private static CalciteSchema           rootSchema;
    private static CalciteConnectionConfig calciteConnectionConfig;
    private static Prepare.CatalogReader   catalogReader;
    private static SqlValidator            validator;
    private static RelOptPlanner           planner;
    private static SqlToRelConverter       converter;
    private static Program                 program;

    @BeforeAll
    public static void setup() {
        // 初始化metadata
        initTable();
        initSchema();

        // 初始化calcite内部信息
        // planner 和 optimizer 会使用CatalogReader读取元数据信息
        initCalciteConnectionConfig();
        initCalciteSchema();
        initCatalogReader();

        // 初始化 validator, planner, converter, optimizer
        initSqlValidator();
        initPlanner();
        initConverter();
        initOptimizer();
    }

    @Test
    public void testSimple() throws SqlParseException {
//        String sql = "SELECT u.id, name, age, sum(price) " +
//                     "FROM users AS u join orders AS o ON u.id = o.user_id " +
//                     "WHERE age >= 20 AND age <= 30 " +
//                     "GROUP BY u.id, name, age " +
//                     "ORDER BY u.id";

        String sql = "SELECT * from users";

        SqlParser.Config c = SqlParser.config()
                                      .withParserFactory(SqlParserImpl.FACTORY)
                                      .withCaseSensitive(false);
        SqlParser sqlParser    = SqlParser.create(sql, c);
        SqlNode   rootNode     = sqlParser.parseQuery();

        System.out.println(rootNode);

        // 验证元数据，例如表名，字段名，函数名和基本数据类型的检查
        SqlNode   validateNode = validator.validate(rootNode);

        System.out.println(validateNode.toString());

        // 将SqlNode树转化为RelNode树
        // 我们也可以不显示的调用 validator.validate(rootNode)，而是通过传递参数 needsValidation = true
        RelRoot relNode = converter.convertQuery(rootNode, false, true);
        System.out.println(relNode.rel.explain());
        // 优化前
        // LogicalSort(sort0=[$0], dir0=[ASC])
        //  LogicalAggregate(group=[{0, 1, 2}], EXPR$3=[SUM($3)])
        //    LogicalProject(ID=[$0], NAME=[$1], AGE=[$2], price=[$6])
        //      LogicalFilter(condition=[AND(>=($2, 20), <=($2, 30))])
        //        LogicalJoin(condition=[=($0, $4)], joinType=[inner])
        //          LogicalTableScan(table=[[x, USERS]])
        //          LogicalTableScan(table=[[x, ORDERS]])

        // 执行优化
        RelNode optimizedRelNode = program.run(
                planner,
                relNode.rel,
                relNode.rel.getTraitSet().plus(EnumerableConvention.INSTANCE),
                Collections.emptyList(),
                Collections.emptyList());
        System.out.println(optimizedRelNode.explain());
        // 优化后
        // EnumerableSort(sort0=[$0], dir0=[ASC])
        //  EnumerableAggregate(group=[{0, 1, 2}], EXPR$3=[SUM($3)])
        //    EnumerableCalc(expr#0..6=[{inputs}], proj#0..2=[{exprs}], price=[$t6])
        //      EnumerableHashJoin(condition=[=($0, $4)], joinType=[inner])
        //        EnumerableCalc(expr#0..2=[{inputs}], expr#3=[Sarg[[20..30]]], expr#4=[SEARCH($t2, $t3)], proj#0..2=[{exprs}], $condition=[$t4])
        //          EnumerableTableScan(table=[[x, USERS]])
        //        EnumerableTableScan(table=[[x, ORDERS]])

        output(optimizedRelNode);
    }

    private static void initTable() {
        orders = SimpleTable.newBuilder("ORDERS", "orders.csv")
                            .addField("id", SqlTypeName.VARCHAR)
                            .addField("user_id", SqlTypeName.VARCHAR)
                            .addField("goods", SqlTypeName.VARCHAR)
                            .addField("price", SqlTypeName.DOUBLE)
                            .build();

        users = SimpleTable.newBuilder("USERS", "users.csv")
                           .addField("id", SqlTypeName.VARCHAR)
                           .addField("name", SqlTypeName.VARCHAR)
                           .addField("age", SqlTypeName.INTEGER)
                           .build();
    }

    private static void initSchema() {
        schema = SimpleSchema.newBuilder("x")
                             .addTable("ORDERS", orders)
                             .addTable("USERS", users)
                             .build();

    }

    private static void initCalciteSchema() {
        // create root schema
        rootSchema = CalciteSchema.createRootSchema(false, false);
        rootSchema.add(schema.getSchemaName(), schema);
    }

    private static void initCalciteConnectionConfig() {
        Properties configProperties = new Properties();
        configProperties.put(CalciteConnectionProperty.CASE_SENSITIVE.camelName(), Boolean.FALSE.toString());
        configProperties.put(CalciteConnectionProperty.UNQUOTED_CASING.camelName(), Casing.UNCHANGED.toString());
        configProperties.put(CalciteConnectionProperty.QUOTED_CASING.camelName(), Casing.UNCHANGED.toString());
        calciteConnectionConfig = new CalciteConnectionConfigImpl(configProperties);
    }

    private static void initCatalogReader() {
        RelDataTypeFactory typeFactory = new JavaTypeFactoryImpl();
        // create catalog reader, needed by SqlValidator
        catalogReader = new CalciteCatalogReader(
                rootSchema,
                Collections.singletonList(schema.getSchemaName()),
                typeFactory,
                calciteConnectionConfig);

    }

    private static void initSqlValidator() {
        // create SqlValidator
        SqlValidator.Config validatorConfig = SqlValidator.Config.DEFAULT
                .withLenientOperatorLookup(calciteConnectionConfig.lenientOperatorLookup())
                .withSqlConformance(calciteConnectionConfig.conformance())
                .withDefaultNullCollation(calciteConnectionConfig.defaultNullCollation())
                .withIdentifierExpansion(true);
        validator = SqlValidatorUtil.newValidator(
                SqlStdOperatorTable.instance(), catalogReader, catalogReader.getTypeFactory(), validatorConfig);
    }

    private static void initPlanner() {
        // 创建VolcanoPlanner, VolcanoPlanner在后面的优化中还需要用到
        planner = new VolcanoPlanner(RelOptCostImpl.FACTORY, Contexts.of(calciteConnectionConfig));
        planner.addRelTraitDef(ConventionTraitDef.INSTANCE);
    }

    private static void initConverter() {
        // 创建SqlToRelConverter
        RelOptCluster cluster = RelOptCluster.create(planner, new RexBuilder(catalogReader.getTypeFactory()));
        SqlToRelConverter.Config converterConfig = SqlToRelConverter.config()
                                                                    .withTrimUnusedFields(true)
                                                                    .withExpand(false);
        converter = new SqlToRelConverter(
                null,
                validator,
                catalogReader,
                cluster,
                StandardConvertletTable.INSTANCE,
                converterConfig);
    }

    private static void initOptimizer() {
        // 优化规则
        RuleSet rules = RuleSets.ofList(
                CoreRules.FILTER_TO_CALC,
                CoreRules.PROJECT_TO_CALC,
                CoreRules.FILTER_CALC_MERGE,
                CoreRules.PROJECT_CALC_MERGE,
                CoreRules.FILTER_INTO_JOIN,        // 过滤谓词下推到Join之前
                EnumerableRules.ENUMERABLE_TABLE_SCAN_RULE,
                EnumerableRules.ENUMERABLE_PROJECT_TO_CALC_RULE,
                EnumerableRules.ENUMERABLE_FILTER_TO_CALC_RULE,
                EnumerableRules.ENUMERABLE_JOIN_RULE,
                EnumerableRules.ENUMERABLE_SORT_RULE,
                EnumerableRules.ENUMERABLE_CALC_RULE,
                EnumerableRules.ENUMERABLE_AGGREGATE_RULE);
        program = Programs.of(RuleSets.ofList(rules));
    }

    private static void output(RelNode relNode) {
        SimpleDataContext context = new SimpleDataContext(rootSchema.plus());
        try (
                Interpreter interpreter = new Interpreter(context, relNode);
        ) {
            Enumerator<Object[]> enumerator = interpreter.enumerator();
            while (enumerator.moveNext()) {
                Object[]      current = enumerator.current();
                StringBuilder sb      = new StringBuilder();
                for (Object v : current) {
                    sb.append(v).append(",");
                }
                sb.setLength(sb.length() - 1);
                System.out.println(sb);

            }
        }
    }

//    private static void initEnumerator() {
//        EnumerableRel        enumerable         = (EnumerableRel) optimizerRelTree;
//        Map<String, Object>  internalParameters = new LinkedHashMap<>();
//        EnumerableRel.Prefer prefer             = EnumerableRel.Prefer.ARRAY;
//        Bindable bindable = EnumerableInterpretable.toBindable(internalParameters,
//                                                               null, enumerable, prefer);
//        Enumerable bind = bindable.bind(new SimpleDataContext(rootSchema.plus()));
//        Enumerator enumerator = bind.enumerator();
//        while (enumerator.moveNext()) {
//            Object current = enumerator.current();
//            Object[] values = (Object[]) current;
//            StringBuilder sb = new StringBuilder();
//            for (Object v : values) {
//                sb.append(v).append(",");
//            }
//            sb.setLength(sb.length() - 1);
//            System.out.println(sb);
//        }
//    }

}
