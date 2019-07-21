package com.data.isolation.parser;


import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.data.isolation.exception.NotSupportJoinTypeException;
import com.data.isolation.filter.ItemFilterManagement;
import com.data.isolation.filter.WhereFilterManagement;
import org.springframework.util.StringUtils;
import java.util.Arrays;
import java.util.List;
import static com.data.isolation.constant.IsolationConstant.ORGANIZATION_ID;
import static com.data.isolation.constant.IsolationConstant.PROJECT_ID;
import static com.data.isolation.constant.IsolationConstant.TENANT_ID;

/**
 * 如果需要使用数据隔离，需要项目上对sql做出如下规定：
 * 1. 不予许使用right连接,因为对于right通过可以使用left连接替代
 * 2. 不需要使用","连接符,因为","连接符通常可以使用inner连接替代
 * select if((select count(1) from xxx) / (select count(1) from xxxx),1,0)
 * 不能处理该类型的sql语句
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/11 18:39.
 */
public class SQLUtil extends SQLCommon {


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

    /**
     * 处理带有union/union all的sql
     *
     * @param unionQuery 含有union/union all的sql
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
     *
     * @param selectItem 字段
     */
    private static final void processQueryInSelectItem(SQLSelectItem selectItem) {
        ItemFilterManagement managements = ItemFilterManagement.getInstance();
        SQLExpr expr = selectItem.getExpr();
        managements.doFilter(expr);
    }

    /**
     * 处理where条件的子查询
     * @param where where条件
     */
    private static void whereProcessor(SQLExpr where) {
        SQLBinaryOpExpr whereExpr = extractWhereExpr(where);
        if (whereExpr != null) {
            SQLExpr rightExpr = whereExpr.getRight();
            WhereFilterManagement whereInstance = WhereFilterManagement.getInstance();
            whereInstance.doFilter(rightExpr);

            SQLExpr leftExpr = whereExpr.getLeft();
            whereProcessor(leftExpr);

        }
    }

    /**
     * 处理操作表
     *
     * @param source
     * @param queryBlock
     */
    public static final void processTableSource(SQLTableSource source, SQLSelectQueryBlock queryBlock) {
        if (source instanceof SQLExprTableSource) {//单表
            SQLExprTableSource tableSource = (SQLExprTableSource) source;
            String prefix = getSelfIfNotTableAlias(tableSource);
            addOrgIdsAndTenantIdByWhere(prefix, queryBlock);
        } else if (source instanceof SQLJoinTableSource) { // 处理left/inner等连接的表
            processJoinTableSource((SQLJoinTableSource) source, queryBlock);
        } else if (source instanceof SQLSubqueryTableSource) { //子查询,不包含union/union all
            SQLSelectQueryBlock selectQueryBlock = extractQueryBlock((SQLSubqueryTableSource) source);
            if (selectQueryBlock != null) {
                processPlainSelect(selectQueryBlock);
            }
            SQLUnionQuery unionQuery = extractUnionQuery((SQLSubqueryTableSource) source);
            if (unionQuery != null) {
                processUnionSelect(unionQuery);
            }
        } else if (source instanceof SQLUnionQueryTableSource) { //子查询 包含union/union all
            SQLUnionQueryTableSource unionQueryTable = (SQLUnionQueryTableSource) source;
            SQLUnionQuery union = unionQueryTable.getUnion();
            processUnionSelect(union);
        }
    }

