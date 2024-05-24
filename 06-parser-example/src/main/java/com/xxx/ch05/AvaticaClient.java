package com.xxx.ch05;

import java.sql.Connection;
import java.sql.DriverManager;
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
            statement.execute("SHOW databases;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws SQLException {
        AvaticaClient c = new AvaticaClient();
        c.connect();
    }
}
