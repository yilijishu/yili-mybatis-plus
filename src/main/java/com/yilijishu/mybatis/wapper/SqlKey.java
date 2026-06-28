package com.yilijishu.mybatis.wapper;

public enum SqlKey {

    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    IN("IN"),
    NOT_IN("NOT IN"),
    LIKE("LIKE"),
    LEFTLIKE ("LIKE"),
    RIGHTLIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    EQ("="),
    NE("&lt;&gt;"),
    GT("&gt;"),
    GE("&gt;="),
    LT("&lt;"),
    LE("&lt;="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    ORDER_BY("ORDER BY"),
    EXISTS("EXISTS"),
    NOT_EXISTS("NOT EXISTS"),
    BETWEEN("BETWEEN"),
    NOT_BETWEEN("NOT BETWEEN"),
    ASC("ASC"),
    DESC("DESC");

    private final String keyword;

    public String getSqlSegment() {
        return this.keyword;
    }
    private SqlKey(final String keyword) {
        this.keyword = keyword;
    }
}
