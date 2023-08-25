SqlNode SqlLoad() :
{
    SqlParserPos pos;
    SqlIdentifier sourceType;
    String sourceObj;
    SqlIdentifier targetType;
    String targetObj;
    SqlParserPos mapPos;
    SqlNodeList colMapping;
    SqlColMapping colMap;
    String separator = "\t";
}
{
    // LOAD 关键字
    <LOAD>
    {
        pos = getPos();
    }
    // 解析source identifier
    sourceType = CompoundIdentifier()
    <COLON>
    sourceObj = StringLiteralValue()
    <TO>
    targetType = CompoundIdentifier()
    <COLON>
    targetObj = StringLiteralValue()
    {
        mapPos = getPos();
    }
    <LPAREN>
    {
        colMapping = new SqlNodeList(mapPos);
        colMapping.add(readOneColMapping());
    }
    (
        <COMMA>
        {
            colMapping.add(readOneColMapping());
        }
    )*
    <RPAREN>
    [<SEPARATOR> separator=StringLiteralValue()]
    {
        return new SqlLoad(pos, new SqlLoadSource(sourceType, sourceObj),
                new SqlLoadSource(targetType, targetObj), colMapping, separator);
    }
}


JAVACODE String StringLiteralValue() {
    SqlNode sqlNode = StringLiteral();
    return ((NlsString) SqlLiteral.value(sqlNode)).getValue();
}

SqlNode readOneColMapping():
{
    SqlIdentifier fromCol;
    SqlIdentifier toCol;
    SqlParserPos pos;
}
{
    { pos = getPos();}
    fromCol = SimpleIdentifier()
    toCol = SimpleIdentifier()
    {
        return new SqlColMapping(pos, fromCol, toCol);
    }
}
