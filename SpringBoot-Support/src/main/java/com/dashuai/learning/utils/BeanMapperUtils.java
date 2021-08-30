package com.dashuai.learning.utils;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Bean mapper utils
 * <p/>
 * Created in 2018.09.11
 * <p/>
 * bean映射，相同的Bean映射工具还有Dozer
 *
 * @author Liaozihong
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanMapperUtils {
    private static MapperFactory mapperFactory;

    static {
        mapperFactory = new DefaultMapperFactory.Builder().build();
    }

    /**
     * 转换源对象中对应的属性值到新的Bean对象中（使用Orika的实现方式，性能更优）
     *
     * @param <T>        泛型
     * @param src        源对象
     * @param targetType 结果对象类型
     * @return 新的结果对象 t
     */
    public static <T> T mapperFast(Object src, Class<T> targetType) {
        if (null == src) {
            return null;
        }
        return mapperFactory.getMapperFacade().map(src, targetType);
    }

    /**
     * 提供一个实例容许自定义设置
     *
     * @return
     */
    public static MapperFactory getMapperFacade() {
        return mapperFactory;
    }
}
