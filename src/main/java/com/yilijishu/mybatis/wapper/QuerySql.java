package com.yilijishu.mybatis.wapper;


import com.yilijishu.mybatis.iter.BaseBeanInterface;
import com.yilijishu.utils.exceptions.BizException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class QuerySql<Entity> {

    private StringBuilder select = null;

    private StringBuilder where = null;

    private StringBuilder orderBy = null;

    private Entity t;

    private BaseBeanInterface bbi;

    private boolean selected = false;

    public QuerySql(Entity t) {
        this.t = t;
        if (!(t instanceof BaseBeanInterface)) {
            throw new BizException("无效的实体");
        }
        this.bbi = (BaseBeanInterface) t;
        this.select = new StringBuilder();
        this.where = new StringBuilder();
        this.orderBy = new StringBuilder();
    }

    public QuerySql select(String... column) {
        if (column != null && column.length > 0) {
            select.append(String.join(", ", column));
            selected = true;
        }
        return this;
    }

    public QuerySql count() {
        select.append(" count(1) ");
        selected = true;
        return this;
    }

    public QuerySql in(String column, List ins) {
        if (StringUtils.isNotBlank(column) && ins != null && ins.size() > 0) {
            where.append(" and ");
            where.append(column);
            where.append(" in (");
            for (int i = 0; i < ins.size(); i++) {
                Object tmp = ins.get(i);
                if (tmp instanceof Integer || tmp instanceof Long) {
                    where.append(tmp);
                } else {
                    where.append("'" + tmp + "'");
                }
                if (i < ins.size() - 1) {
                    where.append(",");
                }
            }
            where.append(") ");
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
            if (obj instanceof Integer || obj instanceof Long || obj instanceof Boolean) {
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
            if (obj instanceof Integer || obj instanceof Long || obj instanceof Boolean) {
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
            if (obj instanceof Integer || obj instanceof Long || obj instanceof Boolean) {
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
            if (obj instanceof Integer || obj instanceof Long || obj instanceof Boolean) {
                where.append(obj);
            } else {
                where.append("'" + obj + "'");
            }
        }
        return this;
    }

    public QuerySql eq(String column, Object obj) {
        if (StringUtils.isNotBlank(column) && obj != null) {
            where.append(" and ");
            where.append(column);
            where.append(" = ");
            if (obj instanceof Integer || obj instanceof Long || obj instanceof Boolean) {
                where.append(obj);
            } else {
                where.append("'" + obj + "'");
            }
        }
        return this;
    }

    public QuerySql notEq(String column, Object obj) {
        if (StringUtils.isNotBlank(column) && obj != null) {
            where.append(" and ");
            where.append(column);
            where.append(" != ");
            if (obj instanceof Integer || obj instanceof Long || obj instanceof Boolean) {
                where.append(obj);
            } else {
                where.append("'" + obj + "'");
            }
        }
        return this;
    }

    public QuerySql orderByDesc(String column) {
        if (orderBy.length() > 1) {
            orderBy.append(" ,");
        }
        orderBy.append(column);
        orderBy.append(" desc ");
        return this;
    }

    public QuerySql orderByAsc(String column) {
        if (orderBy.length() > 1) {
            orderBy.append(" ,");
        }
        orderBy.append(column);
        orderBy.append(" asc ");
        return this;
    }

    public String toSqlOne() {
        String result = toSql();
        String database = bbi.baseSqlDatabase();
        switch (database) {
            case "ORACLE": {
                result += "  FETCH FIRST 1 ROW ONLY ";
                break;
            }
            case "MYSQL":
            case "POSTGRESQL":
            default: {
                result += " limit 1";
                break;
            }
        }
        return result;
    }

    public String toSql() {
        StringBuilder result = new StringBuilder();
        String table = "";
        String columns = "";
        String defWhere = "";
        String defOrderBy = "";
        Object tableObj = bbi.baseGenTable();
        if (tableObj != null) {
            table = tableObj.toString();
        } else {
            throw new RuntimeException("没有找到有效的实体");
        }
        Object baseGenColumnNames = bbi.baseGenColumnNames();
        if (baseGenColumnNames != null) {
            columns = baseGenColumnNames.toString();
        } else {
            throw new RuntimeException("没有找到有效的实体");
        }
        Object baseGenDefWhere = bbi.baseGenDefWhere();
        if (baseGenDefWhere != null) {
            defWhere = baseGenDefWhere.toString();
        } else {
            throw new RuntimeException("没有找到有效的实体.");
        }
        Object baseGenOrderBy = bbi.baseGenOrderBy();
        if (baseGenOrderBy != null) {
            defOrderBy = baseGenOrderBy.toString();
        } else {
            throw new RuntimeException("没有找到有效的实体");
        }

        result.append("select ");
        if (selected) {
            result.append(select);
        } else {
            result.append(columns);
        }
        result.append(" from ");
        result.append(table);
        if (StringUtils.isNotBlank(defWhere)) {
            result.append(defWhere);
            result.append(where);
        } else if (where.length() > 5) {
            result.append("where ");
            result.append(where.substring(4));
        }
        if (orderBy.length() > 2) {
            result.append(" order by ");
            result.append(orderBy);
        } else if (StringUtils.isNotBlank(defOrderBy)) {
            result.append(defOrderBy);
        }
        return result.toString();
    }

    public String toSqlCount() {
        StringBuilder result = new StringBuilder();
        String table = "";
        String columns = "";
        String defWhere = "";
        String defOrderBy = "";
        Object tableObj = bbi.baseGenTable();
        if (tableObj != null) {
            table = tableObj.toString();
        } else {
            throw new RuntimeException("没有找到有效的实体");
        }
        Object baseGenColumnNames = bbi.baseGenColumnNames();
        if (baseGenColumnNames != null) {
            columns = baseGenColumnNames.toString();
        } else {
            throw new RuntimeException("没有找到有效的实体");
        }
        Object baseGenDefWhere = bbi.baseGenDefWhere();
        if (baseGenDefWhere != null) {
            defWhere = baseGenDefWhere.toString();
        } else {
            throw new RuntimeException("没有找到有效的实体.");
        }
        Object baseGenOrderBy = bbi.baseGenOrderBy();
        if (baseGenOrderBy != null) {
            defOrderBy = baseGenOrderBy.toString();
        } else {
            throw new RuntimeException("没有找到有效的实体");
        }
        result.append("select ");
        result.append(" count(*) ");
        result.append(" from ");
        result.append(table);
        if (StringUtils.isNotBlank(defWhere)) {
            result.append(defWhere);
            result.append(where);
        } else if (where.length() > 5) {
            result.append("where ");
            result.append(where.substring(4));
        }
        return result.toString();
    }

}
