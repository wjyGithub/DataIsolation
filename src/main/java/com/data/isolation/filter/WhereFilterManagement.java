package com.data.isolation.filter;

import com.alibaba.druid.sql.ast.SQLExpr;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * where过滤条件管理器
 * Created by jianyuan.wei@hand-china.com
 * on 2019/7/19 10:31.
 */
@Component
public class WhereFilterManagement {

    private static WhereFilterManagement filterManagement;

    @PostConstruct
    public void init(){
        filterManagement = this;
    }

    /**
     * 框架默认提供的过滤器
     */
    private List<WhereExprFilter> defaultWhereFilterManagerments;

    /**
     * 用户自定义的过滤器
     */
    private List<WhereExprFilter> customizeWhereFilterManagerments;

    private WhereFilterManagement() {
        defaultWhereFilterManagerments = new ArrayList<>();
        customizeWhereFilterManagerments = new ArrayList<>();

    }

    /**
     * 返回查询列的过滤器中心
     *
     * @return 查询列过滤器中心
     */
    private List<WhereExprFilter> getItemFilterManagerments() {
        List<WhereExprFilter> itemFilterManagers = new ArrayList<>();
        itemFilterManagers.addAll(defaultWhereFilterManagerments);
        itemFilterManagers.addAll(customizeWhereFilterManagerments);
        return itemFilterManagers;
    }

    /**
     * 第三方的过滤器注册中心
     * @param itemFilter 过滤器
     */
    public void registerItemFilter(WhereExprFilter itemFilter) {
        if (itemFilter != null) {
            customizeWhereFilterManagerments.add(itemFilter);
            //从小到大排序
            Collections.sort(customizeWhereFilterManagerments, Comparator.comparingInt(o1 -> o1.filterOrder()));
        }
    }

    /**
     * 框架默认的过滤器注册中心
     * @param itemFilter 过滤器
     */
    protected void registerDefaultItemFilter(WhereExprFilter itemFilter) {
        if(itemFilter != null) {
            defaultWhereFilterManagerments.add(itemFilter);
            //从小到大排序
            Collections.sort(defaultWhereFilterManagerments,Comparator.comparingInt(o1 -> o1.filterOrder()));
        }
    }

    /**
     * 过滤器处理
     * @param sqlExpr 条件表达式
     */
    public void doFilter(SQLExpr sqlExpr) {
        List<WhereExprFilter> whereFilterManagerments = getItemFilterManagerments();
        for(WhereExprFilter whereExprFilter : whereFilterManagerments) {
            if(whereExprFilter.whereExprProcess(sqlExpr)) {
                break;
            }
        }
    }

    public static WhereFilterManagement getInstance(){
        return filterManagement;
    }

    @Override
    public String toString() {
        return getItemFilterManagerments().toString();
    }
}
