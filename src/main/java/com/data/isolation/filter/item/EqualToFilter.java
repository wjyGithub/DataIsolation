package com.data.isolation.filter.item;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.data.isolation.filter.SelectItemFilter;
import com.data.isolation.parser.SQLUtil;
import org.springframework.stereotype.Component;

/**
 * @cowCount := (ELECT count(1) FROM t_nz_cow t
 * WHERE t.DEL = 0  AND t.PRESENT = 1 AND t.FARM_UUID = tjh.FARM_UUID
 * AND t.HOUSE_UUID = tjh.UUID ) AS cowCount
 *
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/14 17:16.
 */
@Component
public class EqualToFilter implements SelectItemFilter{
    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean selectItemProcessor(SQLSelectItem selectItem) {
        log.info("start EqualToFilter......");
        SQLExpr expr = selectItem.getExpr();
        if (expr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr opExpr = (SQLBinaryOpExpr) expr;
            SQLExpr rightExpr = opExpr.getRight();
            if (rightExpr instanceof SQLQueryExpr && opExpr.getLeft() instanceof SQLVariantRefExpr) {
                SQLQueryExpr sqlQueryExpr = (SQLQueryExpr) rightExpr;
                SQLSelect subQuery = sqlQueryExpr.getSubQuery();
                SQLSelectQuery query = subQuery.getQuery();
                if (query instanceof SQLSelectQueryBlock) {
                    SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) query;
                    SQLUtil.processPlainSelect(queryBlock);
                } else if (query instanceof SQLUnionQuery) {
                    SQLUnionQuery unionQuery = (SQLUnionQuery) query;
                    SQLUtil.processUnionSelect(unionQuery);
                }
                log.info("process success EqualToFilter....");
                return true;
            }
        }
        return false;
    }
}
