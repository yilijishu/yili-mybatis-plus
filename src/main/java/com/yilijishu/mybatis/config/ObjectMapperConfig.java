package com.yilijishu.mybatis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yilijishu.utils.jackson.ObjectMapperConfUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {


    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectMapperConfUtils.config(objectMapper);
        return objectMapper;
    }
}
