package cn.jiangzeyin.common;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.spring.SpringUtil;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 控制初始化
 *
 * @author jiangzeyin
 * @date 2017/10/24
 */
@Configuration
@ComponentScan(value = "cn.jiangzeyin")
public class CommonInitPackage {
    private volatile static boolean init = false;
    private static final HashSet<Method> METHOD_LIST = new HashSet<>();
    private static final HashSet<String> PACKAGE_NAME_LIST = new HashSet<>();

    /**
     * 系统预加载包名
     */
    public static void init() {
        if (init && !ApplicationBuilder.isRestart()) {
            DefaultSystemLog.getLog().warn("系统init 包已经被初始化过啦！");
            return;
        }
        String pageName = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.PRELOAD_PACKAGE_NAME);
        if (StrUtil.isBlank(pageName)) {
            return;
        }
        String[] pages = StrUtil.splitToArray(pageName, StrUtil.COMMA);
        if (pages == null) {
            return;
        }
        for (String item : pages) {
            load(item);
        }
        init = true;
    }

    /**
     * 初始化包
     *
     * @param packageName packageName
     */
    public static void load(String packageName) {
        if (PACKAGE_NAME_LIST.contains(packageName) && !ApplicationBuilder.isRestart()) {
            DefaultSystemLog.getLog().warn(packageName + " 包已经被初始化过啦！");
            return;
        }
        //扫描
        Set<Class<?>> set = ClassUtil.scanPackageByAnnotation(packageName, PreLoadClass.class);
        if (set == null || set.size() <= 0) {
            return;
        }
        // 排序调用
        List<Map.Entry<Class<?>, Integer>> newList = splitClass(set);
        if (newList != null) {
            for (Map.Entry<Class<?>, Integer> item : newList) {
                loadClass(item.getKey());
            }
        }
        PACKAGE_NAME_LIST.add(packageName);
    }

    /**
     * // 排序class
     *
     * @param list list
     * @return 排序后的
     */
    private static List<Map.Entry<Class<?>, Integer>> splitClass(Set<Class<?>> list) {
        HashMap<Class<?>, Integer> sortMap = new HashMap<>(10);
        for (Class<?> item : list) {
            PreLoadClass preLoadClass = item.getAnnotation(PreLoadClass.class);
            sortMap.put(item, preLoadClass.value());
        }
        List<Map.Entry<Class<?>, Integer>> newList = null;
        if (sortMap.size() > 0) {
            newList = new ArrayList<>(sortMap.entrySet());
            newList.sort(Map.Entry.comparingByValue());
        }
        return newList;
    }

    /**
     * 排序class 中方法
     *
     * @param classT class
     */
    private static void loadClass(Class<?> classT) {
        // 注入到Spring 容器中
        SpringUtil.registerSingleton(classT);
        // 调用方法
        Method[] methods = classT.getDeclaredMethods();
        HashMap<Method, Integer> sortMap = new HashMap<>();
        for (Method method : methods) {
            PreLoadMethod preLoadMethod = method.getAnnotation(PreLoadMethod.class);
            if (preLoadMethod == null) {
                continue;
            }
            Type type = method.getGenericReturnType();
            int modifiers = method.getModifiers();
            Type[] parameters = method.getParameterTypes();
            if (parameters.length <= 0 && Void.TYPE.equals(type) && Modifier.isStatic(modifiers) && Modifier.isPrivate(modifiers)) {
                sortMap.put(method, preLoadMethod.value());
            } else {
                throw new IllegalArgumentException(classT + "  " + method + "  " + PreLoadMethod.class + " must use empty parameters static void private");
            }
        }
        if (sortMap.size() > 0) {
            List<Map.Entry<Method, Integer>> newList = new ArrayList<>(sortMap.entrySet());
            newList.sort(Map.Entry.comparingByValue());
            for (Map.Entry<Method, Integer> item : newList) {
                Method method = item.getKey();
                if (METHOD_LIST.contains(method) && !ApplicationBuilder.isRestart()) {
                    DefaultSystemLog.getLog().warn(classT + "  " + method.getName() + "已经调用过啦");
                    continue;
                }
                try {
                    method.setAccessible(true);
                    method.invoke(null);
                    METHOD_LIST.add(method);
                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    Throwable show = cause == null ? e : cause;
                    DefaultSystemLog.getLog().error("预加载包错误:" + classT + "  " + method.getName() + "  执行错误", show);
                }
            }
        }
    }
}
