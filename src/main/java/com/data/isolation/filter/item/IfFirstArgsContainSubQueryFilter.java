package com.data.isolation.filter.item;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.data.isolation.filter.SelectItemFilter;
import com.data.isolation.parser.SQLCommon;
import com.data.isolation.parser.SQLUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SELECT
 * if(
 *   ( SELECT
 *       u.id
 *     FROM
 *       hap_framework_service.lookup l
 *       JOIN hap_framework_service.lookup_value lv ON l.lookup_id = lv.lookup_id
 *       OIN hap_user_service.`user` u ON lv.CODE = u.`name`
 *     WHERE
 *       l.CODE = 'SYS.THING_AUTH_MGR_USER'
 *       AND u.id = ?
 *     GROUP BY
 *       u.id
 *   ) IS NULL, 1,0
 * ) AS existUserName
 * if第一个参数中包含子查询，可以在左边，也可以在右边，或者两边
 * Created by jianyuan.wei@hand-china.com
 * on 2019/7/21 0:44.
 */
@Component
public class IfFirstArgsContainSubQueryFilter extends SQLCommon implements SelectItemFilter {
    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean selectItemProcessor(SQLExpr selectItem) {

        if(selectItem instanceof SQLMethodInvokeExpr) {
            SQLMethodInvokeExpr methodExpr = (SQLMethodInvokeExpr)selectItem;
            List<SQLExpr> arguments = methodExpr.getArguments();
            SQLExpr sqlExpr = arguments.get(0);
            if(sqlExpr instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr binaryExp = (SQLBinaryOpExpr)sqlExpr;
                SQLExpr left = binaryExp.getLeft();
                if(left instanceof SQLQueryExpr) { //子查询
                    SQLSelectQueryBlock leftQueryBlock = extractQueryBlock((SQLQueryExpr) left);
                    if (leftQueryBlock != null) {
                        SQLUtil.processPlainSelect(leftQueryBlock);
                    }

                    SQLUnionQuery leftUnionQuery = extractUnionQuery((SQLQueryExpr) left);
                    if (leftUnionQuery != null) {
                        SQLUtil.processUnionSelect(leftUnionQuery);
                    }
                }

                SQLExpr right = binaryExp.getRight();
                if(right instanceof SQLQueryExpr) {
                    SQLSelectQueryBlock rightQueryBlock = extractQueryBlock((SQLQueryExpr) right);
                    if(rightQueryBlock != null) {
                        SQLUtil.processPlainSelect(rightQueryBlock);
                    }

                    SQLUnionQuery rightUnionQuery = extractUnionQuery((SQLQueryExpr) right);
                    if(rightUnionQuery != null) {
                        SQLUtil.processUnionSelect(rightUnionQuery);
                    }
                }
                return true;
            }
        }
        return false;
    }
}
