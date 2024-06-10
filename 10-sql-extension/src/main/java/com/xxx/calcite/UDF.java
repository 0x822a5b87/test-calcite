package com.xxx.calcite;

import org.apache.calcite.linq4j.function.Parameter;

public class UDF {

    public String mySubString(String str) {
        return str.substring(0, 2);
    }

    public String mySubString2(
            @Parameter(name = "S") String s,
            @Parameter(name = "B") Integer beginIndex,
            @Parameter(name = "E", optional = true) Integer endIndex) {

        if (endIndex == null) {
            endIndex = s.length();
        }

        return s.substring(beginIndex, endIndex);
    }
}
