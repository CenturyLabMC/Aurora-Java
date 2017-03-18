package net.centurylab.aurora.database;

public enum OrderType
{
    ASCENDING("ASC"),
    DESCENDING("DESC");

    private String value;

    OrderType(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
