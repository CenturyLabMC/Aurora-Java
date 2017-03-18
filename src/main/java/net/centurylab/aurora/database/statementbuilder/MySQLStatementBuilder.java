package net.centurylab.aurora.database.statementbuilder;

import com.google.common.base.Preconditions;

import net.centurylab.aurora.database.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import static net.centurylab.aurora.database.QueryType.DELETE;

public class MySQLStatementBuilder extends StatementBuilder<MySQLStatementBuilder>
{
    private MySQLStatementBuilder(QueryType queryType)
    {
        this(queryType, null);
    }

    private MySQLStatementBuilder(QueryType queryType, Map<String, Object> values)
    {
        this.queryType = queryType;
        this.values = values;
    }

    public static MySQLStatementBuilder Select()
    {
        return new MySQLStatementBuilder(QueryType.SELECT);
    }

    public static MySQLStatementBuilder Insert(Map<String, Object> values)
    {
        Preconditions.checkNotNull(values, "values can't be null");

        return new MySQLStatementBuilder(QueryType.INSERT, values);
    }

    public static MySQLStatementBuilder Update(Map<String, Object> values)
    {
        Preconditions.checkNotNull(values, "values can't be null");

        return new MySQLStatementBuilder(QueryType.UPDATE, values);
    }

    public static MySQLStatementBuilder Delete()
    {
        return new MySQLStatementBuilder(DELETE);
    }

