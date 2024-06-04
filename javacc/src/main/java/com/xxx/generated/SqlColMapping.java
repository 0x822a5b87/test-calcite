package com.xxx.generated;

import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import java.util.ArrayList;
import java.util.List;

public class SqlColMapping extends SqlCall {

    @lombok.Getter protected static final SqlOperator OPERATOR =
            new SqlSpecialOperator("SqlColMapping", SqlKind.OTHER);

    private SqlIdentifier fromCol;
    private SqlIdentifier toCol;

    public SqlColMapping(SqlParserPos pos) {
        super(pos);
    }

    @Override
    public SqlOperator getOperator() {
        return OPERATOR;
    }

    public SqlColMapping(SqlParserPos pos, SqlIdentifier fromCol, SqlIdentifier toCol) {
        super(pos);
        this.fromCol = fromCol;
        this.toCol = toCol;
    }

    @Override
    public List<SqlNode> getOperandList() {
        ArrayList<SqlNode> nodes = new ArrayList<>();
        nodes.add(fromCol);
        nodes.add(toCol);
        return nodes;
    }

    @Override
    public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
        fromCol.unparse(writer, leftPrec, rightPrec);
        writer.print(" ");
        toCol.unparse(writer, leftPrec, rightPrec);
    }
}
