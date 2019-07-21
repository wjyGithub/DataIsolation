package com.data.isolation.filter.where;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.data.isolation.filter.WhereExprFilter;
import com.data.isolation.parser.SQLCommon;
import com.data.isolation.parser.SQLUtil;
import org.springframework.stereotype.Component;

/**
 * 处理where第一个即为子查询的情况,即 where id = (select id from t_thing)
 * Created by jianyuan.wei@hand-china.com
 * on 2019/7/19 11:33.
 */
@Component
public class SubQueryFilter extends SQLCommon implements WhereExprFilter {

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean whereExprProcess(SQLExpr sqlExpr) {
        if(sqlExpr instanceof SQLQueryExpr) {

            SQLSelectQueryBlock queryBlock = extractQueryBlock((SQLQueryExpr) sqlExpr);
            if(queryBlock != null) {
                SQLUtil.processPlainSelect(queryBlock);
                return true;
            }

            SQLUnionQuery unionQuery = extractUnionQuery((SQLQueryExpr) sqlExpr);
            if(unionQuery != null) {
                SQLUtil.processUnionSelect(unionQuery);
                return true;
            }
        }
        return false;
    }
}
