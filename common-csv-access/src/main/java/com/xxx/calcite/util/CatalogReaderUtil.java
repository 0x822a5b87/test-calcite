package com.xxx.calcite.util;

import com.xxx.calcite.ds.csv.CsvSchema;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.tools.Frameworks;

import java.util.Properties;

public class CatalogReaderUtil {
    public static CalciteCatalogReader createCatalogReader(SqlParser.Config parserConfig) {
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        rootSchema.add("csv",new CsvSchema("data_01.csv"));
        return createCatalogReader(parserConfig, rootSchema);
    }

    public static CalciteCatalogReader createCatalogReader(SqlParser.Config parserConfig, SchemaPlus rootSchema) {

        Properties prop = new Properties();
        prop.setProperty(CalciteConnectionProperty.CASE_SENSITIVE.camelName(),
                String.valueOf(parserConfig.caseSensitive()));
        CalciteConnectionConfigImpl calciteConnectionConfig = new CalciteConnectionConfigImpl(prop);
        return new CalciteCatalogReader(
                CalciteSchema.from(rootSchema),
                CalciteSchema.from(rootSchema).path(null),
                new JavaTypeFactoryImpl(),
                calciteConnectionConfig
        );
    }

}
