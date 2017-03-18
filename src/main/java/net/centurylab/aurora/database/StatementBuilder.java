package net.centurylab.aurora.database;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class StatementBuilder<T>
{
    protected final List<Condition>  conditions  = new ArrayList<>();
    protected final List<String>     fields      = new ArrayList<>();
    protected final List<String>     tables      = new ArrayList<>();
    protected final List<OrderField> orderFields = new ArrayList<>();

    protected QueryType     queryType     = QueryType.SELECT;
    protected StringBuilder currentSql    = new StringBuilder();
    protected boolean       useConditions = false;
    protected boolean       useLimits     = false;
    protected int           limit         = 0;
    protected int           offset        = 0;
    protected boolean       useOrdering   = false;

    protected Map<String, Object> values;

    /**
     * Adds a field to the query
     *
     * @param fieldName Name of the field
     * @return Current {@link StatementBuilder} instance
     */
    public abstract T addField(String fieldName);

    /**
     * Adds a field to the query
     *
     * @param tableName Name of the table
     * @return Current {@link StatementBuilder} instance
     */
    public abstract T addTable(String tableName);

    /**
     * Resets all variables
     *
     * @return Current {@link StatementBuilder} instance
     */
    public abstract T reset();

    /**
     * Set to true if you want to use conditions for the next query
     *
     * @param useConditions True if you want to use conditions
     * @return Returns the current {@link StatementBuilder} instance
     */
    public abstract T useConditions(boolean useConditions);

    /**
     * Adds a condition
     *
     * @param condition Condition to addDeSerializer
     * @return Current {@link StatementBuilder} instance
     * @throws IllegalArgumentException if the condition is null
     */
    public abstract T addCondition(Condition condition);

    /**
     * Deletes the condition on the given index
     *
     * @param index the index of the element to be removed
     * @return Current {@link StatementBuilder} instance
     * @throws IllegalArgumentException if the index is out of range
     */
    public abstract T deleteCondition(int index);

    /**
     * Deletes the condition by object
     *
     * @param condition Condition to delete
     * @return Current {@link StatementBuilder} instance
     * @throws IllegalArgumentException if the condition is null
     */
    public abstract T deleteCondition(Condition condition);

    /**
     * Limits the result to the given limit and starts at the given offset
     *
     * @param limit  Count of results
     * @param offset Index where to start
     * @return Current {@link StatementBuilder} instance
     * @throws IllegalArgumentException if limit or offset are negative
     */
    public abstract T limit(int limit, int offset);

    /**
     * Orders the result by the given field by the given type
     *
     * @param orderField Name of the field
     * @param orderType  Type to order
     * @return Current {@link StatementBuilder} instance
     * @throws IllegalArgumentException if field equals "" or when the orderType is null
     */

    /**
     * Adds a new orderfield
     *
     * @param orderField The {@link OrderField}
     * @return Current {@link StatementBuilder} instance
     */
    public abstract T addOrderField(OrderField orderField);

    /**
     * Removes an {@link OrderField} by index
     *
     * @param index Index of the {@link OrderField}
     * @return Current {@link StatementBuilder} instance
     */
    public abstract T removeOrderField(int index);

    /**
     * Removes an {@link OrderField} by index
     *
     * @param orderField {@link OrderField} which should be removed
     * @return Current {@link StatementBuilder} instance
     */
    public abstract T removeOrderField(OrderField orderField);

    /**
     * Creates a {@link PreparedStatement} with the current query
     *
     * @param database The database where the query should be executed against
     * @return Current {@link StatementBuilder} instance
     */
    public abstract PreparedStatement build(Database database);

    /**
     * @return The current query type (SELECT, INSERT, UPDATE, DELETE)
     */
    public QueryType getQueryType()
    {
        return queryType;
    }

    /**
     * Returns the current sql after {@link StatementBuilder#build(Database)} was invoked
     *
     * @return Current SQL query
     */
    public String getCurrentSql()
    {
        return currentSql.toString();
    }
}
