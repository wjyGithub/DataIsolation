package com.data.isolation.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.util.StringUtils;
import com.data.isolation.filter.FilterManagement;
import com.data.isolation.filter.item.EqualToFilter;
import com.data.isolation.filter.item.PlainItemFilter;
import com.data.isolation.filter.item.UnionItemFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 如果需要使用数据隔离，需要项目上对sql做出如下规定：
 * 1. 不予许使用right连接,因为对于right通过可以使用left连接替代
 * 2. 不需要使用","连接符,因为","连接符通常可以使用inner连接替代
 * select if((select count(1) from xxx) / (select count(1) from xxxx),1,0)
 * 不能处理该类型的sql语句
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/11 18:39.
 */
public class SQLUtil {

    /**
     * 是否添加In,还是=
     */
    private static final boolean isIn = false;

    /**
     * 处理sql语句,不包含union/union all
     *
     * @param queryBlock 简单sql
     */
    public static final void processPlainSelect(SQLSelectQueryBlock queryBlock) {
        //处理sql查询的字段
        List<SQLSelectItem> selectColumns = queryBlock.getSelectList();
        for (SQLSelectItem selectItem : selectColumns) {
            processQueryInSelectItem(selectItem);
        }
        //处理表
        SQLTableSource tables = queryBlock.getFrom();
        processTableSource(tables, queryBlock);
        //处理where条件里面的子查询
        SQLExpr where = queryBlock.getWhere();
        whereProcessor(where);
    }

    private static void whereProcessor(SQLExpr where) {

    }

