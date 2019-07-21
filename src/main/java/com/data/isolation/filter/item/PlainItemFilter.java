package com.data.isolation.filter.item;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.data.isolation.filter.SelectItemFilter;
import com.data.isolation.parser.SQLCommon;
import com.data.isolation.parser.SQLUtil;
import org.springframework.stereotype.Component;

/**
 * 处理简单的子查询过滤器,不包含union/union all
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/14 16:18.
 */
@Component
public class PlainItemFilter extends SQLCommon implements SelectItemFilter {
    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean selectItemProcessor(SQLExpr selectItem) {
        if(selectItem instanceof SQLQueryExpr) {
            SQLSelectQueryBlock queryBlock = extractQueryBlock((SQLQueryExpr) selectItem);
            if(queryBlock != null) {
                SQLUtil.processPlainSelect(queryBlock);
                log.info("process success PlainItemFilter....");
                return true;
            }
        }
        return false;
    }
}
