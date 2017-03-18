package net.centurylab.aurora.database;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class OrderField
{
    private String    fieldName;
    private OrderType orderType;

    public OrderField(String fieldName, OrderType orderType)
    {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(fieldName));
        Preconditions.checkNotNull(orderType);

        this.fieldName = fieldName;
        this.orderType = orderType;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public OrderField setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
        return this;
    }

    public OrderType getOrderType()
    {
        return orderType;
    }

    public OrderField setOrderType(OrderType orderType)
    {
        this.orderType = orderType;
        return this;
    }
}
