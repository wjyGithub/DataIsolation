package com.data.isolation.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 条件表达式的生成工具类
 * Created by jianyuan.wei@hand-china.com
 * on 2019/7/9 17:16.
 */
public class ConditionUtil {


    /**
     * 获取等值表达式子,即columnName = value
     * @param columnName 列名
     * @param value 数值
     * @return 等值表达式,即columnName = value
     */
    public static final SQLExpr getEqualTo(String columnName, Integer value) {
        SQLBinaryOpExpr equalExpr = new SQLBinaryOpExpr();
        equalExpr.setLeft(new SQLIdentifierExpr(columnName));
        equalExpr.setRight(new SQLNumberExpr(value));
        equalExpr.setOperator(SQLBinaryOperator.Equality);
        equalExpr.setDbType(JdbcConstants.MYSQL);
        return equalExpr;
    }


    /**
     * 获取In表达式,即 columnName IN (value1,value2...)
     * @param columnName 列名
     * @param values 数值
     * @return In表达式,即 columnName IN (value1,value2...)
     */
    public static final SQLExpr getInExpr(String columnName, List<Integer> values) {
        SQLInListExpr inList = new SQLInListExpr();
        inList.setExpr(new SQLIdentifierExpr(columnName));
        inList.setTargetList(IntegerConvertSQLExpr(values));
        return inList;
    }

    /**
     * 将Integer类型的数据转换为SQLExpr类型的数值
     * @param values Integer类型数值列表
     * @return SQLExpr类型的列表
     */
    private static final List<SQLExpr> IntegerConvertSQLExpr(List<Integer> values) {
        List<SQLExpr> sqlExprs = new ArrayList<>();
        for(Integer value : values) {
            SQLNumberExpr valueExpr = new SQLNumberExpr();
            valueExpr.setNumber(value);
            sqlExprs.add(valueExpr);
        }
        return sqlExprs;
    }


}
