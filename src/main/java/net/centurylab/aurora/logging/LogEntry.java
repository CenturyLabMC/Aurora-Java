package net.centurylab.aurora.logging;

import org.joda.time.DateTime;

public class LogEntry
{
    private DateTime      timestamp;
    private LogLevel      logLevel;
    private String        message;
    private PatternFormat patternFormat;
    private Throwable     throwable;
    private int           lineNumber;
    private String        className;
    private String        methodName;
    private String        fileName;

    public LogEntry(LogLevel logLevel, String message)
    {
        this.timestamp = DateTime.now();
        this.logLevel = logLevel;
        this.message = message;
        this.patternFormat = null;

        this.setUpVariables();
    }

    public LogEntry(LogLevel logLevel, String message, PatternFormat patternFormat)
    {
        this.timestamp = DateTime.now();
        this.logLevel = logLevel;
        this.message = message;
        this.patternFormat = patternFormat;
    }

    public LogEntry(String message, LogLevel logLevel, PatternFormat patternFormat, Throwable throwable)
    {
        this.timestamp = DateTime.now();
        this.logLevel = logLevel;
        this.message = message;
        this.patternFormat = patternFormat;
        this.throwable = throwable;
    }

    private void setUpVariables()
    {
        try
        {
            throw new Exception("");
        }
        catch (Exception e)
        {
            StackTraceElement stackTraceElement = e.getStackTrace()[3];

            lineNumber = stackTraceElement.getLineNumber();
            className = stackTraceElement.getClassName();
            methodName = stackTraceElement.getMethodName();
            fileName = stackTraceElement.getFileName();
        }
    }

    public String getParsedMessage()
    {
        try
        {
            this.patternFormat = PatternFormat.getDefaultFormat(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
        return this.patternFormat.getParsed();
    }

    public DateTime getTimestamp()
    {
        return timestamp;
    }

    public LogEntry setTimestamp(DateTime timestamp)
    {
        this.timestamp = timestamp;
        return this;
    }

    public LogLevel getLogLevel()
    {
        return logLevel;
    }

    public LogEntry setLogLevel(LogLevel logLevel)
    {
        this.logLevel = logLevel;
        return this;
    }

    public String getMessage()
    {
        return message;
    }

    public LogEntry setMessage(String message)
    {
        this.message = message;
        return this;
    }

    public Throwable getThrowable()
    {
        return throwable;
    }

    public LogEntry setThrowable(Throwable throwable)
    {
        this.throwable = throwable;
        return this;
    }

    public PatternFormat getPatternFormat()
    {
        return patternFormat;
    }

    public LogEntry setPatternFormat(PatternFormat patternFormat)
    {
        this.patternFormat = patternFormat;
        return this;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public String getClassName()
    {
        return className;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public String getFileName()
    {
        return fileName;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("LogEntry{");
        sb.append("timestamp=").append(timestamp);
        sb.append(", logLevel=").append(logLevel);
        sb.append(", message='").append(message).append('\'');
        sb.append(", patternFormat=").append(patternFormat);
        sb.append(", throwable=").append(throwable);
        sb.append('}');
        return sb.toString();
    }
}
