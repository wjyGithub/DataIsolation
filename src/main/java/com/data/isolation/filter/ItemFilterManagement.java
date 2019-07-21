package com.data.isolation.filter;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 查询列过滤器管理中心
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/14 14:20.
 */
@Component
public class ItemFilterManagement {

    private static ItemFilterManagement filterManagement;

    @PostConstruct
    public void init(){
        filterManagement = this;
    }

    /**
     * 框架默认提供的过滤器
     */
    private List<SelectItemFilter> defaultItemFilterManagerments;

    /**
     * 用户自定义的过滤器
     */
    private List<SelectItemFilter> customizeItemFilterManagerments;

    private ItemFilterManagement() {
        defaultItemFilterManagerments = new ArrayList<>();
        customizeItemFilterManagerments = new ArrayList<>();

    }

    /**
     * 返回查询列的过滤器中心
     *
     * @return 查询列过滤器中心
     */
    private List<SelectItemFilter> getItemFilterManagerments() {
        List<SelectItemFilter> itemFilterManagers = new ArrayList<>();
        itemFilterManagers.addAll(defaultItemFilterManagerments);
        itemFilterManagers.addAll(customizeItemFilterManagerments);
        return itemFilterManagers;
    }

    /**
     * 第三方的过滤器注册中心
     * @param itemFilter 过滤器
     */
    public void registerItemFilter(SelectItemFilter itemFilter) {
        if (itemFilter != null) {
            customizeItemFilterManagerments.add(itemFilter);
            //从小到大排序
            Collections.sort(customizeItemFilterManagerments,Comparator.comparingInt(o1 -> o1.filterOrder()));
        }
    }

    /**
     * 框架默认的过滤器注册中心
     * @param itemFilter 过滤器
     */
    protected void registerDefaultItemFilter(SelectItemFilter itemFilter) {
        if(itemFilter != null) {
            defaultItemFilterManagerments.add(itemFilter);
            //从小到大排序
            Collections.sort(defaultItemFilterManagerments,Comparator.comparingInt(o1 -> o1.filterOrder()));
        }
    }

    /**
     * 过滤器处理
     * @param selectItem 查询列
     */
    public void doFilter(SQLExpr selectItem) {
        List<SelectItemFilter> itemFilterManagerments = getItemFilterManagerments();
        for(SelectItemFilter selectItemFilter : itemFilterManagerments) {
            if(selectItemFilter.selectItemProcessor(selectItem)) {
                break;
            }
        }
    }

    public static ItemFilterManagement getInstance(){
        return filterManagement;
    }

    @Override
    public String toString() {
        return getItemFilterManagerments().toString();
    }
}
