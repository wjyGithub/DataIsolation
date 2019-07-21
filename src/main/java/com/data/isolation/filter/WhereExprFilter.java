package com.data.isolation.filter;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jianyuan.wei@hand-china.com
 * on 2019/7/16 17:37.
 */
public interface WhereExprFilter {

    Logger log = LoggerFactory.getLogger(SelectItemFilter.class);
    /**
     * 过滤器的权重，比值越高,权重高
     * @return 权重值
     */
    int filterOrder();

    /**
     * 用于处理查询列
     * @param sqlExpr where条件的某一列
     * @return true: 中断后面的过滤器链  false:继续执行后面的过滤器链
     */
    boolean whereExprProcess(SQLExpr sqlExpr);
}
