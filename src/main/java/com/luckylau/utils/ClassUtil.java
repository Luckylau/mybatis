package com.luckylau.utils;

import com.alibaba.fastjson.JSON;
import com.luckylau.metadata.DbColumn;
import com.luckylau.metadata.DbTransient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


/**
 * Created by luckylau on 2018/4/20 下午1:25
 */
@Slf4j
public class ClassUtil {

    private static Map<Class<?>, List<Field>> fieldMap = new WeakHashMap<>();
    private static Map<Class<?>, Map<String, Method>> methodMap = new WeakHashMap<>();
    private static Map<Class<?>, Map<String, String>> dbFieldNameMap = new WeakHashMap<>();

    public static <T> List<Class<T>> getClassBySupperClass(Class<T> superClass, String moduleName) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        List<Class<T>> classList = new ArrayList<Class<T>>();
        try {
            Resource[] rs = resolver.getResources("classpath*:com/luckylau/**/" + moduleName + "/**/*.class");
            for (Resource resource : rs) {
                Class<T> clazz = (Class<T>) Class.forName(metadataReaderFactory.getMetadataReader(resource).getClassMetadata().getClassName());
                if (superClass.isAssignableFrom(clazz) && clazz != superClass) {
                    classList.add(clazz);
                }
            }
        } catch (Exception e) {
            CommonUtil.throwAs(e);
        }
        return classList;
    }

    public static Map<String, String> getDbFieldNames(Class<?> clazz) {
        if (clazz == null) {
            return Collections.EMPTY_MAP;
        }
        Map<String, String> map = dbFieldNameMap.get(clazz);
        if (map == null) {
            synchronized (dbFieldNameMap) {
                map = dbFieldNameMap.get(clazz);
                if (map == null) {
                    map = new HashMap<>(64);
                    for (Field field : getRecursiveFields(clazz)) {
                        if (!field.isAnnotationPresent(DbTransient.class) && !isCollectionType(field.getType())) {
                            map.put(field.getName(), Optional.ofNullable(field.getAnnotation(DbColumn.class)).map(DbColumn::value).orElse(field.getName()));
                        }
                    }
                    dbFieldNameMap.put(clazz, map);
                }
            }
        }
        if (clazz.getName().contains("OuterStat")) {
            log.info("OuterStat columns: " + JSON.toJSONString(map));
        }
        return map;
    }

    public static List<Field> getRecursiveFields(Class<?> clazz) {
        if (clazz == null) {
            return Collections.EMPTY_LIST;
        }
        List<Field> list = fieldMap.get(clazz);
        if (list == null) {
            synchronized (fieldMap) {
                list = fieldMap.get(clazz);
                if (list == null) {
                    list = new ArrayList<>();
                    do {
                        for (Field f : clazz.getDeclaredFields()) {
                            list.add(f);
                        }
                    } while (clazz != null && (clazz = clazz.getSuperclass()) != Object.class);
                    fieldMap.put(clazz, list);
                }
            }
        }
        return list;
    }

    public static boolean isCollectionType(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz);
    }

    public static List<Field> getDbFields(Class<?> clazz) {
        if (clazz == null) {
            return Collections.EMPTY_LIST;
        }
        List<Field> fields = new ArrayList<>();
        for (Field field : getRecursiveFields(clazz)) {
            if (!field.isAnnotationPresent(DbTransient.class) && !isCollectionType(field.getType())) {
                fields.add(field);
            }
        }
        return fields;
    }

    public static <T> T castType(Object value, Class<T> clazz) {
        if (value == null || clazz == null) {
            return null;
        }
        if (String.class == clazz) {
            return (T) value.toString();
        }
        return (T) methodInvoke(null, getMethod(clazz, "valueOf", String.class), value);
    }

    public static Object methodInvoke(Object target, Method method, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception e) {
            CommonUtil.throwAs(e);
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... types) {
        if (clazz == null || StringUtils.isEmpty(methodName)) {
            return null;
        }
        String methodKey = methodKey(methodName, types);
        Map<String, Method> map = methodMap.get(clazz);
        try {
            if (map == null) {
                synchronized (methodMap) {
                    map = methodMap.get(clazz);
                    if (map == null) {
                        methodMap.put(clazz, map = new HashMap<>());
                    }
                }
            }
            if (map.get(methodKey) == null) {
                synchronized (map) {
                    if (map.get(methodKey) == null) {
                        map.put(methodKey, clazz.getMethod(methodName, types));
                    }
                }
            }
        } catch (Exception e) {
        }
        return map.get(methodKey);
    }

    private static String methodKey(String name, Class<?>... types) {
        StringBuilder sb = new StringBuilder(name);
        sb.append(":");
        for (Class<?> type : types) {
            sb.append(type).append("_");
        }
        if (types.length > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static <T> Class<T> getGenericsClass(Class<?> clazz) {
        Type superType = clazz.getGenericSuperclass();
        if (superType == null || !ParameterizedType.class.isAssignableFrom(superType.getClass())) {
            return (Class<T>) clazz;
        }
        return recursionGenerics((ParameterizedType) superType);
    }

    private static <T> Class<T> recursionGenerics(ParameterizedType type) {
        Type returnType = type.getActualTypeArguments()[0];
        if (!ParameterizedType.class.isAssignableFrom(returnType.getClass())) {
            return (Class<T>) returnType;
        }
        return recursionGenerics((ParameterizedType) returnType);
    }


}
