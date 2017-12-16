# common-boot  

> 针对SpringBoot封装的一个 common boot

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
   

    @PreLoadClass
    public class test {
        // 值越小越先加载
        @PreLoadMethod(1)
        public static void load1() {
            System.out.println("load1");
        }
    
        @PreLoadMethod(2)
        public static void load2() {
            System.out.println("load2");
        }

 **key-value 内存缓存** 
 
   cn.jiangzeyin.cache.ObjectCache 类主要负责缓存操作
   
   ObjectCache.config(Class cls) 配置缓存的默认属性 cls 可以配置一个cn.jiangzeyin.cache.CacheConfig 注解  cls 中配置缓存的key 的静态不可更改的字符串属性  属性者可以配置 cn.jiangzeyin.cache.CacheConfigField 注解
   实例：

    @CacheConfig(value = 5, UNIT = TimeUnit.MINUTES)
    public class cacheConfig {
        // sss缓存 根据class 的配置读取
        public static final String SSS = "sss";
        // ttt 缓存1 天
        @CacheConfigField(value = 1, UNIT = TimeUnit.DAYS)
        public static final String TTT = "ttt";
    }
  如果没有配置 默认缓存时间为10分钟
  
 **线程池基本服务** 
 cn.jiangzeyin.pool.ThreadPoolService 注意负责线程池的创建和统一管理
 newCachedThreadPool(Class class1) 创建线程池 参数为线程池需要负责类 如需要配置线程池相关参数 则需要对class 增加注解 cn.jiangzeyin.pool.PoolConfig

    @Documented
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PoolConfig {
        // 线程池核心数
        int value() default 0;
    
        // 线程池最大线程数
        int maximumPoolSize() default Integer.MAX_VALUE;
    
        // 线程空闲多久将销毁
        long keepAliveTime() default 60L;
    
        // 时间单位
        TimeUnit UNIT() default TimeUnit.SECONDS;
    
        // 线程池拒绝执行处理策略
        PolicyHandler HANDLER() default PolicyHandler.Caller;
    }

getPoolQueuedTasks(Class tClass)  获取线程池队列数

getPoolRejectedExecutionCount(Class tclass) 获取线程池取消执行的任务数

shutdown() 关闭所有线程池


> 针对SpringBoot封装的一个 common redis

简单调用redis 缓存 基于SpringBoot 中redis 管理