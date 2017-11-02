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
     * //     * 系统预加载包名
     * //
     */
//    @Value("${server.initPackageName:com.yoke.system.init}")
//    public String initPackageName;
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
            List<Map.Entry<Integer, Class>> newList = splitClass(classList);
            if (newList != null)
                for (Map.Entry<Integer, Class> item : newList)
                    loadClass(item.getValue());
        } catch (IOException e) {
            DefaultSystemLog.ERROR().error("预加载包错误", e);
        }
    }

    // 排序class
    private static List<Map.Entry<Integer, Class>> splitClass(List<Class<?>> list) {
        HashMap<Integer, Class> sortMap = new HashMap<>();
        for (Class item : list) {
            PreLoadClass preLoadClass = (PreLoadClass) item.getAnnotation(PreLoadClass.class);
            if (preLoadClass == null)
                continue;
            sortMap.put(preLoadClass.value(), item);
        }
        List<Map.Entry<Integer, Class>> newList = null;
        if (sortMap.size() > 0) {
            newList = new ArrayList<>(sortMap.entrySet());
            newList.sort(Comparator.comparing(Map.Entry::getKey));
        }
        return newList;
    }

    // 排序class 中方法
    private static void loadClass(Class classT) {
        Method[] methods = classT.getMethods();
        HashMap<Integer, Method> sortMap = new HashMap<>();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
                PreLoadMethod preLoadMethod = method.getAnnotation(PreLoadMethod.class);
                if (preLoadMethod == null)
                    continue;
                sortMap.put(preLoadMethod.value(), method);
            }
        }
        if (sortMap.size() > 0) {
            List<Map.Entry<Integer, Method>> newList = new ArrayList<>(sortMap.entrySet());
            newList.sort(Comparator.comparing(Map.Entry::getKey));
            for (Map.Entry<Integer, Method> item : newList) {
                Method method = item.getValue();
                try {
                    method.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    DefaultSystemLog.ERROR().error("预加载包错误:" + classT + "  " + method.getName() + "  执行错误", e);
                }
            }
        }
    }

}
