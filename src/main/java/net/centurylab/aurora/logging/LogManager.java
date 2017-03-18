package net.centurylab.aurora.logging;

import net.centurylab.aurora.logging.appender.implementations.ConsoleAppender;

import java.util.HashMap;
import java.util.Map;

public class LogManager
{
    private static final Map<String, Logger> loggers          = new HashMap<>();
    private static final LogLevel[]          defaultLogLevels = {LogLevel.INFO, LogLevel.WARN, LogLevel.FATAL};

    public static Logger getLogger(String name, LogLevel... logLevels)
    {
        if (loggers.containsKey(name))
        {
            return loggers.get(name);
        }
        else
        {
            Logger logger = new Logger(name);

            logger.addAppender(new ConsoleAppender(logLevels));

            loggers.put(name, logger);

            return logger;
        }
    }

    public static Logger getLogger(String name)
    {
        return getLogger(name, defaultLogLevels);
    }

    public static Logger getLogger(Class aClass, LogLevel... logLevels)
    {
        return getLogger(aClass.getCanonicalName(), logLevels);
    }

    public static Logger getLogger(Class aClass)
    {
        return getLogger(aClass, defaultLogLevels);
    }
}
