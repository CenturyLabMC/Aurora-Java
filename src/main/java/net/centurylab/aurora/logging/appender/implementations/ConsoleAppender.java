package net.centurylab.aurora.logging.appender.implementations;

import net.centurylab.aurora.logging.LogEntry;
import net.centurylab.aurora.logging.LogLevel;
import net.centurylab.aurora.logging.appender.Appender;

public class ConsoleAppender extends Appender
{
    public ConsoleAppender(LogLevel... logLevels)
    {
        super(logLevels);
    }

    @Override
    public void onEnable()
    {

    }

    @Override
    public boolean appendLogEntry(LogEntry logEntry)
    {
        System.out.println(logEntry.getParsedMessage());

        if (logEntry.getThrowable() != null)
        {
            logEntry.getThrowable().printStackTrace(System.out);
        }

        return true;
    }

    @Override
    public void onDisable()
    {

    }
}
