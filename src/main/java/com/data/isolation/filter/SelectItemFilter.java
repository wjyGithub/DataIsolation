package com.data.isolation.filter;

import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 查询列操作过滤器
 * 该接口主要用于对查询类的操作
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/14 15:33.
 */
public interface SelectItemFilter {

    Logger log = LoggerFactory.getLogger(SelectItemFilter.class);
    /**
     * 过滤器的权重，比值越高,权重高
     * @return 权重值
     */
     int filterOrder();

    /**
     * 用于处理查询列
     * @param selectItem 查询出来的某一列
     * @return true: 中断后面的过滤器链  false:继续执行后面的过滤器链
     */
    boolean selectItemProcessor(SQLSelectItem selectItem);
}
