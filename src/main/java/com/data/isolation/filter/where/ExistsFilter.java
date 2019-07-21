package com.data.isolation.filter.where;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.data.isolation.filter.WhereExprFilter;
import com.data.isolation.parser.SQLCommon;
import com.data.isolation.parser.SQLUtil;
import org.springframework.stereotype.Component;

/**
 * exist表达式
 * Created by jianyuan.wei@hand-china.com
 * on 2019/7/16 18:30.
 */
@Component
public class ExistsFilter extends SQLCommon implements WhereExprFilter {

    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean whereExprProcess(SQLExpr sqlExpr) {
        if(sqlExpr instanceof SQLExistsExpr) {
            SQLSelectQueryBlock queryBlock = extractQueryBlock((SQLExistsExpr) sqlExpr);
            if(queryBlock != null) {
                SQLUtil.processPlainSelect(queryBlock);
                return true;
            }

            SQLUnionQuery unionQuery = extractUnionQuery((SQLExistsExpr) sqlExpr);
            if(unionQuery != null) {
                SQLUtil.processUnionSelect(unionQuery);
                return true;
            }
        }
        return false;
    }
}