    public static PreparedStatement describeTable(String tableName, Database database)
    {
        try
        {
            Connection databaseConnection = database.getConnection();
            return databaseConnection.prepareStatement(String.format("DESCRIBE `%s`", tableName));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Adds a field to the query
     *
     * @param fieldName Name of the field
     * @return Current {@link StatementBuilder} instance
     */
    @Override
    public MySQLStatementBuilder addField(String fieldName)
    {
        this.fields.add(fieldName);
        return this;
    }

    /**
     * Adds a field to the query
     *
     * @param tableName Name of the table
     * @return Current {@link StatementBuilder} instance
     */
    @Override
    public MySQLStatementBuilder addTable(String tableName)
    {
        this.tables.add(tableName);
        return this;
    }

    /**
     * Resets all variables
     *
     * @return Current {@link StatementBuilder} instance
     */
    @Override
    public MySQLStatementBuilder reset()
    {
        this.queryType = QueryType.SELECT;
        this.useConditions = false;
        this.conditions.clear();
        this.currentSql = new StringBuilder();
        this.useLimits = false;
        this.limit = 0;
        this.offset = 0;
        this.useOrdering = false;

        return this;
    }

    /**
     * Set to true if you want to use conditions for the next query
     *
     * @param useConditions True if you want to use conditions
     * @return Returns the current {@link StatementBuilder} instance
     */
    @Override
    public MySQLStatementBuilder useConditions(boolean useConditions)
    {
        this.useConditions = useConditions;

        return this;
    }

    /**
     * Adds a condition
     *
     * @param condition Condition to addDeSerializer
     * @throws IllegalArgumentException if the condition is null
     */
    @Override
    public MySQLStatementBuilder addCondition(Condition condition)
    {
        Preconditions.checkNotNull(condition, "Condition can't be null");

        this.useConditions = true;
        this.conditions.add(condition);

        return this;
    }

    /**
     * Deletes the condition on the given index
     *
     * @param index the index of the element to be removed
     * @throws IllegalArgumentException if the index is out of range
     */
    @Override
    public MySQLStatementBuilder deleteCondition(int index)
    {
        Preconditions.checkElementIndex(index, this.conditions.size());

        this.conditions.remove(index);

        return this;
    }

    /**
     * Deletes the condition by object
     *
     * @param condition Condition to delete
     * @throws IllegalArgumentException if the condition is null
     */
    @Override
    public MySQLStatementBuilder deleteCondition(Condition condition)
    {
        Preconditions.checkNotNull(condition, "Condition can't be null");

        this.conditions.remove(condition);

        return this;
    }

    /**
     * Limits the result to the given limit and starts at the given offset
     *
     * @param limit  Count of results
     * @param offset Index where to start
     * @throws IllegalArgumentException if limit or offset are negative
     */
    @Override
    public MySQLStatementBuilder limit(int limit, int offset)
    {
        Preconditions.checkArgument(limit > 0, "limit can't be negative or equals zero");
        Preconditions.checkArgument(offset > -1, "offset can't be negative");

        this.useLimits = true;
        this.limit = limit;
        this.offset = offset;

        return this;
    }

    /**
     * Adds a new orderfield
     *
     * @param orderField The {@link OrderField}
     * @return Current {@link StatementBuilder} instance
     */
    @Override
    public MySQLStatementBuilder addOrderField(OrderField orderField)
    {
        this.useOrdering = true;
        this.orderFields.add(orderField);
        return this;
    }

    /**
     * Removes an {@link OrderField} by index
     *
     * @param index Index of the {@link OrderField}
     * @return Current {@link StatementBuilder} instance
     */
    @Override
    public MySQLStatementBuilder removeOrderField(int index)
    {
        this.orderFields.remove(index);
        return this;
    }

    /**
     * Removes an {@link OrderField} by index
     *
     * @param orderField {@link OrderField} which should be removed
     * @return Current {@link StatementBuilder} instance
     */
    @Override
    public MySQLStatementBuilder removeOrderField(OrderField orderField)
    {
        this.orderFields.remove(orderField);
        return this;
    }

    /**
     * Creates a {@link PreparedStatement} with the current query
     *
     * @return The {@link PreparedStatement} which can be executed
     */
    @Override
    public PreparedStatement build(Database database)
    {
        Preconditions.checkArgument(tables.size() != 0, "tables can't be empty");

        switch (this.queryType)
        {
            case SELECT:
                this.currentSql = new StringBuilder("SELECT ");

                if (fields.size() == 1)
                {
                    if (fields.get(0).equalsIgnoreCase("*"))
                    {
                        this.currentSql.append(String.format("%s", fields.get(0)));
                    }
                    else
                    {
                        this.currentSql.append(String.format("`%s`", fields.get(0)));
                    }
                }
                else
                {
                    for (int i = 0; i < fields.size(); i++)
                    {
                        if (i == fields.size() - 1)
                        {
                            this.currentSql.append(String.format("`%s`", fields.get(i)));
                        }
                        else
                        {
                            this.currentSql.append(String.format("`%s`, ", fields.get(i)));
                        }
                    }
                }

                this.currentSql.append(" FROM ");

                if (tables.size() == 1)
                {
                    this.currentSql.append("`").append(tables.get(0)).append("`");
                }
                else
                {
                    for (int i = 0; i < tables.size(); i++)
                    {
                        if (i == tables.size() - 1)
                        {
                            this.currentSql.append(String.format("`%s`", tables.get(i)));
                        }
                        else
                        {
                            this.currentSql.append(String.format("`%s`, ", tables.get(i)));
                        }
                    }
                }

                if (this.useConditions)
                {
                    this.buildConditions();
                }

                if (this.useOrdering)
                {
                    if (this.orderFields.size() > 0)
                    {
                        if (this.orderFields.size() == 1)
                        {
                            this.currentSql.append(String.format(" ORDER BY `%s` %s", this.orderFields.get(0).getFieldName(), this.orderFields.get(0).getOrderType().getValue()));
                        }
                        else
                        {
                            this.currentSql.append(" ORDER BY ");

                            for (int i = 0; i < this.orderFields.size(); i++)
                            {
                                OrderField currentOrderField = orderFields.get(i);
                                if (i == orderFields.size() - 1)
                                {
                                    this.currentSql.append(String.format("`%s` %s", currentOrderField.getFieldName(), currentOrderField.getOrderType().getValue()));
                                }
                                else
                                {
                                    this.currentSql.append(String.format("`%s` %s, ", currentOrderField.getFieldName(), currentOrderField.getOrderType().getValue()));
                                }
                            }
                        }
                    }
                }

                if (this.useLimits)
                {
                    this.currentSql.append(String.format(" LIMIT %d", this.limit));

                    if (this.offset > 0)
                    {
                        this.currentSql.append(String.format(", %d", this.offset));
                    }
                }
                break;
            case INSERT:
                this.currentSql = new StringBuilder(String.format("INSERT INTO `%s` (", this.tables.get(0)));
                for (int i = 0; i < values.keySet().size(); i++)
                {
                    if (i == values.keySet().size() - 1)
                    {
                        this.currentSql.append(String.format("`%s`", values.keySet().toArray()[i]));
                    }
                    else
                    {
                        this.currentSql.append(String.format("`%s`, ", values.keySet().toArray()[i]));
                    }
                }
                this.currentSql.append(") VALUES (");

                for (int i = 0; i < values.values().size(); i++)
                {
                    if (i == values.values().size() - 1)
                    {
                        this.currentSql.append("?");
                    }
                    else
                    {
                        this.currentSql.append("?, ");
                    }
                }

                this.currentSql.append(")");

                break;
            case DELETE:
                this.currentSql = new StringBuilder(String.format("DELETE FROM `%s`", tables.get(0)));

                if (this.useConditions)
                {
                    this.buildConditions();
                }
                if (this.useLimits)
                {
                    this.currentSql.append(String.format(" LIMIT %d", this.limit));
                }
                break;
            case UPDATE:
                this.currentSql = new StringBuilder(String.format("UPDATE `%s` SET ", tables.get(0)));
                for (int i = 0; i < values.keySet().size(); i++)
                {
                    if (i == values.keySet().size() - 1)
                    {
                        this.currentSql.append(String.format("`%s` = ?", values.keySet().toArray()[i]));
                    }
                    else
                    {
                        this.currentSql.append(String.format("`%s` = ?, ", values.keySet().toArray()[i]));
                    }
                }
                if (this.useConditions)
                {
                    this.buildConditions();
                }
                if (this.useLimits)
                {
                    this.currentSql.append(String.format(" LIMIT %d", this.limit));
                }
                break;
        }

        PreparedStatement statement = null;
        try
        {
            Connection connection = database.getConnection();

            statement = connection.prepareStatement(this.currentSql.toString());

            if (this.currentSql.toString().contains("?"))
            {
                if (useConditions && values != null)
                {
                    int index = 0;

                    // TODO: Use serializer

                    for (int i = 0; index < values.values().size(); index++)
                    {
                        Object o = values.values().toArray()[index];

                        if (o instanceof String)
                        {
                            statement.setString(index + 1, o.toString());
                        }
                        else if (o instanceof UUID)
                        {
                            statement.setString(index + 1, o.toString());
                        }
                        else if (o instanceof Integer)
                        {
                            statement.setInt(index + 1, (Integer) o);
                        }
                        else if (o instanceof Float)
                        {
                            statement.setFloat(index + 1, (Float) o);
                        }
                        else if (o instanceof Long)
                        {
                            statement.setLong(index + 1, (Long) o);
                        }
                        else if (o instanceof Short)
                        {
                            statement.setShort(index + 1, (Short) o);
                        }
                        else
                        {
                            statement.setObject(index + 1, o);
                        }
                    }

                    index += 1;

                    for (int i = 0; i < conditions.size(); i++)
                    {
                        Condition condition = conditions.get(i);

                        if (condition.getValue() instanceof String)
                        {
                            statement.setString(i + index, condition.getValue().toString());
                        }
                        else if (condition.getValue() instanceof UUID)
                        {
                            statement.setString(i + index, condition.getValue().toString());
                        }
                        else if (condition.getValue() instanceof Integer)
                        {
                            statement.setInt(i + index, (Integer) condition.getValue());
                        }
                        else if (condition.getValue() instanceof Float)
                        {
                            statement.setFloat(i + index, (Float) condition.getValue());
                        }
                        else if (condition.getValue() instanceof Long)
                        {
                            statement.setLong(i + index, (Long) condition.getValue());
                        }
                        else if (condition.getValue() instanceof Short)
                        {
                            statement.setShort(i + index, (Short) condition.getValue());
                        }
                        else
                        {
                            statement.setObject(i + index, condition.getValue());
                        }
                    }
                }
                else if (values != null && values.size() > 0)
                {
                    for (int i = 0; i < values.values().size(); i++)
                    {
                        Object o = values.values().toArray()[i];

                        // use serializer

                        if (o instanceof String)
                        {
                            statement.setString(i + 1, o.toString());
                        }
                        else if (o instanceof UUID)
                        {
                            statement.setString(i + 1, o.toString());
                        }
                        else if (o instanceof Integer)
                        {
                            statement.setInt(i + 1, (Integer) o);
                        }
                        else if (o instanceof Float)
                        {
                            statement.setFloat(i + 1, (Float) o);
                        }
                        else if (o instanceof Long)
                        {
                            statement.setLong(i + 1, (Long) o);
                        }
                        else if (o instanceof Short)
                        {
                            statement.setShort(i + 1, (Short) o);
                        }
                        else
                        {
                            statement.setObject(i + 1, o);
                        }
                    }
                }
                else if (useConditions && conditions.size() > 0)
                {
                    for (int i = 0; i < conditions.size(); i++)
                    {
                        Condition condition = conditions.get(i);

                        if (condition.getValue() instanceof String)
                        {
                            statement.setString(i + 1, condition.getValue().toString());
                        }
                        else if (condition.getValue() instanceof UUID)
                        {
                            statement.setString(i + 1, condition.getValue().toString());
                        }
                        else if (condition.getValue() instanceof Integer)
                        {
                            statement.setInt(i + 1, (Integer) condition.getValue());
                        }
                        else if (condition.getValue() instanceof Float)
                        {
                            statement.setFloat(i + 1, (Float) condition.getValue());
                        }
                        else if (condition.getValue() instanceof Long)
                        {
                            statement.setLong(i + 1, (Long) condition.getValue());
                        }
                        else if (condition.getValue() instanceof Short)
                        {
                            statement.setShort(i + 1, (Short) condition.getValue());
                        }
                        else
                        {
                            statement.setObject(i + 1, condition.getValue());
                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return statement;

    }

    /**
     * Builds all conditions for the query
     */
    private void buildConditions()
    {
        Preconditions.checkArgument(this.useConditions, "You need to use conditions before you can build them");
        Preconditions.checkArgument(this.conditions.size() != 0, "You need to add conditions");
        this.conditions.forEach(Preconditions::checkNotNull);

        this.currentSql.append(" WHERE ");

        for (Condition condition : this.conditions)
        {
            if (condition.isFirstCondition())
            {
                this.currentSql.append(String.format("`%s` %s ?", condition.getField(), condition.getOperator()));
            }
            else
            {
                this.currentSql.append(String.format("%s `%s` %s ?", condition.getClause(), condition.getField(), condition.getOperator()));
            }
        }
    }
}
