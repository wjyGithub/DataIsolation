package com.data.isolation.parser;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.util.JdbcConstants;
import java.util.List;

/**
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/11 22:17.
 */
public class DruidUtil {


    public static final String  addWhereForSelectSql(String sql) {
        String mysql = JdbcConstants.MYSQL;
        List<SQLStatement> sqlStatements = SQLUtils.toStatementList(sql, mysql);
        SQLStatement sqlStatement = sqlStatements.get(0);
        if(sqlStatement instanceof SQLSelectStatement) {
            SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
            //获取sql
            SQLSelect select = sqlSelectStatement.getSelect();
            SQLSelectQuery query = select.getQuery();
            if (query instanceof SQLSelectQueryBlock) {
                SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) query;
                SQLUtil.processPlainSelect(sqlSelectQueryBlock);
            } else if(query instanceof SQLUnionQuery) {
                SQLUnionQuery unionQuery = (SQLUnionQuery)query;
                SQLUtil.processUnionSelect(unionQuery);
            }
            return query.toString();
        }
        return null;
    }
}
