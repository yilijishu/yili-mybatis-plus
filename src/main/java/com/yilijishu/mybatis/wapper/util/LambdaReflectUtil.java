package com.yilijishu.mybatis.wapper.util;

import com.yilijishu.mybatis.ann.Column;
import com.yilijishu.mybatis.wapper.fun.SFunction;
import com.yilijishu.utils.CamelUnderUtil;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LambdaReflectUtil {

    // 业务元数据缓存：key=类#方法名#方法签名
    private static final int MAX_CACHE_SIZE = 1000;
    private static final Map<String, String> FIELD_META_CACHE = new ConcurrentHashMap<>();

    // 新增：缓存Lambda实现类 -> writeReplace方法 + 权限状态，规避重复反射查找
    private static final Map<Class<?>, Method> WRITE_REPLACE_METHOD_CACHE = new ConcurrentHashMap<>();

    private LambdaReflectUtil() {
        throw new AssertionError("禁止实例化工具类");
    }

    /**
     * 优化：缓存writeReplace方法，避免每次反射查找
     */
    private static <T, R> SerializedLambda resolveLambda(SFunction<T, R> func) throws Exception {
        if (func == null) {
            throw new IllegalArgumentException("Lambda方法引用不能为空");
        }
        Class<?> lambdaCls = func.getClass();
        // 从缓存获取反射方法，不用每次getDeclaredMethod
        Method writeReplace = WRITE_REPLACE_METHOD_CACHE.computeIfAbsent(lambdaCls, cls -> {
            try {
                Method method = cls.getDeclaredMethod("writeReplace");
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("当前Lambda未实现序列化，无法解析，请使用可序列化函数式接口", e);
            }
        });
        return (SerializedLambda) writeReplace.invoke(func);
    }

    public static <T, R> String getFieldMeta(SFunction<T, R> func) {
        SerializedLambda lambda;
        try {
            lambda = resolveLambda(func);
        } catch (Exception e) {
            throw new RuntimeException("解析Lambda表达式失败", e);
        }

        String implClass = lambda.getImplClass().replace("/", ".");
        String methodName = lambda.getImplMethodName();
        String signature = lambda.getImplMethodSignature();
        String cacheKey = implClass + "#" + methodName + "#" + signature;

        // 命中缓存直接返回，不再执行字段、注解反射
        return FIELD_META_CACHE.computeIfAbsent(cacheKey, k -> {
            try {
                Class<?> clazz = Class.forName(implClass);
                String fieldName = parseFieldName(methodName);
                Field field = clazz.getDeclaredField(fieldName);
                Column prop = field.getAnnotation(Column.class);
                String propValue = prop != null ? prop.value() : CamelUnderUtil.underName(fieldName);
                return propValue;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("实体类不存在:" + implClass, e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Getter[" + methodName + "]未匹配到字段", e);
            }
        });
    }

    private static String parseFieldName(String getterMethodName) {
        if (getterMethodName.startsWith("get") && getterMethodName.length() > 3) {
            return Introspector.decapitalize(getterMethodName.substring(3));
        }
        if (getterMethodName.startsWith("is") && getterMethodName.length() > 2) {
            return Introspector.decapitalize(getterMethodName.substring(2));
        }
        throw new IllegalArgumentException("仅支持get/is开头标准Getter方法：" + getterMethodName);
    }

    // 便捷工具方法
    public static <T, R> String getColumnName(SFunction<T, R> func) {
        return getFieldMeta(func);
    }

    /**
     * 清空所有缓存
     */
    public static void clearCache() {
        FIELD_META_CACHE.clear();
        WRITE_REPLACE_METHOD_CACHE.clear();
    }

    public static int getFieldMetaCacheSize() {
        return FIELD_META_CACHE.size();
    }

    public static int getWriteReplaceMethodCacheSize() {
        return WRITE_REPLACE_METHOD_CACHE.size();
    }


}
