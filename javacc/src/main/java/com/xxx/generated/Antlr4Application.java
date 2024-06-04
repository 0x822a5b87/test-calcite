package com.xxx.generated;

import com.xxx.g4.example.CalciteRulesLexer;
import com.xxx.g4.example.CalciteRulesParser;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.UnbufferedCharStream;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.util.SqlVisitor;

import java.io.IOException;

public class Antlr4Application {
    public void parser() throws IOException {
        UnbufferedCharStream              input   = new UnbufferedCharStream(System.in);
        CalciteRulesLexer                 lexer   = new CalciteRulesLexer(input);
        CommonTokenStream                 tokens  = new CommonTokenStream(lexer);
        CalciteRulesParser                parser  = new CalciteRulesParser(tokens);
        CalciteRulesParser.ProgramContext program = parser.program();
    }
}
