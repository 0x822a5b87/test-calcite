package com.xxx;

import com.google.protobuf.WireFormat;
import lombok.Getter;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * LOAD方法的根节点。
 * <p/>
 *
 * @author 0x822a5b87
 */
@Getter
public class SqlLoad extends SqlCall {

    /**
     * load 数据源
     */
    private SqlLoadSource source;
    /**
     * load sink
     */
    private SqlLoadSource sink;
    /**
     * source 和 sink 字段的映射关系
     */
    private SqlNodeList   colMapping;
    /**
     * 分隔符
     */
    private String        separator;

    public SqlLoad(SqlParserPos pos,
                   SqlLoadSource source,
                   SqlLoadSource sink,
                   SqlNodeList colMapping,
                   String separator) {
        super(pos);
        this.source     = source;
        this.sink       = sink;
        this.colMapping = colMapping;
        this.separator  = separator;
    }

    public SqlLoad(SqlParserPos pos) {
        super(pos);
    }

    @Nonnull
    @Override
    public SqlOperator getOperator() {
        //TODO 实现逻辑
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
        writer.print("LOAD ");
        source.getType().unparse(writer, leftPrec, rightPrec);
        writer.print(":");
        writer.print("'" + source.getObj() + "'");
        writer.print(" TO ");
        sink.getType().unparse(writer, leftPrec, rightPrec);
        writer.print(":");
        writer.print("'" + sink.getObj() + "'");
        SqlWriter.Frame frame = writer.startList("(", ")");
        for (SqlNode sqlNode : colMapping) {
            writer.newlineAndIndent();
            writer.sep(",", false);
            sqlNode.unparse(writer, leftPrec, rightPrec);
        }
        writer.endList(frame);
        writer.keyword(" SEPARATOR ");
        writer.print("'" + separator + "'");
    }
}
