package org.loong;

import org.loong.exception.LoongPoolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
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

    private AtomicLong lastActiveNanoTime;

    public LoongPool(LoongConfig loongConfig) {
        this.config = loongConfig;
        this.connectionQueue = new ArrayBlockingQueue<>(loongConfig.getMaxPoolSize());
        this.borrowCount = new LongAdder();
        this.createConnLock = new ReentrantLock();
        this.lastActiveNanoTime = new AtomicLong();
        this.scheduledExecutor = new ScheduledThreadPoolExecutor(1);

        this.initCreateIdleConnections();
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
                        LOGGER.debug("[loong-pool 提示] 连接池已满,无法再将该数据库连接放到连接池中，将直接关闭该连接！");
                    }
                }
            }
        } catch (SQLException e) {
            throw new LoongPoolException("[loong-pool 异常] 创建数据库连接时发生异常！", e);
        } finally {
            this.createConnLock.unlock();
        }
    }

    private LoongConnection newLoongConnection() throws SQLException {
        LoongConnection connection = new LoongConnection(this.config, this);
        return connection;
    }

    /**
     * 从池中拿出一个
     */
    public LoongConnection borrowConnection() throws SQLException, InterruptedException {
        // 先通过从连接池中非阻塞的获取连接，如果连接为空，说明连接池是空的，那么就判断当前的正在被使用的连接数是否超过了约定的最大连接数.
        LoongConnection connection = this.connectionQueue.poll();
        if (connection == null) {
            // 如果连接池是空的，且正在使用中的连接比最大连接数小，那么就尝试异步创建新的数据库连接即可.
            if (this.borrowCount.intValue() < this.config.getMaxPoolSize() && this.connectionQueue.isEmpty()) {
                CompletableFuture.runAsync(this::initCreateIdleConnections);
            }

            // 超过了最大连接数，那么就尝试阻塞获取连接，直到超时为止，如果最后获取的还是空的，那么就抛出异常.
            // TODO
//            connection = this.connectionQueue.poll(config.getBorrowTimeout(), TimeUnit.MILLISECONDS);

            connection = this.connectionQueue.poll(1000L, TimeUnit.MILLISECONDS);
            if (connection == null) {
                throw new SQLException("[blink-pool 异常] 从连接池中获取数据库连接已超时，建议调大最大连接数的配置项或者优化慢 SQL!");
            }
        }

        // 判断连接是否有效之前，先将借用中的值 +1. 如果连接是可用的有效的，就直接返回此连接.
        this.borrowCount.increment();
        // TODO
//        if (connection.isAvailable()) {
//            return connection;
//        }

        return connection;
    }

    /**
     * 将释放的连接放回池中
     */
    public void returnConnection(LoongConnection conn) {
        // 归还连接，如果连接不能再归还到连接池中，说明了连接池已经满了，就直接关闭此连接.
        if (this.connectionQueue.offer(conn)) {
            this.borrowCount.decrement();
        } else {
            conn.closeReally();
        }
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

    public AtomicLong getLastActiveNanoTime() {
        return lastActiveNanoTime;
    }

    public void setLastActiveNanoTime(AtomicLong lastActiveNanoTime) {
        this.lastActiveNanoTime = lastActiveNanoTime;
    }
}
