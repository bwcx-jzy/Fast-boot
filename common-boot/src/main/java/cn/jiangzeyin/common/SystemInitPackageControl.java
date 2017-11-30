package cn.jiangzeyin.common;

import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.common.spring.SpringUtil;
import cn.jiangzeyin.util.PackageUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by jiangzeyin on 2017/10/24.
 */
public class SystemInitPackageControl {

    /**
     * 系统预加载包名
     */
    public static void init() {
        String pageName = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.PRELOAD_PACKAGE_NAME);
        if (StringUtil.isEmpty(pageName))
            return;
        try {
            List<String> list = PackageUtil.getClassName(pageName);
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
        Method[] methods = classT.getMethods();
        HashMap<Method, Integer> sortMap = new HashMap<>();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
                PreLoadMethod preLoadMethod = method.getAnnotation(PreLoadMethod.class);
                if (preLoadMethod == null)
                    continue;
                sortMap.put(method, preLoadMethod.value());
            }
        }
        if (sortMap.size() > 0) {
            List<Map.Entry<Method, Integer>> newList = new ArrayList<>(sortMap.entrySet());
            newList.sort(Comparator.comparing(Map.Entry::getValue));
            for (Map.Entry<Method, Integer> item : newList) {
                Method method = item.getKey();
                try {
                    method.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    DefaultSystemLog.ERROR().error("预加载包错误:" + classT + "  " + method.getName() + "  执行错误", e);
                }
            }
        }
    }
}
