package com.yilijishu.mybatis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yilijishu.mybatis.entity.Page;
import com.yilijishu.utils.CamelUnderUtil;
import com.yilijishu.utils.exceptions.BizException;
import com.yilijishu.utils.result.ApiResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class StandardWeb {

    @Autowired
    private BaseService baseService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * save : 保存
     * d-save : 批量保存
     * unn : 修改数据 不为空时候修改。修改个别属性时使用
     * u : 修改数据 全部修改。
     * d : 删除
     * dd-id: 批量删除 根据ID
     * dd-vid: 批量删除 根据虚拟ID
     * ddv-id : 批量虚拟删除
     * ddv-vid : 批量虚拟删除
     * q : 查询
     * g : 获取
     * qp : 分页查询
     */
    private static Map<String, String> OPERATE = new HashMap<>();

    static {
        OPERATE.put("save", "save");
        OPERATE.put("d-save", "d-save");
        OPERATE.put("unn", "unn");
        OPERATE.put("u", "u");
        OPERATE.put("d", "d");
        OPERATE.put("dd-id", "dd-id");
        OPERATE.put("dd-vid", "dd-vid");
        OPERATE.put("ddv-id", "ddv-id");
        OPERATE.put("ddv-vid", "ddv-vid");
        OPERATE.put("q", "q");
        OPERATE.put("g", "g");
        OPERATE.put("qp", "qp");
    }

    /**
     * "{"", "data":[{}]}"
     *
     * @param packageName 包名。com-yilijishu-entity
     * @param entityName  实例名。比如user-member
     * @param operate     操作
     * @param start       开始页数
     * @param size        长度
     * @param body        数据串
     * @return 返回ApiResult标准输出
     */
    @PostMapping("/{packageName}/{entityName}/{operate}")
    public ApiResult<?> standard(@PathVariable("packageName") String packageName,
                                 @PathVariable("entityName") String entityName,
                                 @PathVariable("operate") String operate,
                                 @RequestParam(value = "start", defaultValue = "1") int start,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 @RequestBody String body) {
        String op = OPERATE.get(operate);
        if (StringUtils.isNotBlank(op)) {
            String className = packageName.replaceAll("-", ".") + "." + CamelUnderUtil.camelName(entityName, "-", true);
            Class<?> clz = null;
            try {
                clz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new BizException("无效的标准路径");
            }
            switch (op) {
                case "save": {
                    Object obj = null;
                    try {
                        obj = objectMapper.readValue(body, clz);
                    } catch (JsonProcessingException e) {
                        throw new BizException("无效的数据格式");
                    }
                    return ApiResult.resultSuccess(baseService.insert(obj));
                }
                case "d-save": {
                    List<?> list = null;
                    try {
                        list = objectMapper.readValue(body, objectMapper.getTypeFactory().constructCollectionType(List.class, clz));
                    } catch (JsonProcessingException e) {
                        throw new BizException("无效的数据格式");
                    }
                    return ApiResult.resultSuccess(baseService.insertAll(list));
                }
                case "unn": {
                    Object obj = null;
                    try {
                        obj = objectMapper.readValue(body, clz);
                    } catch (JsonProcessingException e) {
                        throw new BizException("无效的数据格式");
                    }
                    return ApiResult.resultSuccess(baseService.updateIfNot(obj));
                }
                case "u": {
                    Object obj = null;
                    try {
                        obj = objectMapper.readValue(body, clz);
                    } catch (JsonProcessingException e) {
                        throw new BizException("无效的数据格式");
                    }
                    return ApiResult.resultSuccess(baseService.update(obj));
                }
                case "d": {
                    Object obj = null;
                    try {
                        obj = objectMapper.readValue(body, clz);
                    } catch (JsonProcessingException e) {
                        throw new BizException("无效的数据格式");
                    }
                    return ApiResult.resultSuccess(baseService.delete(obj));
                }
                case "dd-id": {
                    Object obj = null;
                    try {
                        obj = clz.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException e) {
                        throw new BizException("无效的实例");
                    } catch (IllegalAccessException e) {
                        throw new BizException("无效的实例");
                    } catch (InvocationTargetException e) {
                        throw new BizException("无效的实例");
                    } catch (NoSuchMethodException e) {
                        throw new BizException("无效的方法");
                    }
                    List<Object> list = null;
                    try {
                        list = objectMapper.readValue(body, objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));
                    } catch (JsonProcessingException e) {
                        throw new BizException("无效的数据格式");
                    }
                    return ApiResult.resultSuccess(baseService.deleteByIds(obj, list));
                }
                case "dd-vid": {
                    Object obj = null;
                    try {
                        obj = clz.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException e) {
                        throw new BizException("无效的实例");
                    } catch (IllegalAccessException e) {
                        throw new BizException("无效的实例");
                    } catch (InvocationTargetException e) {
                        throw new BizException("无效的实例");
                    } catch (NoSuchMethodException e) {
                        throw new BizException("无效的方法");
                    }
                    List<Object> list = null;
                    try {
                        list = objectMapper.readValue(body, objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));
                    } catch (JsonProcessingException e) {
                        throw new BizException("无效的数据格式");
                    }
                    return ApiResult.resultSuccess(baseService.deleteByVirtualIds(obj, list));
                }
                case "ddv-id": {
                    Object obj = null;
                    try {
                        obj = clz.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException e) {
                        throw new BizException("无效的实例");
                    } catch (IllegalAccessException e) {
                        throw new BizException("无效的实例");
                    } catch (InvocationTargetException e) {
                        throw new BizException("无效的实例");
                    } catch (NoSuchMethodException e) {
                        throw new BizException("无效的方法");
                    }
                    List<Object> list = null;
                    try {
                        list = objectMapper.readValue(body, objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));
                    } catch (JsonProcessingException e) {
                        throw new BizException("无效的数据格式");
                    }
                    return ApiResult.resultSuccess(baseService.virtualDeleteByIds(obj, list));
                }
                case "ddv-vid": {
                    Object obj = null;
                    try {
                        obj = clz.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException e) {
                        throw new BizException("无效的实例");
                    } catch (IllegalAccessException e) {
                        throw new BizException("无效的实例");
                    } catch (InvocationTargetException e) {
                        throw new BizException("无效的实例");
                    } catch (NoSuchMethodException e) {
                        throw new BizException("无效的方法");
                    }
                    List<Object> list = null;
                    try {
                        list = objectMapper.readValue(body, objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));
                    } catch (JsonProcessingException e) {
                        throw new BizException("无效的数据格式");
                    }
                    return ApiResult.resultSuccess(baseService.virtualDeleteByVirtualIds(obj, list));
                }
                case "q": {
                    Object obj = null;
                    try {
                        obj = objectMapper.readValue(body, clz);
                    } catch (JsonProcessingException e) {
                        throw new BizException("无效的数据格式");
                    }
                    return ApiResult.resultSuccess(baseService.select(obj));
                }
                case "g": {
                    Object obj = null;
                    try {
                        obj = objectMapper.readValue(body, clz);
                    } catch (JsonProcessingException e) {
                        throw new BizException("无效的数据格式");
                    }
                    return ApiResult.resultSuccess(baseService.get(obj));
                }
                case "qp": {
                    Object obj = null;
                    try {
                        obj = objectMapper.readValue(body, clz);
                    } catch (JsonProcessingException e) {
                        throw new BizException("无效的数据格式");
                    }
                    Page page = new Page(start, size);
                    List list = baseService.selectByPage(obj, page);
                    Map<String, Object> result = new HashMap<>();
                    result.put("page", page);
                    result.put("data", list);
                    return ApiResult.resultSuccess(result);
                }
                default: {
                    throw new BizException("无效的请求");
                }
            }
        }
        throw new BizException("请求无效");
    }
}
