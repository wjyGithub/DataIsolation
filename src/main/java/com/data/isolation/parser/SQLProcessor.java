package com.data.isolation.parser;

import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.data.isolation.filter.FilterManagement;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 如果需要使用数据隔离，需要项目上对sql做出如下规定：
 * 1. 不予许使用right连接,因为对于right通过可以使用left连接替代
 * 2. 不需要使用","连接符,因为","连接符通常可以使用inner连接替代
 *
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/14 19:43.
 */
public class SQLProcessor {


    /**
     * 处理不含有union/union all的sql
     * @param queryBlock 简单的sql
     */
    public static void selectQueryBlockProcessor(SQLSelectQueryBlock queryBlock) {
        List<SQLSelectItem> selectList = queryBlock.getSelectList();
        FilterManagement filterManagement = FilterManagement.getInstance();
        for(SQLSelectItem sqlSelectItem : selectList) {
            filterManagement.doFilter(sqlSelectItem);
        }

    }

}