    /**
     * 处理表
     *
     * @param tableSource
     * @param queryBlock
     */
    public static final void processTableSource(SQLTableSource tableSource, SQLSelectQueryBlock queryBlock) {
        if (tableSource instanceof SQLExprTableSource) {
            //单表
            SQLExprTableSource exprTableSource = (SQLExprTableSource) tableSource;
            processExprTableSource(exprTableSource, queryBlock);
        } else if (tableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource joinTableSource = (SQLJoinTableSource) tableSource;
            processJoinTableSource(joinTableSource, queryBlock);
        } else if (tableSource instanceof SQLSubqueryTableSource) {
            //子查询,不包含union/union all
            SQLSubqueryTableSource subqueryTableSource = (SQLSubqueryTableSource) tableSource;
            SQLSelect select = subqueryTableSource.getSelect();
            SQLSelectQuery query = select.getQuery();
            if (query instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) query;
                processPlainSelect(selectQueryBlock);
            } else if (query instanceof SQLUnionQuery) {
                SQLUnionQuery unionQuery = (SQLUnionQuery) query;
                processUnionSelect(unionQuery);
            }
        } else if (tableSource instanceof SQLUnionQueryTableSource) {
            //子查询 包含union/union all
            SQLUnionQueryTableSource unionQueryTable = (SQLUnionQueryTableSource) tableSource;
            SQLUnionQuery union = unionQueryTable.getUnion();
            processUnionSelect(union);
        }
    }

    /**
     * 处理单表
     *
     * @param tableSource
     * @param queryBlock
     */
    public static final void processExprTableSource(SQLExprTableSource tableSource, SQLSelectQueryBlock queryBlock) {
        String columnName = getColumnName(tableSource);
        addWhere(columnName, queryBlock);
    }

    /**
     * 处理left/inner/, 不处理right
     *
     * @param joinTableSource
     * @param queryBlock
     */
    public static final void processJoinTableSource(SQLJoinTableSource joinTableSource, SQLSelectQueryBlock queryBlock) {
        //右连接
        SQLTableSource rightTableSource = joinTableSource.getRight();
        if (rightTableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource subqueryTableSource = (SQLSubqueryTableSource) rightTableSource;
            SQLSelect select = subqueryTableSource.getSelect();
            SQLSelectQuery query = select.getQuery();
            if (query instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) query;
                processPlainSelect(selectQueryBlock);
            } else if (query instanceof SQLUnionQuery) {
                SQLUnionQuery unionQuery = (SQLUnionQuery) query;
                processUnionSelect(unionQuery);
            }
            addConditionForRight(joinTableSource);

        } else if (rightTableSource instanceof SQLExprTableSource) {
            //单表
            SQLExprTableSource exprTableSource = (SQLExprTableSource) rightTableSource;
            String rightCondition = getColumnName(exprTableSource);
            addAndCondition(rightCondition, joinTableSource);
            addConditionForRight(joinTableSource);
        }

        //左连接
        SQLTableSource leftTableSource = joinTableSource.getLeft();
        //子查询
        if (leftTableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource subqueryTableSource = (SQLSubqueryTableSource) leftTableSource;
            SQLSelect select = subqueryTableSource.getSelect();
            SQLSelectQuery query = select.getQuery();
            if (query instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) query;
                processPlainSelect(selectQueryBlock);
            } else if (query instanceof SQLUnionQuery) {
                SQLUnionQuery unionQuery = (SQLUnionQuery) query;
                processUnionSelect(unionQuery);
            }
        } else if (leftTableSource instanceof SQLJoinTableSource) {
            //left/inner/,
            SQLJoinTableSource joinTable = (SQLJoinTableSource) leftTableSource;
            processJoinTableSource(joinTable, queryBlock);
        } else if (leftTableSource instanceof SQLExprTableSource) {
            //单表
            SQLExprTableSource exprTableSource = (SQLExprTableSource) leftTableSource;
            processExprTableSource(exprTableSource, queryBlock);
        }
    }

    public static final void addConditionForRight(SQLJoinTableSource joinTableSource) {
        SQLTableSource right = joinTableSource.getRight();
        if (right instanceof SQLExprTableSource) {
            String leftColumnName = getColumnName((SQLExprTableSource) right);
            addAndCondition(leftColumnName, joinTableSource);
        }
    }

    /**
     * 为左连接添加On条件
     *
     * @param joinTableSource join连接
     */
    public static final void addConditionForLeft(SQLJoinTableSource joinTableSource) {
        SQLTableSource leftTableSource = joinTableSource.getLeft();
        if (leftTableSource instanceof SQLExprTableSource) {
            String leftColumnName = getColumnName((SQLExprTableSource) leftTableSource);
            addAndCondition(leftColumnName, joinTableSource);
        } else if (leftTableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource joinTable = (SQLJoinTableSource) leftTableSource;
            SQLTableSource right = joinTable.getRight();
            if (right instanceof SQLExprTableSource) {
                String leftColumnName = getColumnName((SQLExprTableSource) right);
                addAndCondition(leftColumnName, joinTableSource);
            }
        }
    }

    public static final String getColumnName(SQLExprTableSource tableSource) {
        return StringUtils.isEmpty(tableSource.getAlias()) ? "organization_id" : tableSource.getAlias() + ".organization_id";
    }

    public static final void addWhere(String columnName, SQLSelectQueryBlock queryBlock) {
        if (isIn) {
            queryBlock.addWhere(WhereUtil.getInExpr(columnName, DataUtil.getOrganizationIds()));
        } else {
            queryBlock.addWhere(WhereUtil.getEqualTo(columnName, DataUtil.getOrganizationId()));
        }
    }

    public static final void addAndCondition(String columnName, SQLJoinTableSource joinTableSource) {
        if (isIn) {
            joinTableSource.addConditionn(WhereUtil.getInExpr(columnName, DataUtil.getOrganizationIds()));
        } else {
            joinTableSource.addConditionn(WhereUtil.getEqualTo(columnName, DataUtil.getOrganizationId()));
        }

    }


    /**
     * 处理带有union的sql
     *
     * @param unionQuery 含有union的sql
     */
    public static final void processUnionSelect(SQLUnionQuery unionQuery) {
        SQLSelectQuery rightSelectQuery = unionQuery.getRight();
        if (rightSelectQuery instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock rightSelectQueryBlock = (SQLSelectQueryBlock) rightSelectQuery;
            processPlainSelect(rightSelectQueryBlock);
        }

        SQLSelectQuery leftSelectQuery = unionQuery.getLeft();
        if (leftSelectQuery instanceof SQLSelectQueryBlock) {
            //简单sql
            SQLSelectQueryBlock leftSelectQueryBlock = (SQLSelectQueryBlock) leftSelectQuery;
            processPlainSelect(leftSelectQueryBlock);
        } else if (leftSelectQuery instanceof SQLUnionQuery) {
            //含有union的sql
            SQLUnionQuery leftUnionQuery = (SQLUnionQuery) leftSelectQuery;
            processUnionSelect(leftUnionQuery);
        }
    }

    /**
     * 处理字段中的子查询
     * @param selectItem 字段
     */
    private static final void processQueryInSelectItem(SQLSelectItem selectItem) {
        FilterManagement managements = FilterManagement.getInstance();
        managements.doFilter(selectItem);
    }
}
