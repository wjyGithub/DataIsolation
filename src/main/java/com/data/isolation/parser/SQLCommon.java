package com.data.isolation.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;

/**
 * Created by jianyuan.wei@hand-china.com
 * on 2019/7/19 14:41.
 */
public abstract class SQLCommon {

    public SQLCommon(){}

    /**
     * 提取where表达式
     * @param sqlExpr where表达式
     * @return
     */
    public static SQLBinaryOpExpr extractWhereExpr(SQLExpr sqlExpr) {
        if(sqlExpr instanceof SQLBinaryOpExpr) {
            return (SQLBinaryOpExpr)sqlExpr;
        }
        return null;
    }

    public static SQLSelectQueryBlock extractQueryBlock(SQLExistsExpr existsExpr) {
        SQLSelect subQuery = existsExpr.getSubQuery();
        SQLSelectQuery query = subQuery.getQuery();
        if(query instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock)query;
        }
        return null;
    }

    public static SQLUnionQuery extractUnionQuery(SQLExistsExpr existsExpr) {
        SQLSelect subQuery = existsExpr.getSubQuery();
        SQLSelectQuery query = subQuery.getQuery();
        if(query instanceof SQLUnionQuery) {
            return (SQLUnionQuery)query;
        }
        return null;
    }

    public static SQLSelectQueryBlock extractQueryBlock(SQLInSubQueryExpr inSubQueryExpr) {
        SQLSelect subQuery = inSubQueryExpr.getSubQuery();
        SQLSelectQuery query = subQuery.getQuery();
        if(query instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock)query;
        }
        return null;
    }


    public static SQLUnionQuery extractUnionQuery(SQLInSubQueryExpr inSubQueryExpr) {
        SQLSelect subQuery = inSubQueryExpr.getSubQuery();
        SQLSelectQuery query = subQuery.getQuery();
        if(query instanceof SQLUnionQuery) {
            return (SQLUnionQuery)query;
        }
        return null;
    }
    /**
     * 提取sql语句
     *
     * @param queryExpr sql表达式
     * @return
     */
    public static SQLSelectQueryBlock extractQueryBlock(SQLQueryExpr queryExpr) {
        SQLSelect subQuery = queryExpr.getSubQuery();
        SQLSelectQuery query = subQuery.getQuery();
        if (query instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock) query;
        }
        return null;
    }

    /**
     * 提取union/union all的sql语句
     *
     * @param queryExpr sql表达式
     * @return
     */
    public static SQLUnionQuery extractUnionQuery(SQLQueryExpr queryExpr) {
        SQLSelect subQuery = queryExpr.getSubQuery();
        SQLSelectQuery query = subQuery.getQuery();
        if (query instanceof SQLUnionQuery) {
            return (SQLUnionQuery) query;
        }
        return null;
    }

    /**
     * 提取sql语句
     * @param tableSource 子查询(查询表)
     * @return 不包含union/union all的sql语句
     */
    public static final SQLSelectQueryBlock extractQueryBlock(SQLSubqueryTableSource tableSource) {
        SQLSelect select = tableSource.getSelect();
        SQLSelectQuery query = select.getQuery();
        if(query instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock) query;
        }
        return null;
    }

    /**
     * 提取union/union all语句
     * @param tableSource 子查询(查询表)
     * @return 包含union/union all的sql语句
     */
    public static final SQLUnionQuery extractUnionQuery(SQLSubqueryTableSource tableSource){
        SQLSelect select = tableSource.getSelect();
        SQLSelectQuery query = select.getQuery();
        if(query instanceof SQLUnionQuery) {
            return (SQLUnionQuery)query;
        }
        return null;
    }
}
