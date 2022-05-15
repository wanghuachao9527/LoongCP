package org.loong;

import org.loong.exception.LoongPoolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class LoongDataSource implements DataSource, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoongDataSource.class);

    private LoongConfig config;

    private LoongPool pool;

    public LoongDataSource(LoongConfig loongConfig) {
        loongConfig.validate();
        this.config = loongConfig;
        this.pool = new LoongPool(this.config);
        LOGGER.info("[loong-pool 提示] 创建连接池完毕，最小空闲连接数:【{}】,最大连接数:【{}】.",
                this.config.getMinIdle(), this.config.getMaxPoolSize());
    }

    /** ------------------下面都是DataSource的实现方法-------------------------- */
    @Override
    public void close() throws IOException {
        this.pool.shutdown();
    }

    @Override
    public Connection getConnection() throws SQLException {
        LoongConnection connection;
        try {
            connection = this.pool.borrowConnection();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LoongPoolException("[loong-pool 异常] 获取数据库连接的线程已被中断!", e);
        }
        // 获取当前最新的时间，并将借用数 +1，
        final long endNanoTime = System.nanoTime();
        connection.setLastBorrowNanoTime(endNanoTime);
        this.pool.getLastActiveNanoTime().lazySet(endNanoTime);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.getConnection();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return DriverManager.getDrivers().nextElement().getParentLogger();
    }
}
