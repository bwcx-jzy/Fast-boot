# common-parent 
> 针对SpringBoot 封装的一系列的快捷包

**maven 坐标**

    <parent>
        <groupId>cn.jiangzeyin.fast-boot</groupId>
        <artifactId>common-parent</artifactId>
        <version>VERSION</version>
    </parent>

[![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/cn/jiangzeyin/fast-boot/common-parent/maven-metadata.xml.svg)](https://mvnrepository.com/artifact/cn.jiangzeyin.fast-boot/common-parent)
![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)
![jdk](https://img.shields.io/badge/JDK-1.8+-green.svg)


注：VERSION 请更换为公共maven库最新的版本号


 https://mvnrepository.com/artifact/cn.jiangzeyin.fast-boot/common-parent

博客地址：http://blog.csdn.net/jiangzeyin_/article/details/78709043

javadoc：https://apidoc.gitee.com/jiangzeyin/common-parent/

子项目：

# 1.common-boot

> 此模块主要封装Boot 程序的常用工具和内存缓存
>
>如：公共的Controller、自动化拦截器、启动加载资源接口、线程池管理

**maven坐标**

    <dependency>
        <groupId>cn.jiangzeyin.fast-boot</groupId>
        <artifactId>common-boot</artifactId>
    </dependency>


地址：https://gitee.com/jiangzeyin/common-parent/tree/master/common-boot

# 2.common-redis

> 此模块主要是封装了SpringBoot 里面包含Redis 的操作支持动态指定使用数据库编号


**maven坐标**

    <dependency>
        <groupId>cn.jiangzeyin.fast-boot</groupId>
        <artifactId>common-redis</artifactId>
    </dependency>

特点：支持动态指定Redis 索引库编号

地址：https://gitee.com/jiangzeyin/common-parent/tree/master/common-redis
