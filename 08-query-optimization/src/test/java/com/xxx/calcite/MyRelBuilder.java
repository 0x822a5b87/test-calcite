package com.xxx.calcite;

import com.xxx.calcite.ds.csv.CsvSchema;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.JoinRelType;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;
import org.junit.jupiter.api.Test;

class MyRelBuilder {

    @Test
    public void joinTest() {
        final FrameworkConfig config = MyRelBuilder.config("STUDENT.csv,SCORE.csv,CITY.csv,SCHOOL.csv").build();
        final RelBuilder builder = RelBuilder.create(config);
        final RelNode left = builder
                .scan("STUDENT")
                .scan("SCORE")
                .join(JoinRelType.INNER, "Id")
                .build();

        final RelNode right = builder
                .scan("CITY")
                .scan("SCHOOL")
                .join(JoinRelType.INNER, "Id")
                .build();

        final RelNode result = builder
                .push(left)
                .push(right)
                .join(JoinRelType.INNER, "Id")
                .build();
        System.out.println(RelOptUtil.toString(result));
    }

    @Test
    public void projectWithFilterTest() {
        final FrameworkConfig config = MyRelBuilder.config().build();
        final RelBuilder builder = RelBuilder.create(config);
        final RelNode node = builder
                .scan("data")
                // project Name field and Score field
                .project(builder.field("Name"), builder.field("Score"))
                // predicates : Score > 90
                .filter(builder.call(SqlStdOperatorTable.GREATER_THAN,
                        builder.field("Score"),
                        builder.literal(90)))
                .build();
        System.out.println(RelOptUtil.toString(node));
    }

    @Test
    public void scanTest() {
        final FrameworkConfig config = MyRelBuilder.config().build();
        final RelBuilder builder = RelBuilder.create(config);
        final RelNode node = builder
                .scan("data")
                .build();
        System.out.println(RelOptUtil.toString(node));
    }

    public static Frameworks.ConfigBuilder config() {
        return config("data.csv,CITY.csv");
    }

    public static Frameworks.ConfigBuilder config(String dataFile) {
        // create a root schema
        final SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        SchemaPlus rootSchemaPlus = rootSchema.add("csv", new CsvSchema(dataFile));
        return Frameworks.newConfigBuilder()
                         // define the configuration for a SQL parser, use Oracle lexer
                         .parserConfig(SqlParser.Config.DEFAULT)
                         // add schema csv, and table name data to rootSchema
                         .defaultSchema(rootSchemaPlus);
    }
}
