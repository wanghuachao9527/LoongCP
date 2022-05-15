package org.loong;

public class LoongConfig {

    /* 默认的连接池名称 */
    public static final String DEFAULT_POOL_NAME = "loong-pool";

    /* 当我从池中借出连接时，愿意等待多长时间 单位：秒 */
    private static final int DEFAULT_CONNECTION_TIMEOUT = 30;

    /* 秒 */
    private static final int DEFAULT_VALIDATION_TIMEOUT = 5;

    /* 一个连接在池里闲置多久时会被抛弃 单位：分 */
    private static final int DEFAULT_IDLE_TIMEOUT = 10;

    /* 当一个连接存活了足够久，HikariCP 将会在它空闲时把它抛弃 单位：分 */
    private static final int DEFAULT_MAX_LIFETIME = 30;

    /* 默认活跃时间 */
    private static final long DEFAULT_KEEPALIVE_TIME = 0L;

    /* 默认连接数 */
    private static final int DEFAULT_POOL_SIZE = 10;

    /* 当一个连接存活了足够久，HikariCP 将会在它空闲时把它抛弃 单位：分 */
    private volatile long maxLifetime;

    /* 连接超时时间 30秒 */
    private volatile long connectionTimeout;

    /* 校验超时时间 5秒 */
    private volatile long validationTimeout;

    /* 连接允许被闲置在池中的最大时间 */
    private volatile long idleTimeout;

    /* 池中最多容纳多少连接（包括空闲的和在用的） */
    private volatile int maxPoolSize;

    /* 池中至少要有多少空闲连接 */
    private volatile int minIdle;

    /* 连接url */
    private volatile String url = null;

    /* 驱动类 */
    private volatile String driverClassName = null;

    /* 用户名 */
    private volatile String username;

    /* 密码 */
    private volatile String password;

    /**
     * 默认构造方法
     */
    public LoongConfig() {
        minIdle = -1;
        maxPoolSize = -1;
        maxLifetime = DEFAULT_MAX_LIFETIME;
        connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        validationTimeout = DEFAULT_VALIDATION_TIMEOUT;
        idleTimeout = DEFAULT_IDLE_TIMEOUT;
//        keepaliveTime = DEFAULT_KEEPALIVE_TIME;
    }

    public long getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(long maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

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
}
