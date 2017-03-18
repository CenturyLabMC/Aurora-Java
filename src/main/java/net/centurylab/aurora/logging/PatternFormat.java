package net.centurylab.aurora.logging;

import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternFormat
{
    private StackTraceElement          stackTraceElement;
    private String                     pattern;
    private Map<String, Object>        replacements;
    private Map<String, PatternAction> functions;

    public static PatternFormat getDefaultFormat(LogEntry logEntry) throws Exception
    {
        if (Strings.isNullOrEmpty(logEntry.getMessage()))
        {
            throw new Exception("The message in the logentry must be non-null and non-empty");
        }

        if (Strings.isNullOrEmpty(logEntry.getLogLevel().toString()))
        {
            throw new Exception("The log level in the logentry must be non-null and non-empty");
        }

        PatternFormat patternFormat = new PatternFormat();

        if (logEntry.getPatternFormat() == null || Strings.isNullOrEmpty(logEntry.getPatternFormat().getPattern()))
        {
            patternFormat.setPattern("[%d{dd.MM.yyyy HH:mm:ss:SSS}%] [%L%] [%c%] [%M%] [%l%] - %m%");
        }
        else
        {
            patternFormat.setPattern(logEntry.getPatternFormat().getPattern());
        }

        Map<String, PatternAction> functions = new HashMap<>();


        functions.put("d", args ->
        {
            switch (args.length)
            {
                case 1:
                    return logEntry.getTimestamp().toString(args[0]);
                default:
                    return "";
            }
        });

        patternFormat.setReplacements(PatternFormat.setUpVariables(logEntry));
        patternFormat.setFunctions(functions);

        return patternFormat;
    }

    private static Map<String, Object> setUpVariables(LogEntry logEntry)
    {
        Map<String, Object> replacements = new HashMap<>();

        replacements.put("m", logEntry.getMessage());
        replacements.put("L", logEntry.getLogLevel().toString());
        replacements.put("l", logEntry.getLineNumber());
        replacements.put("c", logEntry.getClassName());
        replacements.put("M", logEntry.getMethodName());
        replacements.put("f", logEntry.getFileName());

        return replacements;
    }

    public String getPattern()
    {
        return pattern;
    }

    public PatternFormat setPattern(String pattern)
    {
        this.pattern = pattern;
        return this;
    }

    public Map<String, Object> getReplacements()
    {
        return replacements;
    }

    public PatternFormat setReplacements(Map<String, Object> replacements)
    {
        this.replacements = replacements;
        return this;
    }

    public Map<String, PatternAction> getFunctions()
    {
        return functions;
    }

    public PatternFormat setFunctions(Map<String, PatternAction> functions)
    {
        this.functions = functions;
        return this;
    }

    public String getParsed()
    {
        String result = this.pattern;

        for (Map.Entry<String, Object> entry : this.replacements.entrySet())
        {
            String target = "%" + entry.getKey() + "%";
            result = result.replace(target, entry.getValue().toString());
        }

        for (Map.Entry<String, PatternAction> entry : this.functions.entrySet())
        {
            final String regex = "\\%d\\{(?<args>.*)\\}\\%";

            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(result);

            if (matcher.find())
            {
                String functionArgs = matcher.group("args");
                String[] args = functionArgs.split(",");
                result = result.replaceFirst(regex, entry.getValue().execute(args));
            }
        }

        return result;
    }
}
