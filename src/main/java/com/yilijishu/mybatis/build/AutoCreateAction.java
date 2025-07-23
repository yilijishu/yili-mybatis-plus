package com.yilijishu.mybatis.build;


import com.yilijishu.mybatis.mapper.BaseMapper;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Constructor;
import java.util.Map;

public class AutoCreateAction {

    private final ApplicationContext context;

    private static boolean created = false;

    public AutoCreateAction(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    @SneakyThrows
    public void autoCreate() {
        Map<String, BaseMapper> beansOfType = context.getBeansOfType(BaseMapper.class);
        for (BaseMapper bean : beansOfType.values()) {
            ResolvableType resolvableType = ResolvableType.forInstance(bean).getSuperType();
            Class<?> genericType = resolvableType.getGeneric(0).resolve();
            Constructor<?> defaultConstructor = genericType.getDeclaredConstructor();
            Object instance = defaultConstructor.newInstance();
            bean.create(instance);
        }
    }

}
