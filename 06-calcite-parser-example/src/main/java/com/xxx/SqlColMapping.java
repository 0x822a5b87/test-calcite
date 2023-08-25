package com.xxx;

import lombok.Getter;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author 0x822a5b87
 */
@Getter
public class SqlColMapping extends SqlCall {

    /**
     * TODO 搞清楚这个OPERATOR的作用
     */
    protected static final SqlOperator OPERATOR = new SqlSpecialOperator("SqlColMapping", SqlKind.OTHER);

    private SqlIdentifier fromCol;

    private SqlIdentifier toCol;

    public SqlColMapping(SqlParserPos pos) {
        super(pos);
    }
    public SqlColMapping(SqlParserPos pos, SqlIdentifier fromCol, SqlIdentifier toCol) {
        super(pos);
        this.fromCol = fromCol;
        this.toCol   = toCol;
    }

    @Nonnull
    @Override
    public SqlOperator getOperator() {
        // TODO 实现逻辑
        return null;
    }

    @Nonnull
    @Override
    public List<SqlNode> getOperandList() {
        //TODO 实现逻辑
        return null;
    }

    @Override
    public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
        fromCol.unparse(writer, leftPrec, rightPrec);
        writer.print(" ");
        toCol.unparse(writer, leftPrec, rightPrec);
    }
}
