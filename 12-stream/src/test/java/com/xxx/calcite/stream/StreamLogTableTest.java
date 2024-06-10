package com.xxx.calcite.stream;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

@Slf4j
class StreamLogTableTest {

    private void printResult(ResultSet rs) throws SQLException {
        final ResultSetMetaData md = rs.getMetaData();
        for (int i = 0; i < md.getColumnCount(); i++) {
            System.out.print(md.getColumnLabel(i + 1) + "\t");
        }
        System.out.println("\n------------------------------------------");
        while (rs.next()) {
            for (int i = 0; i < md.getColumnCount(); i++) {
                System.out.print(rs.getObject(i + 1) + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    @Test
    void testStreamQuery() throws SQLException {
        URL url = ClassLoader.getSystemClassLoader().getResource("model.json");
        assert url != null;
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:model=" + url.getPath())) {
            final Statement stmt = connection.createStatement();
            final ResultSet rs = stmt.executeQuery("select STREAM * from LOG");
            // 永无止境的输出
            printResult(rs);
        }
    }

    @Test
    void testStreamWithCancel() throws SQLException {
        URL url = ClassLoader.getSystemClassLoader().getResource("model.json");
        assert url != null;
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:model=" + url.getPath())) {
            final Statement stmt = connection.createStatement();
            final ResultSet rs = stmt.executeQuery("select STREAM * from LOG");
            // 开启一个定时停止线程
            new Thread(() -> {
                try {
                    // 5秒后停止
                    TimeUnit.SECONDS.sleep(5);
                    stmt.cancel();
                } catch (InterruptedException | SQLException e) {
                    e.printStackTrace();
                }
            }).start();
            // 永无止境的输出
            try {
                printResult(rs);
            } catch (SQLException e) {
                // ignore end
            }
        }
    }

    @Test
    void testStreamGroupBy() throws SQLException {
        URL url = ClassLoader.getSystemClassLoader().getResource("model.json");
        assert url != null;
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:model=" + url.getPath())) {
//            final Statement stmt = connection.createStatement();
//            final ResultSet rs = stmt.executeQuery("select STREAM level,count(*) from LOG group by level");

            final Statement stmt = connection.createStatement();
            final ResultSet rs = stmt.executeQuery("select STREAM FLOOR(log_time TO SECOND) as " +
                    "log_time,level,count(*) as c from LOG group by FLOOR(log_time TO SECOND)," +
                    "level");
            printResult(rs);
        }
    }

    @Test
    void testStreamGroupByCache() throws SQLException, InterruptedException {
        URL url = ClassLoader.getSystemClassLoader().getResource("model.json");
        assert url != null;
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:model=" + url.getPath())) {
            // 模拟查询10次
            for (int i = 0; i < 10; i++) {
                TimeUnit.SECONDS.sleep(1);
                final Statement stmt = connection.createStatement();
                final ResultSet rs = stmt.executeQuery("select STREAM FLOOR(log_time TO MINUTE) as " +
                        "log_time,level,count(*) as c from LOG_CACHE group by FLOOR(log_time TO MINUTE)," +
                        "level");
                printResult(rs);
            }
        }
    }
}
