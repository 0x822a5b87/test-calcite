package com.xxx;

import com.xxx.generated.ParseException;
import com.xxx.generated.lexical.MyLexical;
import com.xxx.generated.lexical.MyLexicalConstants;
import com.xxx.generated.lexical.Token;

import java.io.StringReader;

public class Application {
    public static void main(String[] args) throws ParseException {
        // 输入源
        String input = "\"hello world\"";

        // 创建词法分析器实例
        MyLexical lexer = new MyLexical(new StringReader(input));

        // 获取并处理词法单元
        Token token;
        while ((token = lexer.getNextToken()) != null) {
            if (token.kind == MyLexicalConstants.ABCD) {
                // 在识别到 "cd" 时返回 image
                System.out.println(token.image);
            }
            if (token.kind == MyLexicalConstants.EOF) {
                break;
            }
        }
    }
}
