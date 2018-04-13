package cn.jiangzeyin.common;

import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.common.spring.SpringUtil;
import cn.jiangzeyin.util.PackageUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by jiangzeyin on 2017/10/24.
 */
public class CommonInitPackage {
    private volatile static boolean init = false;
    private static final List<Method> METHOD_LIST = new ArrayList<>();
    private static final List<String> PACKAGE_NAME_LIST = new ArrayList<>();

    /**
     * 系统预加载包名
     */
    public static void init() {
        if (init) {
            DefaultSystemLog.LOG().info("init 包已经被初始化过啦！");
            return;
        }
        String pageName = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.PRELOAD_PACKAGE_NAME);
        if (StringUtil.isEmpty(pageName))
            return;
        load(pageName);
        init = true;
    }

    /**
     * 初始化包
     *
     * @param packageName packageName
     */
    public static void load(String packageName) {
        if (PACKAGE_NAME_LIST.contains(packageName)) {
            DefaultSystemLog.LOG().info(packageName + " 包已经被初始化过啦！");
            return;
        }
        try {
            List<String> list = PackageUtil.getClassName(packageName);
            if (list == null || list.size() <= 0)
                return;
            List<Class<?>> classList = new ArrayList<>();
            for (String name : list) {
                try {
                    Class<?> cls = Class.forName(name);
                    classList.add(cls);
                } catch (ClassNotFoundException e) {
                    DefaultSystemLog.ERROR().error("预加载包错误:" + name, e);
                }
            }
            if (classList.size() <= 0)
                return;
            List<Map.Entry<Class, Integer>> newList = splitClass(classList);
            if (newList != null)
                for (Map.Entry<Class, Integer> item : newList)
                    loadClass(item.getKey());
            PACKAGE_NAME_LIST.add(packageName);
        } catch (IOException e) {
            DefaultSystemLog.ERROR().error("预加载包错误", e);
        }
    }

    // 排序class
    private static List<Map.Entry<Class, Integer>> splitClass(List<Class<?>> list) {
        HashMap<Class, Integer> sortMap = new HashMap<>();
        for (Class item : list) {
            PreLoadClass preLoadClass = (PreLoadClass) item.getAnnotation(PreLoadClass.class);
            if (preLoadClass == null)
                continue;
            sortMap.put(item, preLoadClass.value());
        }
        List<Map.Entry<Class, Integer>> newList = null;
        if (sortMap.size() > 0) {
            newList = new ArrayList<>(sortMap.entrySet());
            newList.sort(Comparator.comparing(Map.Entry::getValue));
        }
        return newList;
    }

    // 排序class 中方法
    private static void loadClass(Class classT) {
        Method[] methods = classT.getDeclaredMethods();
        HashMap<Method, Integer> sortMap = new HashMap<>();
        for (Method method : methods) {
            PreLoadMethod preLoadMethod = method.getAnnotation(PreLoadMethod.class);
            if (preLoadMethod == null)
                continue;
            Type type = method.getGenericReturnType();
            int modifiers = method.getModifiers();
            Type[] parameters = method.getParameterTypes();
            if ((parameters == null || parameters.length <= 0) && Void.TYPE.equals(type) && Modifier.isStatic(modifiers) && Modifier.isPrivate(modifiers)) {
                sortMap.put(method, preLoadMethod.value());
            } else
                throw new IllegalArgumentException(classT + "  " + method + "  " + PreLoadMethod.class + " must use empty parameters static void private");
        }
        if (sortMap.size() > 0) {
            List<Map.Entry<Method, Integer>> newList = new ArrayList<>(sortMap.entrySet());
            newList.sort(Comparator.comparing(Map.Entry::getValue));
            for (Map.Entry<Method, Integer> item : newList) {
                Method method = item.getKey();
                if (METHOD_LIST.contains(method)) {
                    DefaultSystemLog.LOG().info(classT + "  " + method.getName() + "已经调用过啦");
                    continue;
                }
                try {
                    method.setAccessible(true);
                    method.invoke(null);
                    METHOD_LIST.add(method);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    DefaultSystemLog.ERROR().error("预加载包错误:" + classT + "  " + method.getName() + "  执行错误", e);
                }
            }
        }
    }
}
