package cn.jiangzeyin.util.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射缓存工具类
 *
 * @author jiangzeyin
 */
public final class ReflectCache {
    final static ConcurrentHashMap<String, Field> fieldMap = new ConcurrentHashMap<>();
    final static ConcurrentHashMap<String, Field[]> fieldsMap = new ConcurrentHashMap<>();
    final static ConcurrentHashMap<String, Class<?>> classMap = new ConcurrentHashMap<>();
    final static ConcurrentHashMap<String, Method> methodMap = new ConcurrentHashMap<>();
    final static ConcurrentHashMap<String, Method[]> methodsMap = new ConcurrentHashMap<>();

    /**
     * 加载class
     *
     * @param className className
     * @return class
     * @throws ClassNotFoundException notFound
     * @author jiangzeyin
     */
    public static Class<?> forName(String className) throws ClassNotFoundException {
        Class<?> cls = classMap.get(className);
        if (cls == null) {
            cls = Class.forName(className);
            classMap.put(className, cls);
        }
        return cls;
    }

    /**
     * 获取class 所有字段
     *
     * @param cls cls
     * @return field
     * @author jiangzeyin
     */
    public static Field[] getDeclaredFields(Class<?> cls) {
        Field[] fields = fieldsMap.get(cls.getName());
        if (fields == null) {
            fields = cls.getDeclaredFields();
            fieldsMap.put(cls.getName(), fields);
        }
        return fields;
    }

    /**
     * 获取class 字段
     *
     * @param cls  cls
     * @param name name
     * @return field
     * @throws NoSuchFieldException y
     * @throws SecurityException    y
     * @author jiangzeyin
     */
    public static Field getDeclaredField(Class<?> cls, String name) throws NoSuchFieldException, SecurityException {
        Field field = fieldMap.get(cls.getName() + "." + name);
        if (field == null) {
            field = cls.getDeclaredField(name);
            field.setAccessible(true);
            fieldMap.put(cls.getName() + "." + name, field);
        }
        return field;
    }

    /**
     * 获取class 方法
     *
     * @param cls            cls
     * @param name           name
     * @param parameterTypes types
     * @return method
     * @throws NoSuchMethodException y
     * @throws SecurityException     y
     * @author jiangzeyin
     */
    public static Method getDeclaredMethod(Class<?> cls, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        String mapname = cls.getName() + "." + name + "." + ParameterTypestoString(parameterTypes);
        Method method = methodMap.get(name);
        if (method == null) {
            method = cls.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            methodMap.put(mapname, method);
        }
        return method;
    }

    /**
     * 获取方法
     *
     * @param cls cls
     * @return method[]
     * @throws NoSuchMethodException y
     * @throws SecurityException     y
     * @author jiangzeyin
     */
    public static Method[] getDeclaredMethods(Class<?> cls) throws NoSuchMethodException, SecurityException {
        Method[] method = methodsMap.get(cls.getName());
        if (method == null) {
            method = cls.getDeclaredMethods();
            methodsMap.put(cls.getName(), method);
        }
        return method;
    }

    /**
     * 将参数转字符串名称
     *
     * @param parameterTypes types
     * @return string
     * @author jiangzeyin
     */
    private static String ParameterTypestoString(Class<?>... parameterTypes) {
        if (parameterTypes == null)
            return "";
        String name = "";
        for (Class<?> class1 : parameterTypes) {
            name = class1.getName() + ",";
        }
        return name;
    }
}
