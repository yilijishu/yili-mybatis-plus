package cn.yili.mybatis.wapper;


import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class QuerySql<Entity> {

    private StringBuilder select = null;

    private StringBuilder where = null;

    private Entity entity;

    public QuerySql(Entity entity) {
        this.entity = entity;
        this.select = new StringBuilder();
        this.where = new StringBuilder();
    }

    public QuerySql select(String... column) {
        if (column != null && column.length > 0) {
            select.append(String.join(", ", column));
        }
        return this;
    }

    public QuerySql in(String column, List ins) {
        if (StringUtils.isNotBlank(column) && ins != null && ins.size() > 0) {
            where.append(" and ");
            where.append(column);
            where.append(" in (");
            for (int i = 0; i < ins.size(); i++) {
                Object tmp = ins.get(i);
                if (tmp instanceof Integer) {
                    where.append(tmp);
                } else {
                    where.append("'" + tmp + "'");
                }
                if (i < ins.size() - 1) {
                    where.append(",");
                }
            }
        }
        return this;
    }

    public QuerySql isNotNull(String column) {
        if (StringUtils.isNotBlank(column)) {
            where.append(" and ");
            where.append(column);
            where.append(" is not null ");
        }
        return this;
    }

    public QuerySql isNull(String column) {
        if (StringUtils.isNotBlank(column)) {
            where.append(" and ");
            where.append(column);
            where.append(" is null ");
        }
        return this;
    }


    public QuerySql lt(String column, Object obj) {
        if (StringUtils.isNotBlank(column) && obj != null) {
            where.append(" and ");
            where.append(column);
            where.append(" < ");
            if (obj instanceof Integer) {
                where.append(obj);
            } else {
                where.append("'" + obj + "'");
            }
        }
        return this;
    }

    public QuerySql ltEq(String column, Object obj) {
        if (StringUtils.isNotBlank(column) && obj != null) {
            where.append(" and ");
            where.append(column);
            where.append(" <= ");
            if (obj instanceof Integer) {
                where.append(obj);
            } else {
                where.append("'" + obj + "'");
            }
        }
        return this;
    }

    public QuerySql gt(String column, Object obj) {
        if (StringUtils.isNotBlank(column) && obj != null) {
            where.append(" and ");
            where.append(column);
            where.append(" > ");
            if (obj instanceof Integer) {
                where.append(obj);
            } else {
                where.append("'" + obj + "'");
            }
        }
        return this;
    }

    public QuerySql gtEq(String column, Object obj) {
        if (StringUtils.isNotBlank(column) && obj != null) {
            where.append(" and ");
            where.append(column);
            where.append(" >= ");
            if (obj instanceof Integer) {
                where.append(obj);
            } else {
                where.append("'" + obj + "'");
            }
        }
        return this;
    }

}
