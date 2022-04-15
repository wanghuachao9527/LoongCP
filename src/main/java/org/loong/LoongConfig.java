package org.loong;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

public class LoongConfig {

    /** 默认的连接池名称 */
    public static final String DEFAULT_POOL_NAME = "blink-pool";

    /** 默认活跃时间 */
    private static final long DEFAULT_KEEPALIVE_TIME = 0L;

    /** 默认连接数 */
    private static final int DEFAULT_POOL_SIZE = 10;

    /** 连接超时时间 30秒 */
    private volatile long connectionTimeout = SECONDS.toMillis(30);

    /** 校验超时时间 5秒 */
    private volatile long validationTimeout = SECONDS.toMillis(5);

    /** 连接允许被闲置在池中的最大时间 */
    private volatile long idleTimeout = MINUTES.toMillis(10);

    /** 最大连接池数量 */
    private volatile int maxPoolSize;

    /** 连接池中的最小空闲连接数 */
    private volatile int minIdle;

    /** 连接url */
    private volatile String url = null;

    /** 驱动类 */
    private volatile String driverClassName = null;

    /** 用户名 */
    private volatile String username;

    /** 密码 */
    private volatile String password;

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public long getValidationTimeout() {
        return validationTimeout;
    }

    public void setValidationTimeout(long validationTimeout) {
        this.validationTimeout = validationTimeout;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoongConfig() {
    }

}
