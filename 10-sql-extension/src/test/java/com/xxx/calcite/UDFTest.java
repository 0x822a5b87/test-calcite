package com.xxx.calcite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UDFTest {

    @Test
    public void testAddUdf() throws SQLException {
        Connection connection = getConnection("model.json");
        Statement  st         = connection.createStatement();
        ResultSet  resultSet  = st.executeQuery("SELECT MY_SUB_STRING2(\"Name\", 0, 3) from CSV.\"data\" where \"Id\"=1");
        while (resultSet.next()) {
            String name = resultSet.getString(1);
            System.out.println(name);
        }
    }


    @Test
    public void testAddUdaf() throws SQLException {
        Connection connection = getConnection("model.json");
        Statement  st         = connection.createStatement();
        ResultSet  resultSet  = st.executeQuery("SELECT COLLECT_LIST(\"Name\") from CSV.\"data\"");
        while (resultSet.next()) {
            String name = resultSet.getString(1);
            System.out.println(name);
        }
    }

    @Test
    public void testExplode() throws SQLException {
        String sql = "SELECT C FROM table ( EXPLODE('hello world', ' ') ) as t(C) WHERE C = 'hello'";
        Connection connection = getConnection("model.json");
        Statement  st         = connection.createStatement();
        ResultSet  resultSet  = st.executeQuery(sql);
        while (resultSet.next()) {
            String value = resultSet.getString(1);
            Assertions.assertEquals("hello", value);
        }
    }

    private Connection getConnection(String modelPath) throws SQLException {
        URL url = ClassLoader.getSystemClassLoader().getResource(modelPath);
        if (url == null) {
            throw new IllegalArgumentException("model.json not found!");
        }
        return DriverManager.getConnection("jdbc:calcite:model=" + url.getPath());
    }
}