    /**
     * 处理left/inner,不处理right/,
     *
     * @param joinTableSource
     * @param queryBlock
     */
    public static final void processJoinTableSource(SQLJoinTableSource joinTableSource, SQLSelectQueryBlock queryBlock) {
        //判断是否包含right/,
        if (!isLeftOrInner(joinTableSource)) {
            throw new NotSupportJoinTypeException("不支持right join和,(逗号)连表操作;请使用left join和inner join替代");
        }

        //处理右表
        SQLTableSource rightTableSource = joinTableSource.getRight();
        if (rightTableSource instanceof SQLSubqueryTableSource) { //子查询

            //简单的sql
            SQLSelectQueryBlock selectQueryBlock = extractQueryBlock((SQLSubqueryTableSource) rightTableSource);
            if (selectQueryBlock != null) {
                processPlainSelect(selectQueryBlock);
            }
            //含有union/union all的sql语句
            SQLUnionQuery unionQuery = extractUnionQuery((SQLSubqueryTableSource) rightTableSource);
            if (unionQuery != null) {
                processUnionSelect(unionQuery);
            }
            addConditionByJoin(joinTableSource, queryBlock);
        } else if (rightTableSource instanceof SQLExprTableSource) { //单表
            addConditionByJoin(joinTableSource, queryBlock);
        }

        //处理左表
        SQLTableSource leftTableSource = joinTableSource.getLeft();
        if (leftTableSource instanceof SQLSubqueryTableSource) { //子查询

            SQLSelectQueryBlock selectQueryBlock = extractQueryBlock((SQLSubqueryTableSource) leftTableSource);
            if (queryBlock != null) {
                processPlainSelect(selectQueryBlock);
            }
            SQLUnionQuery unionQuery = extractUnionQuery((SQLSubqueryTableSource) leftTableSource);
            if (unionQuery != null) {
                processUnionSelect(unionQuery);
            }

        } else if (leftTableSource instanceof SQLJoinTableSource) { //left/inner
            SQLJoinTableSource joinTable = (SQLJoinTableSource) leftTableSource;
            processJoinTableSource(joinTable, queryBlock);
        } else if (leftTableSource instanceof SQLExprTableSource) {//单表
            String predix = getSelfIfNotTableAlias((SQLExprTableSource) leftTableSource);
            addOrgIdsAndTenantIdByWhere(predix, queryBlock);
        }
    }


    private static final Boolean isLeftOrInner(SQLJoinTableSource joinTableSource) {
        return joinTableSource.getJoinType() != SQLJoinTableSource.JoinType.COMMA &&
                joinTableSource.getJoinType() != SQLJoinTableSource.JoinType.RIGHT_OUTER_JOIN;
    }

    /**
     * join类型的查询，为on添加过滤条件
     *
     * @param joinTableSource join连接
     */
    public static final void addConditionByJoin(SQLJoinTableSource joinTableSource, SQLSelectQueryBlock queryBlock) {
        SQLTableSource rightTable = joinTableSource.getRight();
        if (rightTable instanceof SQLExprTableSource) { //单表
            String prefix = getSelfIfNotTableAlias((SQLExprTableSource) rightTable);
            addOrgIdsAndTenantIdForOn(prefix, joinTableSource);
        }
    }

    /**
     * 如果表的别名不存在，则返回表名自身;否则返回表的别名
     *
     * @param source 表信息
     * @return 表的别名或表名
     */
    private static final String getSelfIfNotTableAlias(SQLExprTableSource source) {
        return StringUtils.isEmpty(source.getAlias()) ? source.getName().getSimpleName() : source.getAlias();
    }

    private static final void addOrgIdsAndTenantIdForOn(String prefix, SQLJoinTableSource joinTableSource) {
//        if (condition.getOrgIds() != null && !condition.getOrgIds().isEmpty()) {
            SQLExpr organizationIds = ConditionUtil.getInExpr(prefix + "." + ORGANIZATION_ID, Arrays.asList(1));
            joinTableSource.addConditionnIfAbsent(organizationIds);
//        }
//        if (condition.getTenantId() != null) {
            SQLExpr tenantId = ConditionUtil.getEqualTo(prefix + "." + TENANT_ID, 1);
            joinTableSource.addConditionnIfAbsent(tenantId);
//        }
//        if(condition.getProjectIds() != null && !condition.getProjectIds().isEmpty()) {
            SQLExpr projectId = ConditionUtil.getInExpr(prefix + "." + PROJECT_ID, Arrays.asList(1));
            joinTableSource.addConditionnIfAbsent(projectId);
//        }
    }

    /**
     * 为Sql添加组织和租户过滤条件
     *
     * @param queryBlock 整条sql信息
     */
    private static final void addOrgIdsAndTenantIdByWhere(String prefix, SQLSelectQueryBlock queryBlock) {
//        if (condition.getOrgIds() != null) {
            SQLExpr organizationIds = ConditionUtil.getInExpr(prefix + "." + ORGANIZATION_ID, Arrays.asList(1));
            queryBlock.addWhere(organizationIds);
//        }
//        if (condition.getTenantId() != null) {
            SQLExpr tenantId = ConditionUtil.getEqualTo(prefix + "." + TENANT_ID, 1);
            queryBlock.addWhere(tenantId);
//        }
//        if(condition.getProjectIds() != null) {
            SQLExpr projectId = ConditionUtil.getInExpr(prefix + "." + PROJECT_ID, Arrays.asList(1));
            queryBlock.addWhere(projectId);
//        }
    }
}
