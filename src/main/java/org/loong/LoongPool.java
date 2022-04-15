package org.loong;

import org.loong.exception.LoongPoolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.LongAdder;

public class LoongPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoongPool.class);

    private final LoongConfig config;

    private final BlockingQueue<LoongConnection> connectionQueue;

    private final ScheduledExecutorService scheduledExecutor;

    private final LongAdder borrowCount;

    public LoongPool(LoongConfig loongConfig) {
        this.config = loongConfig;
        this.connectionQueue = new ArrayBlockingQueue<>(loongConfig.getMaxPoolSize());
        this.borrowCount = new LongAdder();

        this.scheduledExecutor = new ScheduledThreadPoolExecutor(
                1, r -> new Thread(r, "blink-pool"),
                (r, e) -> LOGGER.warn("[blink-pool 警告] 已经启动或运行了用于维持空闲连接的定时任务!"));

        try {
            this.startKeepIdleConnectionsJob();
        } catch (Exception e) {
            // 如果启动失败，则直接关闭 executor，并抛出异常.
            this.scheduledExecutor.shutdownNow();
            throw e;
        }
    }

    private void initCreateIdleConnections() {
        // 初始化创建一个连接.
        try {
            this.createBlinkConnectionIntoPool();
        } catch (SQLException e) {
            throw new LoongPoolException("[blink-pool 异常] 初始化创建数据库连接时发生异常！", e);
        }
    }

    /**
     * 创建出最小可用的数据库连接数.
     */
    private void createMinIdleConnections() {
//        this.createConnLock.lock();
        try {
            while (this.connectionQueue.size() < this.config.getMinIdle()) {
                this.createBlinkConnectionIntoPool();
            }
        } catch (SQLException e) {
            throw new LoongPoolException("[blink-pool 异常] 创建数据库连接时发生异常！", e);
        } finally {
//            this.createConnLock.unlock();
        }
    }

    private void createBlinkConnectionIntoPool() throws SQLException {
        // 如果当前所有连接总数都小于最大连接数，那么就创建新的数据库连接，并放到连接池中.
        if ((this.connectionQueue.size() + this.borrowCount.intValue()) < this.config.getMaxPoolSize()) {
            LoongConnection connection = this.newCamilleConnection();
            if (!this.connectionQueue.offer(connection)) {
                connection.closeReally();
                LOGGER.debug("[blink-pool 提示] 连接池已满,无法再将该数据库连接放到连接池中，将直接关闭该连接！");
            }
        }
    }

    private LoongConnection newCamilleConnection() throws SQLException {
        LoongConnection connection = new LoongConnection(this.config);
//        this.stats.getCreations().increment();
        // TODO
        LOGGER.debug("");
        return connection;
    }
}
