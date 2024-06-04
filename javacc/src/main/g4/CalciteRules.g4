grammar CalciteRules;

program : stmt SEMICOLON ? EOF;

// 定义load规则
stmt : loadStmt;

loadStmt : LOAD loadFromStmt TO loadToStmt loadColumns (SEPARATOR STRING)?;

loadFromStmt : IDENTIFIER COLON STRING;

loadToStmt : IDENTIFIER COLON STRING;

loadColumns : OPEN_P columnsItem CLOSE_P;

columnsItem : (IDENTIFIER IDENTIFIER) (COMA IDENTIFIER IDENTIFIER)+;

LOAD : 'load';
TO : 'to';
SEPARATOR : 'seperator';
COLON: ':';
COMA : ',';
OPEN_P : '(';
CLOSE_P : ')';
SEMICOLON : ';';

fragment LETTER:[a-zA-Z]+;

// begin with ' and end with '
STRING : '\'' (~('\'' | '\\') | ('\\' .))* '\''
        | '"' (~('"' | '\\') | ('\\' .))* '"';

IDENTIFIER : LETTER;
