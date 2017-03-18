package net.centurylab.aurora.database;

import com.google.common.base.Strings;
import com.zaxxer.hikari.HikariDataSource;
import io.reactivex.Observable;

import net.centurylab.aurora.logging.LogManager;
import net.centurylab.aurora.logging.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public abstract class Database
{
    private static final int DEFAULT_MIN_POOLSIZE = 5;
    private static final int DEFAULT_MAX_POOLSIZE = 10;

    protected Logger           logger;
    private   HikariDataSource connectionPool;

    public Database(String driverClass, String jdbcUrl)
    {
        this(driverClass, jdbcUrl, "", "");
    }

    public Database(String driverClass, String jdbcUrl, String username, String password)
    {
        this(driverClass, jdbcUrl, username, password, 5, 10);
    }

    public Database(String driverClass, String jdbcUrl, String username, String password, int minPoolSize, int maxPoolSize)
    {
        this(driverClass, jdbcUrl, username, password, minPoolSize, maxPoolSize, new Properties());
    }

    public Database(String driverClass, String jdbcUrl, String username, String password, int minPoolSize, int maxPoolSize, Properties connectionPoolProperties)
    {
        this.logger = LogManager.getLogger(Database.class);
        this.connectionPool = new HikariDataSource();

        if (Strings.isNullOrEmpty(driverClass))
        {
            this.logger.fatal("Parameter 'driverClass' is null or an empty string.");
            return;
        }

        if (Strings.isNullOrEmpty(jdbcUrl))
        {
            this.logger.fatal("Parameter 'jdbcUrl' is null or an empty string.");
            return;
        }

        if (username == null)
        {
            this.logger.fatal("Parameter 'username' is null.");
            return;
        }

        if (password == null)
        {
            this.logger.fatal("Parameter 'password' is null.");
            return;
        }

        if (minPoolSize < 1)
        {
            this.connectionPool.setMinimumIdle(DEFAULT_MIN_POOLSIZE);
            this.logger.warn("Adjusted minPoolSize to %s", DEFAULT_MIN_POOLSIZE);
        }
        else
        {
            this.connectionPool.setMinimumIdle(minPoolSize);
        }

        if (maxPoolSize < 1)
        {
            this.connectionPool.setMaximumPoolSize(DEFAULT_MAX_POOLSIZE);
            this.logger.warn("Adjusted maxPoolSize to %s", DEFAULT_MIN_POOLSIZE);
        }
        else
        {
            this.connectionPool.setMaximumPoolSize(maxPoolSize);
        }

        if (maxPoolSize < minPoolSize)
        {
            this.logger.fatal("maxPoolSize < minPoolSize");
            return;
        }

        try
        {
            Class.forName(driverClass);
        }
        catch (ClassNotFoundException e)
        {
            this.logger.error(e, "Could not find class '%s'. Please check if you added all dependencies to the classpath", driverClass);
        }

        this.connectionPool.setDriverClassName(driverClass);
        this.connectionPool.setJdbcUrl(jdbcUrl);
        this.connectionPool.setUsername(username);
        this.connectionPool.setPassword(password);
        this.connectionPool.setDataSourceProperties(connectionPoolProperties);
        this.connectionPool.setConnectionTestQuery("SELECT 1");
        this.connectionPool.setConnectionTimeout(TimeUnit.SECONDS.toMillis(15));
    }

    /**
     * @return A new connection from the connection pool
     * @throws SQLException If a database access error occurs
     * @see HikariDataSource#getConnection()
     */
    public Connection getConnection() throws SQLException
    {
        return connectionPool.getConnection();
    }

    /**
     * @param username Username
     * @param password Password
     * @return A new connection from the connection pool
     * @throws SQLException If a database access error occurs
     * @see HikariDataSource#getConnection(String, String)
     */
    public Connection getConnectionWithCredentials(String username, String password) throws SQLException
    {
        return connectionPool.getConnection(username, password);
    }

    /**
     * Executes a {@link PreparedStatement}
     *
     * @param preparedStatement The {@link PreparedStatement}
     * @return An {@link Observable} which pushes the result
     */
    public abstract Observable<ParsedResultSet> execute(PreparedStatement preparedStatement);

    public HikariDataSource getConnectionPool()
    {
        return connectionPool;
    }

    public Database setConnectionPool(HikariDataSource connectionPool)
    {
        this.connectionPool = connectionPool;
        return this;
    }

    public Properties getConnectionPoolProperties()
    {
        return this.connectionPool.getDataSourceProperties();
    }

    public Database setConnectionPoolProperties(Properties connectionPoolProperties)
    {
        this.connectionPool.setDataSourceProperties(connectionPoolProperties);
        return this;
    }

    public int getMinPoolSize()
    {
        return this.connectionPool.getMinimumIdle();
    }

    public Database setMinPoolSize(int minPoolSize)
    {
        this.connectionPool.setMinimumIdle(minPoolSize);
        return this;
    }

    public int getMaxPoolsize()
    {
        return this.connectionPool.getMaximumPoolSize();
    }

    public Database setMaxPoolsize(int maxPoolsize)
    {
        this.connectionPool.setMaximumPoolSize(maxPoolsize);
        return this;
    }

    public String getDriverClass()
    {
        return this.connectionPool.getDriverClassName();
    }

    public Database setDriverClass(String driverClass)
    {
        this.connectionPool.setDriverClassName(driverClass);
        return this;
    }

    public String getJdbcUrl()
    {
        return this.connectionPool.getJdbcUrl();
    }

    public Database setJdbcUrl(String jdbcUrl)
    {
        this.connectionPool.setJdbcUrl(jdbcUrl);
        return this;
    }

    public String getUsername()
    {
        return this.connectionPool.getUsername();
    }

    public Database setUsername(String username)
    {
        this.connectionPool.setUsername(username);
        return this;
    }

    public String getPassword()
    {
        return this.connectionPool.getPassword();
    }

    public Database setPassword(String password)
    {
        this.connectionPool.setPassword(password);
        return this;
    }
}
