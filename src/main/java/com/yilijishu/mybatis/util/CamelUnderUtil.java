package com.yilijishu.mybatis.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符下划线转换
 */
public class CamelUnderUtil {
    /**
     * 将驼峰式命名的字符串转换为下划线大写方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。
     * 例如：HelloWorld -- HELLO_WORLD
     *
     * @param name 转换前的驼峰式命名的字符串
     * @return 转换后下划线大写方式命名的字符串
     */
    public static String underName(String name) {
        return concatSymbolName(name, "_");
    }

    /**
     * 字符串连接symbol字符
     * @param name  字符串
     * @param symbol  符号
     * @return 返回拼装好的字符串
     */
    public static String concatSymbolName(String name, String symbol) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            // 将第一个字符处理成大写
            result.append(name.substring(0, 1).toLowerCase());
            // 循环处理其余字符
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                // 在大写字母前添加下划线
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append(symbol);
                }
                // 其他字符直接转成大写
                result.append(s.toLowerCase());
            }
        }
        return result.toString();
    }


    /**
     * 下划线转驼峰结构  首字母不大写
     * 用来把下划线字符串转驼峰结构。
     * @param name 下划线名字
     * @return 返回驼峰结构字符串
     */
    public static String camelName(String name) {
        return camelName(name, false);
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。
     * 例如：HELLO_WORLD - HelloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @param firstUpper 首字母是否大写
     * @return 转换后的驼峰式命名的字符串
     */
    public static String camelName(String name, Boolean firstUpper) {
        return camelName(name, "_", firstUpper);
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。
     * 例如：HELLO_WORLD - HelloWorld
     * @param name 转换前的下划线大写方式命名的字符串
     * @param splitSymbol 连接符号
     * @param firstUpper 首字母是否大写
     * @return 转换后的驼峰式命名的字符串
     */
    public static String camelName(String name, String splitSymbol, Boolean firstUpper) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!name.contains(splitSymbol)) {
            // 不含下划线，仅将首字母小写
            return firstUpper ? name.substring(0, 1).toUpperCase() + name.substring(1) : name.substring(0, 1).toLowerCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = name.split(splitSymbol);
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 处理真正的驼峰片段
            if (result.length() == 0) {
                // 第一个驼峰片段，全部字母都小写
                result.append(firstUpper ? camel.substring(0, 1).toUpperCase() + camel.substring(1) : camel.toLowerCase());
            } else {
                // 其他的驼峰片段，首字母大写
                result.append(camel.substring(0, 1).toUpperCase());
                result.append(camel.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
    /**
     * 首字母大写
     * @param name 名字
     * @return 返回首字母大写
     */
    public static String nameUpper(String name) {
        if(StringUtils.isNotBlank(name)) {
            name = name.substring(0,1).toUpperCase() + name.substring(1);
        }
        return name;
    }

}
