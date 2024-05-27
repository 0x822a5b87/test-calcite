package com.xxx.generated;

import com.xxx.generated.lookahead.Lookahead;
import com.xxx.generated.lookahead.ParseException;

import java.io.StringReader;

public class GeneratedApplication {
    public static void main(String[] args) throws ParseException {
        // 输入源
        String input = "hello,world,123";

        // 创建词法分析器实例
        Lookahead lexer = new Lookahead(new StringReader(input));

        lexer.list1();

        // 获取并处理词法单元
        com.xxx.generated.lookahead.Token token;
        while ((token = lexer.getNextToken()) != null) {
            System.out.println(token);
            if (token.kind == Lookahead.EOF) {
                break;
            }
        }
    }
}
