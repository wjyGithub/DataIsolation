数据隔离框架
# 简介
数据隔离框架主要用于为每条sql动态的添加相同的过滤条件，无需开发人员手动添加过滤添加  
该框架在设计上，采用过滤器链的方式，用于解决不同复杂度的sql解析，对于框架不支持的sql写法,  
开发者可自己实现接口将解析逻辑添加到该框架中

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
