package com.data.isolation.filter.item;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.data.isolation.filter.SelectItemFilter;
import com.data.isolation.parser.SQLUtil;
import org.springframework.stereotype.Component;

/**
 * union/union all相关的处理器
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/14 17:01.
 */
@Component
public class UnionItemFilter implements SelectItemFilter {

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean selectItemProcessor(SQLSelectItem selectItem) {
        log.info("start UnionItemFilter....");
        SQLExpr expr = selectItem.getExpr();
        if(expr instanceof SQLQueryExpr) {
            SQLQueryExpr queryExpr = (SQLQueryExpr)expr;
            SQLSelect subQuery = queryExpr.getSubQuery();
            SQLSelectQuery selectQuery = subQuery.getQuery();
            if(selectQuery instanceof SQLUnionQuery) {
                SQLUnionQuery unionQuery = (SQLUnionQuery)selectQuery;
                SQLUtil.processUnionSelect(unionQuery);
                log.info("process success UnionItemFilter....");
                return true;
            }
        }
        return false;
    }
}
