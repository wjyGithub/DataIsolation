package com.data.isolation.filter.where;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.data.isolation.filter.WhereExprFilter;
import com.data.isolation.parser.SQLCommon;
import com.data.isolation.parser.SQLUtil;
import org.springframework.stereotype.Component;

/**
 * 等值(=)表达式
 * Created by jianyuan.wei@hand-china.com
 * on 2019/7/16 18:25.
 */
@Component(value = "whereEqualTo")
public class EqualToFilter extends SQLCommon implements WhereExprFilter {


    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean whereExprProcess(SQLExpr sqlExpr) {
        if(sqlExpr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr equalToExpr = (SQLBinaryOpExpr)sqlExpr;
            SQLExpr rightExpr = equalToExpr.getRight();
            if(rightExpr instanceof SQLQueryExpr) {
                //子查询
                SQLSelectQueryBlock queryBlock = extractQueryBlock((SQLQueryExpr) rightExpr);
                if(queryBlock != null) {
                    SQLUtil.processPlainSelect(queryBlock);
                    return true;
                }

                SQLUnionQuery unionQuery = extractUnionQuery((SQLQueryExpr) rightExpr);
                if(unionQuery != null) {
                    SQLUtil.processUnionSelect(unionQuery);
                    return true;
                }
            }
        }
        return false;
    }

}
