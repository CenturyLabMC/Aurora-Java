package net.centurylab.aurora.logging;

import net.centurylab.aurora.logging.appender.Appender;

import java.util.ArrayList;
import java.util.List;

public class Logger
{
    private String         fqn;
    private List<Appender> appenders;

    public Logger(String fqn)
    {
        this.fqn = fqn;
        this.appenders = new ArrayList<>();
    }

    public void info(String message, Object... args)
    {
        String formattedMessage = String.format(message, args);
        LogEntry logEntry = new LogEntry(LogLevel.INFO, formattedMessage);

        this.handleNewLogEntry(logEntry);
    }

    public void debug(String message, Object... args)
    {
        String formattedMessage = String.format(message, args);
        LogEntry logEntry = new LogEntry(LogLevel.DEBUG, formattedMessage);

        this.handleNewLogEntry(logEntry);
    }

    public void warn(String message, Object... args)
    {
        String formattedMessage = String.format(message, args);
        LogEntry logEntry = new LogEntry(LogLevel.WARN, formattedMessage);

        this.handleNewLogEntry(logEntry);
    }

    public void fatal(String message, Object... args)
    {
        String formattedMessage = String.format(message, args);
        LogEntry logEntry = new LogEntry(LogLevel.FATAL, formattedMessage);

        this.handleNewLogEntry(logEntry);
    }

    public void fine(String message, Object... args)
    {
        String formattedMessage = String.format(message, args);
        LogEntry logEntry = new LogEntry(LogLevel.FINE, formattedMessage);
        this.handleNewLogEntry(logEntry);
    }

    public void finer(String message, Object... args)
    {
        String formattedMessage = String.format(message, args);
        LogEntry logEntry = new LogEntry(LogLevel.FINER, formattedMessage);

        this.handleNewLogEntry(logEntry);
    }

    public void finest(String message, Object... args)
    {
        String formattedMessage = String.format(message, args);
        LogEntry logEntry = new LogEntry(LogLevel.FINEST, formattedMessage);

        this.handleNewLogEntry(logEntry);
    }

    public void database(String message, Object... args)
    {
        String formattedMessage = String.format(message, args);
        LogEntry logEntry = new LogEntry(LogLevel.DATABASE, formattedMessage);

        this.handleNewLogEntry(logEntry);
    }

    public void error(Throwable throwable, String message, Object... args)
    {
        String formattedMessage = String.format(message, args);
        LogEntry logEntry = new LogEntry(LogLevel.FATAL, formattedMessage);
        logEntry.setThrowable(throwable);

        this.handleNewLogEntry(logEntry);
    }

    private void handleNewLogEntry(LogEntry logEntry)
    {
        if (logEntry.getPatternFormat() == null)
        {
            try
            {
                logEntry.setPatternFormat(PatternFormat.getDefaultFormat(logEntry));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
        }

        for (Appender appender : this.appenders)
        {
            if (appender.getLogLevels().contains(logEntry.getLogLevel()) || appender.getLogLevels().contains(LogLevel.ALL))
            {
                if (appender.handleLogEntry(logEntry))
                {
                    appender.appendLogEntry(logEntry);
                }
            }
        }
    }

    public String getFqn()
    {
        return fqn;
    }

    public Logger setFqn(String fqn)
    {
        this.fqn = fqn;
        return this;
    }

    public List<Appender> getAppenders()
    {
        return appenders;
    }

    public Logger setAppenders(List<Appender> appenders)
    {
        this.appenders = appenders;
        return this;
    }

    public void addAppender(Appender appender)
    {
        if (!this.appenders.contains(appender))
        {
            this.appenders.add(appender);
            appender.onEnable();
        }
    }

    public boolean removeAppender(Appender appender)
    {
        if (this.appenders.contains(appender))
        {
            appender.onDisable();
            return this.appenders.remove(appender);
        }

        return false;
    }
}
