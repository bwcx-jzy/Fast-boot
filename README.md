# common-boot  
SpringBoot common boot

 **公共的Controller** 
使用ThreadLocal 记录当前请求的request session respone
  cn.jiangzeyin.controller.base.AbstractBaseControl
在新建Controller 时请继承此类
  cn.jiangzeyin.controller.base.AbstractMultipartFileBaseControl
文件上传Controller 在需要接收上传文件请继承此类

 **自动加载拦截器** 
   程序会对指定包下就行拦截器扫描创建 请配置
   interceptor.initPackageName 属性

   所有拦截器请继承  cn.jiangzeyin.common.interceptor.BaseInterceptor  该类主要实现公共Controller 属性自动解析和记录请求错误信息

 **SpringUtil 操作集成** 
   cn.jiangzeyin.common.spring.SpringUtil 主要对Spring容器简单管理  getBean()  getEnvironment() 
   
 同时提供SpringBoot 程序监听接口  cn.jiangzeyin.common.spring.ApplicationEventClient  在创建SpringBoot Application类时，请继承
cn.jiangzeyin.common.BaseApplication  该类主要实现对该程序进行初始化和接口注入

 **启动自动加载资源接口** 
  程序会在Spring 容器启动回调接口中自动初始化指定包下的所有类的指定方法，并可以支持加载排序

   请配置 preload.packageName 属性  在需要的类上增加 cn.jiangzeyin.common.PreLoadClass 注解 对需要的方法增加 cn.jiangzeyin.common.PreLoadMethod 注解  注意：方法必须为public static

 **key-value 内存缓存** 

 **线程池基本服务** 