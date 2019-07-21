package com.data.isolation.filter.where;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.data.isolation.filter.WhereExprFilter;
import com.data.isolation.parser.SQLCommon;
import com.data.isolation.parser.SQLUtil;
import org.springframework.stereotype.Component;

/**
 * In表达式
 * Created by jianyuan.wei@hand-china.com
 * on 2019/7/16 18:28.
 */
@Component
public class InSubQueryFilter extends SQLCommon implements WhereExprFilter {

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean whereExprProcess(SQLExpr sqlExpr) {
        if(sqlExpr instanceof SQLInSubQueryExpr){
            SQLSelectQueryBlock queryBlock = extractQueryBlock((SQLInSubQueryExpr) sqlExpr);
            if(queryBlock != null) {
                SQLUtil.processPlainSelect(queryBlock);
                return true;
            }

            SQLUnionQuery unionQuery = extractUnionQuery((SQLInSubQueryExpr) sqlExpr);
            if(unionQuery != null) {
                SQLUtil.processUnionSelect(unionQuery);
                return true;
            }
        }
        return false;
    }

}
