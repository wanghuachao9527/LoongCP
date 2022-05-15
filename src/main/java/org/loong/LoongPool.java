package org.loong;

import org.loong.exception.LoongPoolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoongPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoongPool.class);

    private final LoongConfig config;

    private final BlockingQueue<LoongConnection> connectionQueue;

    private final ScheduledExecutorService scheduledExecutor;

    private final LongAdder borrowCount;

    private final Lock createConnLock;

    public LoongPool(LoongConfig loongConfig) {
        this.config = loongConfig;
        this.connectionQueue = new ArrayBlockingQueue<>(loongConfig.getMaxPoolSize());
        this.borrowCount = new LongAdder();
        this.createConnLock = new ReentrantLock();
        this.scheduledExecutor = new ScheduledThreadPoolExecutor(1);

//        final int maxPoolSize = config.getMaximumPoolSize();
    }

    private void initCreateIdleConnections() {
        this.createConnLock.lock();
        try {
            while (this.connectionQueue.size() < this.config.getMinIdle()) {
                // 如果当前所有连接总数都小于最大连接数，那么就创建新的数据库连接，并放到连接池中.
                if ((this.connectionQueue.size() + this.borrowCount.intValue()) < this.config.getMaxPoolSize()) {
                    LoongConnection connection = this.newLoongConnection();
                    if (!this.connectionQueue.offer(connection)) {
                        connection.closeReally();
                        LOGGER.debug("[blink-pool 提示] 连接池已满,无法再将该数据库连接放到连接池中，将直接关闭该连接！");
                    }
                }
            }
        } catch (SQLException e) {
            throw new LoongPoolException("[blink-pool 异常] 创建数据库连接时发生异常！", e);
        } finally {
            this.createConnLock.unlock();
        }
    }

    private LoongConnection newLoongConnection() throws SQLException {
        LoongConnection connection = new LoongConnection(this.config, this);
        // TODO
        LOGGER.debug("");
        return connection;
    }

    /**
     * 从池中拿出一个
     */
    public void borrowConnection(LoongConnection conn) {
        return;
    }

    /**
     * 将释放的连接放回池中
     */
    public void returnConnection(LoongConnection conn) {
        this.borrowCount.decrement();
    }

    /**
     * 关闭连接池中的若干连接.
     */
    public synchronized void shutdown() {
        // 设置已关闭，并关闭定时任务的调度器.
//        this.closed = true;
        this.scheduledExecutor.shutdown();

        // 循环关闭和清空连接池.
        this.connectionQueue.forEach(LoongConnection::closeReally);
        this.connectionQueue.clear();
    }
}
