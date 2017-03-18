package net.centurylab.aurora.database;

public class Condition
{
    private boolean isFirstCondition;
    private String  clause;
    private String  operator;
    private String  field;
    private Object  value;

    public Condition()
    {
    }

    public Condition(String clause, String field, String operator, Object value)
    {
        this.isFirstCondition = false;
        this.clause = clause;
        this.operator = operator;
        this.field = field;
        this.value = value;
    }

    public Condition(String field, String operator, Object value)
    {
        this.isFirstCondition = true;
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public boolean isFirstCondition()
    {
        return isFirstCondition;
    }

    public Condition setFirstCondition(boolean firstCondition)
    {
        isFirstCondition = firstCondition;
        return this;
    }

    public String getClause()
    {
        return clause;
    }

    public Condition setClause(String clause)
    {
        this.clause = clause;
        return this;
    }

    public String getOperator()
    {
        return operator;
    }

    public Condition setOperator(String operator)
    {
        this.operator = operator;
        return this;
    }

    public String getField()
    {
        return field;
    }

    public Condition setField(String field)
    {
        this.field = field;
        return this;
    }

    public Object getValue()
    {
        return value;
    }

    public Condition setValue(Object value)
    {
        this.value = value;
        return this;
    }
}
