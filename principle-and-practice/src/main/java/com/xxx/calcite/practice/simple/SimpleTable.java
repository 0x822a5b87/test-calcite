package com.xxx.calcite.practice.simple;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.file.CsvEnumerator;
import org.apache.calcite.adapter.file.CsvFieldType;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelDataTypeFieldImpl;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.rel.type.StructKind;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.ImmutableIntList;
import org.apache.calcite.util.Source;
import org.apache.calcite.util.Sources;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleTable extends AbstractTable implements ScannableTable {

    private final String            tableName;
    private final String            filePath;
    private final List<String>      fieldNames;
    private final List<SqlTypeName> fieldTypes;

    public SimpleTable(String tableName, String filePath,
                       List<String> fieldNames,
                       List<SqlTypeName> typeNames) {
        this.tableName  = tableName;
        this.filePath   = filePath;
        this.fieldNames = fieldNames;
        this.fieldTypes = typeNames;
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        List<RelDataTypeField> fields = new ArrayList<>();
        for (int i = 0; i < fieldTypes.size(); i++) {
            RelDataType      fieldType = typeFactory.createSqlType(fieldTypes.get(i));
            RelDataTypeField field     = new RelDataTypeFieldImpl(fieldNames.get(i), i, fieldType);
            fields.add(field);
        }
        return new RelRecordType(StructKind.PEEK_FIELDS, fields, true);
    }

    @Override
    public Enumerable<Object[]> scan(DataContext root) {
        return new AbstractEnumerable<Object[]>() {
            @Override
            public Enumerator<Object[]> enumerator() {
                URL           url        = ClassLoader.getSystemClassLoader().getResource(filePath);
                Preconditions.checkNotNull(url, "error init source : " + filePath);
                Source        source     = Sources.of(url);
                AtomicBoolean cancelFlag = DataContext.Variable.CANCEL_FLAG.get(root);
                return new CsvEnumerator<>(source, cancelFlag,
                                           buildCsvFieldTypes(root.getTypeFactory()),
                                           buildFieldIndexes());
            }
        };
    }

    private List<CsvFieldType> buildCsvFieldTypes(RelDataTypeFactory factory) {
        List<CsvFieldType> csvFieldTypes = new ArrayList<>();
        for (SqlTypeName fieldType : fieldTypes) {
            RelDataType  relDataType  = factory.createSqlType(fieldType);
            CsvFieldType csvFieldType = CsvFieldType.of(relDataType.getFullTypeString());
            csvFieldTypes.add(csvFieldType);
        }
        return csvFieldTypes;
    }

    private List<Integer> buildFieldIndexes() {
        return ImmutableIntList.identity(fieldNames.size());
    }

    public String getTableName() {
        return tableName;
    }

    public static Builder newBuilder(String tableName, String filePath) {
        return new Builder(tableName, filePath);
    }

    public static class Builder {
        private final String            tableName;
        private final String            filePath;
        private final List<String>      fieldNames = new ArrayList<>();
        private final List<SqlTypeName> typeNames  = new ArrayList<>();

        private Builder(String tableName, String filePath) {
            this.tableName = Preconditions.checkNotNull(tableName);
            this.filePath  = Preconditions.checkNotNull(filePath);
        }

        public Builder addField(String fieldName, SqlTypeName typeName) {
            if (Strings.isNullOrEmpty(fieldName)) {
                throw new IllegalArgumentException("Field name cannot be null or empty");
            }
            if (fieldNames.contains(fieldName)) {
                throw new IllegalArgumentException("field already exists : " + fieldName);
            }
            fieldNames.add(fieldName);
            typeNames.add(typeName);
            return this;
        }

        public SimpleTable build() {
            return new SimpleTable(tableName, filePath, fieldNames, typeNames);
        }
    }
}
