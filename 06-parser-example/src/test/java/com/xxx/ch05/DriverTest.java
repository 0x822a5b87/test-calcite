package com.xxx.ch05;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DriverTest {

    @Test
    void connectTest() throws SQLException, ClassNotFoundException {
        Class.forName("com.xxx.ch05.Driver");
        final Properties p = new Properties();
        p.put("avatica_user", "USER1");
        p.put("avatica_password", "password1");
        p.put("serialization", "protobuf");
        try (Connection conn = DriverManager.getConnection("jdbc:cdm:remote:url=http://localhost:8765;" +
                                                           "authentication=DIGEST", p)) {
            final Statement statement = conn.createStatement();
            final ResultSet rs        = statement.executeQuery("SHOW DATABASES");
            assertTrue(rs.next());
            // 查询数据
            final Statement stmt1 = conn.createStatement();
            final ResultSet rs1 = stmt1.executeQuery("SELECT * FROM sys_user");
            rs1.next();
            assertEquals("admin", rs1.getString("user_name"));
        }
    }
}
