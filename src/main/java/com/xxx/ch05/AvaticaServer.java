package com.xxx.ch05;

import org.apache.calcite.avatica.jdbc.JdbcMeta;
import org.apache.calcite.avatica.remote.LocalService;
import org.apache.calcite.avatica.server.HttpServer;
import org.apache.calcite.sql.SqlNode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;

public class AvaticaServer {

    public final static int PORT = 8765;
    public static void main(String[] args) throws SQLException, InterruptedException, UnsupportedEncodingException {
        String             url     = "jdbc:mysql://localhost:3306/test";
        final JdbcMeta     meta    = new JdbcMeta(url, "root", "123456");
        final LocalService service = new LocalService(meta);

        final HttpServer server = new HttpServer.Builder<>()
                .withPort(PORT)
                .withHandler(service, Driver.Serialization.PROTOBUF)
                .withDigestAuthentication(readAuthProperties(), new String[]{"users"})
                .build();

        server.start();
        server.join();
    }

    private static String readAuthProperties() throws UnsupportedEncodingException, UnsupportedEncodingException {
        return URLDecoder.decode(AvaticaServer.class.getResource("/auth-users.properties").getFile(), "UTF-8");
    }

}
