package com.data.isolation.filter;

import com.alibaba.druid.util.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jianyuan.wei@hand-china.com
 * on 2019/6/17 19:20.
 */
@Component
public class FilterRegister implements BeanPostProcessor {

    @Autowired
    private FilterManagement management;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        List<Class<?>> filters = Arrays.asList(interfaces);
        if(filters.contains(SelectItemFilter.class)) {
            Package pack = bean.getClass().getPackage();
            if(StringUtils.equals("com.data.isolation.filter.item",pack.getName())) {
                management.registerDefaultItemFilter((SelectItemFilter) bean);
            }else {
                management.registerItemFilter((SelectItemFilter) bean);
            }
        }
        return bean;
    }
}
