package cn.jiangzeyin.util;


import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * 利用反射进行操作的一个工具类
 *
 * @author jiangzeyin
 */
public final class ReflectUtil {
    /**
     * 利用反射获取指定对象的指定属性
     *
     * @param obj       目标对象
     * @param fieldName 目标属性
     * @return 目标属性的值
     * @throws IllegalAccessException   y
     * @throws IllegalArgumentException y
     */
    public static Object getFieldValue(Object obj, String fieldName) throws IllegalArgumentException, IllegalAccessException {
        Assert.notNull(obj);
        Object result = null;
        Field field = ReflectUtil.getField(obj.getClass(), fieldName);
        if (field != null) {
            field.setAccessible(true);
            result = field.get(obj);
        }
        return result;
    }

    /**
     * @param class1 class
     * @param class2 class
     * @return boolean
     */
    public static boolean isSuperclass(Class class1, Class class2) {
        for (Class<?> calzz = class1; calzz != Object.class; calzz = calzz.getSuperclass()) {
            if (calzz == class2)
                return true;
        }
        return false;
    }

    public static Annotation getFieldAnnotation(Class<?> cls, String name, Class<? extends Annotation> annotationClass) throws IllegalArgumentException, IllegalAccessException {
        Field field = getField(cls, name);
        if (field == null)
            return null;
        return field.getAnnotation(annotationClass);
    }

    public static Annotation getFieldAnnotation(Class<?> cls, Class<? extends Annotation> annotationClass) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = ReflectCache.getDeclaredFields(cls);
        if (fields == null)
            return null;
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(annotationClass);
            if (annotation == null)
                continue;
            return annotation;
        }
        return null;
    }

    public static List<String> getAnnotationFieldNames(Class<?> cls, Class<? extends Annotation> annotationClass) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = ReflectCache.getDeclaredFields(cls);
        if (fields == null)
            return null;
        List<String> fields2 = new ArrayList<>();
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(annotationClass);
            if (annotation == null)
                continue;
            fields2.add(field.getName());
        }
        return fields2;
    }

    /**
     * 调用 get方法
     *
     * @param obj       obj
     * @param fieldName fieldName
     * @return obj
     * @throws InvocationTargetException y
     * @throws IllegalArgumentException  y
     * @throws IllegalAccessException    y
     * @author jiangzeyin
     */
    public static Object getMethodValue(Object obj, String fieldName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // 自动 驼峰命名
        fieldName = StringUtil.captureName(fieldName);
        Method method = getMethod(obj.getClass(), "get" + fieldName);// obj.getClass().getMethod();
        if (method == null)
            throw new IllegalArgumentException(String.format("没有找到%s 对应get 方法", fieldName));
        return method.invoke(obj);
    }

    /**
     * 利用反射获取指定对象里面的指定属性
     *
     * @param cls       目标对象
     * @param fieldName 目标属性
     * @return 目标字段
     */
    public static Field getField(Class<?> cls, String fieldName) {
        Field field = null;
        for (Class<?> clazz = cls; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = ReflectCache.getDeclaredField(clazz, fieldName);// clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                // 这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
                continue;
            }
        }
        return field;
    }

    /**
     * 获取方法 包含 父类
     *
     * @param cls            cls
     * @param methodName     methodName
     * @param parameterTypes type
     * @return method
     * @author jiangzeyin
     */
    public static Method getMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        for (Class<?> clazz = cls; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = ReflectCache.getDeclaredMethod(clazz, methodName, parameterTypes);
                break;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                continue;
            }
        }
        return method;
    }

    /**
     * 利用反射设置指定对象的指定属性为指定的值
     *
     * @param obj        目标对象
     * @param fieldName  目标属性
     * @param fieldValue 目标值
     * @throws IllegalAccessException ill
     */
    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) throws IllegalAccessException {
        Field field = ReflectUtil.getField(obj.getClass(), fieldName);
        if (field != null) {
            field.setAccessible(true);
            Class type = field.getType();
            String typeName = type.getSimpleName();
            if (typeName.equalsIgnoreCase("int")) {
                field.set(obj, StringUtil.parseInt(fieldValue.toString()));
            } else if (typeName.equalsIgnoreCase("double")) {
                field.set(obj, StringUtil.parseDouble(fieldValue.toString()));
            } else if (typeName.equalsIgnoreCase("string")) {
                if (fieldValue == null)
                    field.set(obj, "");
                else
                    field.set(obj, fieldValue.toString());
            } else if (typeName.equalsIgnoreCase("long")) {
                field.set(obj, StringUtil.parseLong(fieldValue.toString()));
            } else if (type == Integer.class) {
                field.set(obj, Integer.valueOf(fieldValue.toString()));
            } else {
                field.set(obj, fieldValue);
            }
        }
    }


    /**
     * 自定义排序
     *
     * @param list           list
     * @param orderField     order
     * @param orderDirection desc
     */
    public static void sortString(List<?> list, String orderField, String orderDirection) {
        if (!"desc".equals(orderDirection) && !"asc".equals(orderDirection))
            return;
        final String methodName = "get" + orderField;
        final String type = orderDirection;
        Collections.sort(list, (Comparator<Object>) (a, b) -> {
            int ret = 0;
            Method m = null;
            Class<?> a1 = a.getClass();
            for (; ; ) {
                try {
                    m = a1.getDeclaredMethod(methodName);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    a1 = a1.getSuperclass();
                    if (a1 == null)
                        return ret;
                    continue;
                }
                break;
            }
            try {
                Integer aStr = (Integer) m.invoke(a);
                Integer bStr = (Integer) m.invoke(b);
                ret = aStr.compareTo(bStr);
                ret = "asc".equals(type) ? ret : -ret;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret;
        });
    }

    /**
     * 获取对象的泛型
     *
     * @param obj obj
     * @return class
     * @author jiangzeyin
     */
    public static Class<?> getTClass(Object obj) {
        Assert.notNull(obj, "obj 不能为空");
        return getTClass(obj.getClass());
    }

    public static Class<?> getTClass(Class<?> cls) {
        Type type = cls.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getActualTypeArguments()[0];
            return (Class<?>) type;
        }
        return null;
    }

    /**
     * list 转map
     *
     * @param key_ key
     * @param list list
     * @param key  key
     * @param <T>  t
     * @param <V>  v
     * @return r
     * @throws IllegalAccessException   y
     * @throws IllegalArgumentException y
     * @author jiangzeyin
     */
    @SuppressWarnings("unchecked")
    public static <V, T> HashMap<T, V> mapToList(List<V> list, Class<T> key_, String key) throws IllegalArgumentException, IllegalAccessException {
        if (list == null)
            return null;
        HashMap<T, V> map = new HashMap<>();
        if (list.size() < 1)
            return map;
        for (V v : list) {
            map.put((T) getFieldValue(v, key), v);
        }
        return map;
    }

    public static List<Method> getAllGetMethods(Class cls) {
        return getAllMethods(cls, "get");
    }

    public static List<Method> getAllSetMethods(Class cls) {
        return getAllMethods(cls, "set");
    }

    private static List<Method> getAllMethods(Class cls, String prefix) {
        Assert.notNull(cls);
        List<Method> list = new ArrayList<>();
        for (Class<?> clazz = cls; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith(prefix)) {
                    list.add(method);
                }
            }
        }
        return list;
    }
}