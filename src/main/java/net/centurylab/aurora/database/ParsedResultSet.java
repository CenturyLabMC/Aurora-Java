package net.centurylab.aurora.database;

import net.centurylab.aurora.utilities.CommonFunctions;

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

    public ParsedResultSet(ResultSet resultSet)
    {
        try
        {
            this.rows = new ArrayList<>(CommonFunctions.getRowCount(resultSet));
            this.updateCount = -1;

            while (resultSet.next())
            {
                Map<String, String> dbValues = new HashMap<>();

                for (int i = 1; i < resultSet.getMetaData().getColumnCount() + 1; i++)
                {
                    dbValues.put(resultSet.getMetaData().getColumnName(i), resultSet.getString(i));
                }

                this.rows.add(dbValues);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public ParsedResultSet(int updateCount)
    {
        this.updateCount = updateCount;
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
