package net.centurylab.aurora.logging.appender;

import net.centurylab.aurora.logging.LogEntry;
import net.centurylab.aurora.logging.LogLevel;

import java.util.Arrays;
import java.util.List;

public abstract class Appender
{
    private List<LogLevel> logLevels;

    public Appender(LogLevel... logLevel)
    {
        this.logLevels = Arrays.asList(logLevel);
    }

    public abstract void onEnable();

    public boolean handleLogEntry(LogEntry logEntry)
    {
        return this.logLevels.contains(logEntry.getLogLevel());
    }

    public abstract boolean appendLogEntry(LogEntry logEntry);

    public List<LogLevel> getLogLevels()
    {
        return logLevels;
    }

    public Appender setLogLevels(List<LogLevel> logLevels)
    {
        this.logLevels = logLevels;
        return this;
    }

    public abstract void onDisable();
}
