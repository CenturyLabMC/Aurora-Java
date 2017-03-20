package net.centurylab.aurora.database.implementations;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import net.centurylab.aurora.database.Database;
import net.centurylab.aurora.database.ParsedResultSet;

import java.sql.PreparedStatement;

public class MySQLDatabase extends Database
{
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String JDBC_URL     = "jdbc:mysql://%s:%s/%s";

    private String host;
    private int    port;
    private String database;
    private String username;
    private String password;

    public MySQLDatabase(String host, String database, String username, String password)
    {
        this(host, 3306, database, username, password);
    }

    public MySQLDatabase(String host, int port, String database, String username, String password)
    {
        this(host, port, database, username, password, 5, 10);
    }

    public MySQLDatabase(String host, int port, String database, String username, String password, int minPoolSize, int maxPoolSize)
    {
        super(DRIVER_CLASS, String.format(JDBC_URL, host, port, database), username, password, minPoolSize, maxPoolSize);

        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * Executes a {@link PreparedStatement}
     *
     * @param preparedStatement The {@link PreparedStatement}
     * @return An {@link Observable} which pushes the result
     */
    @Override
    public Observable<ParsedResultSet> execute(PreparedStatement preparedStatement)
    {
        Observable<ParsedResultSet> observable = Observable.create(new ObservableOnSubscribe<ParsedResultSet>()
        {
            @Override
            public void subscribe(ObservableEmitter<ParsedResultSet> e) throws Exception
            {
                ParsedResultSet parsedResultSet;

                if (preparedStatement.execute())
                {
                    parsedResultSet = new ParsedResultSet(preparedStatement.getResultSet());
                }
                else
                {
                    parsedResultSet = new ParsedResultSet(preparedStatement.getUpdateCount());
                }

                MySQLDatabase.this.closePreparedStatement(preparedStatement);
                e.onNext(parsedResultSet);
                e.onComplete();
            }
        });

        return observable;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String getDatabase()
    {
        return database;
    }

    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public String getPassword()
    {
        return password;
    }
}
