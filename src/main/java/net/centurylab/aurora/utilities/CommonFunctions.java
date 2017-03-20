package net.centurylab.aurora.utilities;

import com.google.common.base.Preconditions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class CommonFunctions
{
    public static <T> T getFromSetIndex(int index, Set<T> set)
    {
        Preconditions.checkPositionIndex(index, set.size());

        int localIndex = 0;

        T result = null;

        for (T t : set)
        {
            if (localIndex == index)
            {
                result = t;
                break;
            }

            localIndex++;
        }

        return result;
    }

    public static int getRowCount(ResultSet resultSet) throws SQLException
    {
        if (resultSet.isClosed())
        {
            return 0;
        }

        int rows = 0;

        while (resultSet.next())
        {
            rows++;
        }

        resultSet.first();

        return rows;
    }
}
