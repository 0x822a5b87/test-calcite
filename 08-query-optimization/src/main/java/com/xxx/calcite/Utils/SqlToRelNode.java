package com.xxx.calcite.Utils;

import org.apache.calcite.config.Lex;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelRule;
import org.apache.calcite.plan.hep.HepPlanner;
import org.apache.calcite.plan.hep.HepProgram;
import org.apache.calcite.plan.hep.HepProgramBuilder;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.prepare.PlannerImpl;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.util.SqlOperatorTables;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.sql2rel.StandardConvertletTable;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

import java.util.List;

public class SqlToRelNode {
    public static SqlToRelConverter createSqlToRelConverter(SqlParser.Config parserConfig,
                                                            SqlToRelConverter.Config sqlToRelConverterConfig,
                                                            RelOptPlanner planner) {

        PlannerImpl plannerImpl = new PlannerImpl(Frameworks
                .newConfigBuilder()
                .sqlToRelConverterConfig(sqlToRelConverterConfig)
                .parserConfig(parserConfig)
                .build());
        SqlTypeFactoryImpl factory = new SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);

        RexBuilder rexBuilder = new RexBuilder(new JavaTypeFactoryImpl());
        RelOptCluster cluster = RelOptCluster.create(planner, rexBuilder);
        CalciteCatalogReader catalogReader = CatalogReaderUtil.createCatalogReader(parserConfig);
        final SqlStdOperatorTable instance = SqlStdOperatorTable.instance();
        SqlValidator validator = SqlValidatorUtil.newValidator(SqlOperatorTables.chain(instance, catalogReader),
                catalogReader, factory, SqlValidator.Config.DEFAULT.withIdentifierExpansion(true));

        return new SqlToRelConverter(
                plannerImpl,
                validator,
                catalogReader,
                cluster,
                StandardConvertletTable.INSTANCE,
                sqlToRelConverterConfig);
    }

    public static RelRoot createRelRoot(SqlParser.Config parserConfig,
                                        SqlToRelConverter.Config sqlToRelConverterConfig,
                                        RelOptPlanner planner,
                                        SqlNode sqlQuery) {
        SqlToRelConverter sqlToRelConverter = createSqlToRelConverter(parserConfig,
                sqlToRelConverterConfig, planner);
        return sqlToRelConverter.convertQuery(sqlQuery, true, true);
    }

    public static RelNode getSqlNode(String sql, RelOptPlanner relOptPlanner) throws SqlParseException {
        final FrameworkConfig frameworkConfig = Frameworks.newConfigBuilder().build();
        final Planner planner = Frameworks.getPlanner(frameworkConfig);
        SqlNode sqlNode = planner.parse(sql);
        SqlParser.Config sqlConfig = SqlParser.config().withLex(Lex.MYSQL).withCaseSensitive(false);
        SqlToRelConverter.Config config = SqlToRelConverter.config();
        return SqlToRelNode.createRelRoot(sqlConfig, config, relOptPlanner, sqlNode).rel;
    }

    public static RelNode findHepBestExp(RelNode node, List<RelRule.Config> ruleConfigs) {
        HepPlanner planner = newHepPlanner(ruleConfigs);
        planner.setRoot(node);
        return planner.findBestExp();
    }

    public static RelNode findUnoptimizedExp(String sql, List<RelRule.Config> ruleConfigs) throws SqlParseException {
        HepPlanner planner = newHepPlanner(ruleConfigs);
        return getSqlNode(sql, planner);
    }

    private static HepPlanner newHepPlanner(List<RelRule.Config> ruleConfigs) {
        HepProgramBuilder builder = HepProgram.builder();
        for (RelRule.Config config : ruleConfigs) {
            builder.addRuleInstance(config.toRule());
        }
        HepProgram program = builder.build();
        return new HepPlanner(program);
    }

    public static RelNode findVolcanoBestExp(RelNode node, List<RelRule.Config> ruleConfigs) {
        RelOptPlanner planner = node.getCluster().getPlanner();
        for (RelRule.Config config : ruleConfigs) {
            planner.addRule(config.toRule());
        }
        planner.setRoot(node);
        return planner.findBestExp();
    }

    private static VolcanoPlanner newVolcanoPlanner(List<RelRule.Config> ruleConfigs) {
        VolcanoPlanner planner = new VolcanoPlanner();
        for (RelRule.Config config : ruleConfigs) {
            planner.addRule(config.toRule());
        }
        return planner;
    }
}
