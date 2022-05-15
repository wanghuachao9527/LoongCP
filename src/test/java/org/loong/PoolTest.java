package org.loong;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PoolTest {

    @Before
    public void setup() throws SQLException {



//        try (LoongDataSource ds = new LoongDataSource(config);
//             Connection conn = ds.getConnection();
//             Statement stmt = conn.createStatement()) {
//            stmt.execute("DROP TABLE IF EXISTS basic_pool_test");
//            stmt.execute("CREATE TABLE basic_pool_test ("
//                    + "id INTEGER NOT NULL PRIMARY KEY, "
//                    + "timestamp TIMESTAMP, "
//                    + "string VARCHAR(128), "
//                    + "string_from_number NUMERIC "
//                    + ")");
//        }

    }

    @Test
    public void test() {
        LoongConfig config = new LoongConfig();
        config.setMinIdle(5);
        config.setMaxPoolSize(10);
        config.setConnectionTestQuery("SELECT 1");
        config.setDriverClassName("com.mysql.jdbc.Driver");

        config.setUrl("jdbc:mysql://bj-cdb-o35dlvd4.sql.tencentcdb.com:60205/study_room?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowMultiQueries=true");
        config.setUsername("root");
        config.setPassword("Lily685457***");

        LoongDataSource ds = new LoongDataSource(config);
        System.out.println("测试");
    }
}
