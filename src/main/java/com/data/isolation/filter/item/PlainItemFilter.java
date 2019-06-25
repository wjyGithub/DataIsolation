package com.data.isolation.filter.item;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.data.isolation.filter.SelectItemFilter;
import com.data.isolation.parser.SQLUtil;
import org.springframework.stereotype.Component;

/**
 * 处理简单的子查询过滤器,不包含union/union all
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/14 16:18.
 */
@Component
public class PlainItemFilter implements SelectItemFilter {
    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean selectItemProcessor(SQLSelectItem selectItem) {
        log.info("start PlainItemFilter....");
        SQLExpr expr = selectItem.getExpr();
        if(expr instanceof SQLQueryExpr) {
            SQLQueryExpr queryExpr = (SQLQueryExpr)expr;
            SQLSelect subQuery = queryExpr.getSubQuery();
            SQLSelectQuery selectQuery = subQuery.getQuery();
            if(selectQuery instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock)selectQuery;
                SQLUtil.processPlainSelect(selectQueryBlock);
                log.info("process success PlainItemFilter....");
                return true;
            }
        }
        return false;
    }
}
