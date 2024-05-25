package com.xxx;

import com.xxx.generated.Example;
import com.xxx.generated.ParseException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Application {
    public static void main(String[] args) throws ParseException {
        String      words       = "{{}}";
        byte[]      bytes       = words.getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        Example     example     = new Example(inputStream);
        example.Input();
    }
}
