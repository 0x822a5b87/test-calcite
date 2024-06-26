package com.xxx.calcite.redis;


import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

class RedisTest {
    private RedisServerStart redisServer;

    private void query(String sql, int expected) throws SQLException {
        URL url = ClassLoader.getSystemClassLoader().getResource("model.json");
        Connection connection =
                DriverManager.getConnection("jdbc:calcite:model=" + url.getPath());
        Statement st = connection.createStatement();
        ResultSet resultSet = st.executeQuery(sql);
        int count = 0;
        while (resultSet.next()) {
            count += 1;
        }
        Assertions.assertEquals(expected, count);
    }

    @BeforeEach
    void startUp() throws Exception {
        redisServer = RedisServerStart.getInstance();
        redisServer.startServer();
    }

    @AfterEach
    void stop() throws IOException {
        redisServer.stopServer();
    }

    @Test
    public void testServer() throws SQLException, ClassNotFoundException {
        query("select name from \"stu_01\"", 2);
    }


}
