package net.centurylab.aurora.database;

import net.centurylab.aurora.utilities.CommonFunctions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsedResultSet
{
    private List<Map<String, String>> rows;
    private int                       updateCount;
    private int[]                     generatedKeys;

    public ParsedResultSet(PreparedStatement preparedStatement, QueryType queryType, boolean returnGeneratedKeys) throws SQLException
    {
        try
        {
            if (queryType == QueryType.SELECT)
            {
                preparedStatement.execute();
            }
            else
            {
                preparedStatement.executeUpdate();
            }

            switch (queryType)
            {
                case SELECT:
                    this.updateCount = -1;
                    this.rows = new ArrayList<>(CommonFunctions.getRowCount(preparedStatement.getResultSet()));
                    this.updateCount = -1;

                    while (preparedStatement.getResultSet().next())
                    {
                        Map<String, String> dbValues = new HashMap<>();

                        for (int i = 1; i < preparedStatement.getResultSet().getMetaData().getColumnCount() + 1; i++)
                        {
                            dbValues.put(preparedStatement.getResultSet().getMetaData().getColumnName(i), preparedStatement.getResultSet().getString(i));
                        }

                        this.rows.add(dbValues);
                    }
                    break;
                case INSERT:
                    this.updateCount = preparedStatement.getUpdateCount();
                    this.rows = null;

                    if (returnGeneratedKeys)
                    {
                        this.generatedKeys = new int[CommonFunctions.getRowCount(preparedStatement.getGeneratedKeys())];
                        int index = 0;
                        ResultSet resultSet = preparedStatement.getGeneratedKeys();

                        while (resultSet.next())
                        {
                            this.generatedKeys[index] = resultSet.getInt(1);
                            index++;
                        }
                    }
                    else
                    {
                        this.generatedKeys = null;
                    }
                    break;
                case DELETE:
                case UPDATE:
                    this.updateCount = preparedStatement.getUpdateCount();
                    this.rows = null;
                    this.generatedKeys = null;
                    break;
            }
        }
        catch (SQLException e)
        {
            throw e;
        }
    }

    public int getUpdateCount()
    {
        return updateCount;
    }

    public String getString(String columnName) throws Exception
    {
        return this.getString(0, columnName);
    }

    public String getString(int index, String columnName) throws Exception
    {
        this.check();

        return this.rows.get(index).get(columnName);
    }

    public int getInt(String columnName) throws Exception
    {
        return this.getInt(0, columnName);
    }

    public int getInt(int index, String columnName) throws Exception
    {
        this.check();

        return Integer.parseInt(this.rows.get(0).get(columnName));
    }

    public short getShort(String columnName) throws Exception
    {
        return this.getShort(0, columnName);
    }

    public short getShort(int index, String columnName) throws Exception
    {
        this.check();

        return Short.parseShort(this.rows.get(index).get(columnName));
    }

    public long getLong(String columnName) throws Exception
    {
        return this.getLong(0, columnName);
    }

    public long getLong(int index, String columnName) throws Exception
    {
        this.check();

        return Long.parseLong(this.rows.get(index).get(columnName));
    }

    public byte getByte(String columnName) throws Exception
    {
        return this.getByte(0, columnName);
    }

    private byte getByte(int index, String columnName) throws Exception
    {
        this.check();

        return Byte.parseByte(this.rows.get(index).get(columnName));
    }

    public float getFloat(String columnName) throws Exception
    {
        return this.getFloat(0, columnName);
    }

    public float getFloat(int index, String columnName) throws Exception
    {
        this.check();

        return Float.parseFloat(this.rows.get(index).get(columnName));
    }

    public double getDouble(String columnName) throws Exception
    {
        return this.getDouble(0, columnName);
    }

    private double getDouble(int index, String columnName) throws Exception
    {
        this.check();

        return Double.parseDouble(this.rows.get(index).get(columnName));
    }

    public int[] getGeneratedKeys()
    {
        return generatedKeys;
    }

    private void check() throws Exception
    {
        if (this.rows == null)
        {
            throw new Exception("We don't have a resultset...");
        }
    }

    public int getRowCount() throws Exception
    {
        this.check();

        return this.rows.size();
    }

    @Override
    public String toString()
    {
        return "ParsedResultSet{rows=" + rows.size() +
                ", updateCount=" + updateCount +
                '}';
    }
}
