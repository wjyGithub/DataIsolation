package com.data.isolation.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.util.JdbcConstants;
import javafx.scene.chart.ValueAxis;
import sun.awt.geom.AreaOp;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/11 20:50.
 */
public class WhereUtil {


    /**
     * 添加等值条件
     * @param columnName 字段名
     * @param value 数值
     * @return columnName = value
     */
    public static final SQLExpr getEqualTo(String columnName,Integer value) {
        SQLBinaryOpExpr binaryOpExpr = new SQLBinaryOpExpr();
        binaryOpExpr.setLeft(new SQLIdentifierExpr(columnName));
        binaryOpExpr.setRight(new SQLNumberExpr(value));
        binaryOpExpr.setOperator(SQLBinaryOperator.Equality);
        binaryOpExpr.setDbType(JdbcConstants.MYSQL);
        return binaryOpExpr;
    }

    /**
     * 获取IN
     * @param columnName 字段名
     * @param values 数值
     * @return columnName IN (values)
     */
    public static final SQLExpr getInExpr(String columnName, List<Integer> values) {
        SQLInListExpr inListExpr = new SQLInListExpr();
        inListExpr.setExpr(new SQLIdentifierExpr(columnName));
        inListExpr.setTargetList(convertSQLExprList(values));
        return inListExpr;
    }

    private static final List<SQLExpr> convertSQLExprList(List<Integer> values) {
        List<SQLExpr> sqlExprs = new ArrayList<>();
        for(Integer value : values) {
            SQLNumberExpr numberExpr = new SQLNumberExpr();
            numberExpr.setNumber(value);
            sqlExprs.add(numberExpr);
        }
        return sqlExprs;
    }
}
