package com.xxx.calcite.ds.csv;

import org.apache.calcite.linq4j.function.Parameter;

public class UDF {

    public static String mySubString2(
            @Parameter(name = "S") String s,
            @Parameter(name = "B") Integer beginIndex,
            @Parameter(name = "E") Integer endIndex) {

        if (endIndex == null) {
            endIndex = s.length();
        }

        return s.substring(beginIndex, endIndex);
    }

    public static String[] mySplit(String value, String delimiter) {
        return value.split(delimiter);
    }
}
