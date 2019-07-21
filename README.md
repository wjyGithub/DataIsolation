数据隔离框架
# 简介
数据隔离框架主要用于为每条sql动态的添加相同的过滤条件，无需开发人员手动添加隔离条件    
该框架在设计上，采用过滤器链的方式，用于解决不同复杂度的sql解析，对于目前框架不提供的sql写法,  
开发者可自己实现接口将解析逻辑添加到该框架中  
该框架在使用时需要对sql做一定的约束,并约束并不会弱化sql的功能,只是为了更好的规范sql的编写,方便框架解析:  
1. 不允许使用right join进行sql的连接操作,可以使用left join进行替代
2. 不允许使用逗号(,)进行sql的连接操作，可使用inner join进行替代


# 框架设计原理
![框架设计原理](https://github.com/wjyGithub/DataIsolation/blob/master/src/main/resources/images/%E6%95%B0%E6%8D%AE%E9%9A%94%E7%A6%BB%E8%AE%BE%E8%AE%A1.png)

# 用法
在pom.xml里面直接引入依赖即可
```text
<dependency>
    <groupId>com.wjy</groupId>
    <artifactId>data-isolation</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

# 定制化解析
该框架对外提供了两个接口，分别用于开发者定制化解析sql  
查询列的定制化开发
```java
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
    boolean selectItemProcessor(SQLExpr selectItem);
}
```
where条件的定制化开发
```java
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
```
如:
```java
/**
 * SELECT
 * if(
 *   ( SELECT
 *       u.id
 *     FROM
 *       hap_framework_service.lookup l
 *       JOIN hap_framework_service.lookup_value lv ON l.lookup_id = lv.lookup_id
 *       OIN hap_user_service.`user` u ON lv.CODE = u.`name`
 *     WHERE
 *       l.CODE = 'SYS.THING_AUTH_MGR_USER'
 *       AND u.id = ?
 *     GROUP BY
 *       u.id
 *   ) IS NULL, 1,0
 * ) AS existUserName
 * if第一个参数中包含子查询，可以在左边，也可以在右边，或者两边
 */
@Component
public class IfFirstArgsContainSubQueryFilter extends SQLCommon implements SelectItemFilter {
    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean selectItemProcessor(SQLExpr selectItem) {

        if(selectItem instanceof SQLMethodInvokeExpr) {
            SQLMethodInvokeExpr methodExpr = (SQLMethodInvokeExpr)selectItem;
            List<SQLExpr> arguments = methodExpr.getArguments();
            SQLExpr sqlExpr = arguments.get(0);
            if(sqlExpr instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr binaryExp = (SQLBinaryOpExpr)sqlExpr;
                SQLExpr left = binaryExp.getLeft();
                if(left instanceof SQLQueryExpr) { //子查询
                    SQLSelectQueryBlock leftQueryBlock = extractQueryBlock((SQLQueryExpr) left);
                    if (leftQueryBlock != null) {
                        SQLUtil.processPlainSelect(leftQueryBlock);
                    }

                    SQLUnionQuery leftUnionQuery = extractUnionQuery((SQLQueryExpr) left);
                    if (leftUnionQuery != null) {
                        SQLUtil.processUnionSelect(leftUnionQuery);
                    }
                }

                SQLExpr right = binaryExp.getRight();
                if(right instanceof SQLQueryExpr) {
                    SQLSelectQueryBlock rightQueryBlock = extractQueryBlock((SQLQueryExpr) right);
                    if(rightQueryBlock != null) {
                        SQLUtil.processPlainSelect(rightQueryBlock);
                    }

                    SQLUnionQuery rightUnionQuery = extractUnionQuery((SQLQueryExpr) right);
                    if(rightUnionQuery != null) {
                        SQLUtil.processUnionSelect(rightUnionQuery);
                    }
                }
                return true;
            }
        }
        return false;
    }
}
```
