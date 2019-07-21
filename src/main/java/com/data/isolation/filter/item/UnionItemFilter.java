package com.data.isolation.filter.item;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.data.isolation.filter.SelectItemFilter;
import com.data.isolation.parser.SQLCommon;
import com.data.isolation.parser.SQLUtil;
import org.springframework.stereotype.Component;

/**
 * union/union all相关的处理器
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/14 17:01.
 */
@Component
public class UnionItemFilter extends SQLCommon implements SelectItemFilter {

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean selectItemProcessor(SQLExpr selectItem) {
        if(selectItem instanceof SQLQueryExpr) {
            SQLUnionQuery unionQuery = extractUnionQuery((SQLQueryExpr) selectItem);
            if(unionQuery != null) {
                SQLUtil.processUnionSelect(unionQuery);
                log.info("process success UnionItemFilter....");
                return true;
            }
        }
        return false;
    }
}
