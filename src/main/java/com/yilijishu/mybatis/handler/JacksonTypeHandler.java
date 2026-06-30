package com.yilijishu.mybatis.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yilijishu.utils.jackson.ObjectMapperConfUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;

@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JacksonTypeHandler extends AbstractJsonTypeHandler<Object> {

    private static ObjectMapper objectMapper = new ObjectMapper();
    private final Class<?> type;

    public JacksonTypeHandler(Class<?> type) {
        this.type = type;
        ObjectMapperConfUtils.config(JacksonTypeHandler.objectMapper);
    }

    @Override
    protected Object parse(String json) {
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        JacksonTypeHandler.objectMapper = objectMapper;
    }
}
