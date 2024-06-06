package com.xxx.calcite.ds.csv;

import com.xxx.calcite.util.PrintUtil;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

class CsvTableTest {

    @Test
    void testQuery() throws SQLException {
        URL url = ClassLoader.getSystemClassLoader().getResource("model.json");
        assert url != null;
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:model=" + url.getPath())) {
            Statement st = connection.createStatement();
            PrintUtil.printResult(st.executeQuery("select \"Id\" from CSV.\"data\" group by \"Id\" having \"Id\" =1"));
        }
    }
}
