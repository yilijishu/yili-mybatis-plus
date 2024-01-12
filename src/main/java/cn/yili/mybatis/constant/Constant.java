package cn.yili.mybatis.constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Constant {

    public static final class OrderByValue {
        public static final String DESC = "desc";
        public static final String ASC = "asc";
    }

    public static final String IFNULL_TEMPLATE = "      <if test=\"{CONDITION}\">" +
            "        {WHERE}" +
            "      </if>";
    public enum Oper {
        WHERE, UPDATE
    }
}
