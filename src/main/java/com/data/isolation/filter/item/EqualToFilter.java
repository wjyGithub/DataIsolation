package com.data.isolation.filter.item;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.data.isolation.filter.SelectItemFilter;
import com.data.isolation.parser.SQLCommon;
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
@Component(value = "selectEqualTo")
public class EqualToFilter extends SQLCommon implements SelectItemFilter{
    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean selectItemProcessor(SQLExpr selectItem) {
        if (selectItem instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr opExpr = (SQLBinaryOpExpr) selectItem;
            SQLExpr rightExpr = opExpr.getRight();
            if (rightExpr instanceof SQLQueryExpr && opExpr.getLeft() instanceof SQLVariantRefExpr) {
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
