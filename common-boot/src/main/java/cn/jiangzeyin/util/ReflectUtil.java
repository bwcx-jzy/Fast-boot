package cn.jiangzeyin.util;


import java.lang.reflect.Field;

/**
 * 利用反射进行操作的一个工具类
 *
 * @author jiangzeyin
 */
public final class ReflectUtil {

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
}