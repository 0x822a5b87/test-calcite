package com.xxx.ch05;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class AvaticaClient {

    public void connect() throws SQLException {
        final Properties properties = new Properties();
        properties.put("avatica_user", "USER1");
        properties.put("avatica_password", "password1");
        properties.put("serialization", "protobuf");

        try (Connection connection = DriverManager.getConnection("jdbc:avatica:remote:url=http://localhost:8765;"
                                                 + "authentication=DIGEST", properties)) {

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from db limit 1;");
            if (rs.next()) {
                // 这里有个问题需要注意
                // 如果mysql的字段类型为 char(255)，这里会读取255长度的字符串
                System.out.printf("[%s]\n", rs.getString("Host"));
                System.out.printf("[%s]\n", rs.getString("Db"));
                System.out.printf("[%s]\n", rs.getString("User"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws SQLException {
        AvaticaClient c = new AvaticaClient();
        c.connect();
    }
}
