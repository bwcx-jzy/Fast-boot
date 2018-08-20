<p align="center">
	<img src="https://images.gitee.com/uploads/images/2018/0814/182554_be855ac3_804942.png" width="400">
</p>

> 针对SpringBoot 封装的一系列的快捷包 提供公共的Controller、自动化拦截器、启动加载资源接口、线程池管理

[![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/cn/jiangzeyin/fast-boot/common-parent/maven-metadata.xml.svg)](https://mvnrepository.com/artifact/cn.jiangzeyin.fast-boot/common-parent)
![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)
![jdk](https://img.shields.io/badge/JDK-1.8+-green.svg)

## 安装

### Maven
在项目的pom.xml 添加如下代码

    <parent>
        <groupId>cn.jiangzeyin.fast-boot</groupId>
        <artifactId>common-parent</artifactId>
        <version>VERSION</version>
    </parent>

注：VERSION 请更换为公共maven库最新的版本号

## 版本变更

- [Release版本变更说明](https://gitee.com/jiangzeyin/common-parent/blob/master/CHANGELOG.md)

## 文档

[博客地址](http://blog.csdn.net/jiangzeyin_/article/details/78709043)

[参考API](https://apidoc.gitee.com/jiangzeyin/common-parent/)

##依赖
[SpringBoot starter-web](https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#spring-boot-starter-web)

[HuTool](https://gitee.com/loolly/hutool/)

[fastjson](https://github.com/alibaba/fastjson)

## 子项目：

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


### 提供bug反馈或建议

- [码云](https://gitee.com/jiangzeyin/common-parent/issues)
- [Gtihub](https://github.com/jiangzeyin/Fast-boot/issues)
