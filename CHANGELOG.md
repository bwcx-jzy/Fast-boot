# 版本日志

## 1.2.11

*  【common-boot】 文件上传对象添加重置文件参数方法
*  【common-boot】 拦截器自动识别UTF-8 兼容更多情况 

## 1.2.10

*  【common-boot】 文件上传添加支持保存原文件名
*  【common-redis】 判断是否配置对应信息 

---------------------------------------------------------------------
## 1.2.8-9

*  改变文件上传模式使用Builder cn.jiangzeyin.controller.multipart.MultipartFileBuilder
*  Controller 基类变更为 cn.jiangzeyin.controller.base.AbstractController

---------------------------------------------------------------------
## 1.2.7

*  自动创建 ServletContext.TEMPDIR 目录
*  上传文件能指定文件大
*  多方式上传文件

---------------------------------------------------------------------
## 1.2.6

*  AbstractBaseControl 新增获取所有header 和 原始参数方法
*  BaseCallbackController 提供静态方法获取客户端ip地址
---------------------------------------------------------------------

## 1.2.5

*  几处代码优化
*  pom 调整
---------------------------------------------------------------------

## 1.2.4
*  更新hutool 版本，几个重大bug修护
*  JsonMessage 无参构造方法
---------------------------------------------------------------------

## 1.2.3
*  【common-boot】 拦截器包不扫描抽象类
*  添加SpringBoot 配置文档  让配置属性不再困难

---------------------------------------------------------------------

## 1.2.2
*  【common-boot】 新增[request.log]配置是否记录请求日志信息和响应超时信息
*  【common-boot】 新增[request.parameterXss]配置是否强制过滤xss标签问题
*  【common-boot】 全局过滤器修护ISO-8859-1 转UTF-8 问题
*  【common-boot】 新增扫描拦截器包一下的 HandlerMethodArgumentResolver 实现类，来添加参数解析器

---------------------------------------------------------------------

## 1.2.1
*  【common-boot】 拦截注解添加排序属性  排序值越小越先加载
*  【common-redis】 去掉读取配置文件，采用动态读取属性 
*  【common-boot】 删除simple-util 依赖
*  【common-boot】 xss自动支持文件表单
*  【去除】AbstractMultipartFileBaseControl 类 合并到 AbstractBaseControl

---------------------------------------------------------------------

## 1.2.0
*  去掉默认依赖SpringBoot parent  只需要配置  spring-boot.version 来指定版本 
*  引入hutool 工具集
*  去掉原PagekageUtil

---------------------------------------------------------------------

## 1.1.23
*  【common-boot】            添加EnableCommonBoot注解来注入程序

---------------------------------------------------------------------