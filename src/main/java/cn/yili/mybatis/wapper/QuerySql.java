package cn.yili.mybatis.wapper;


import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class QuerySql<Entity> {

    private StringBuilder select = null;

    private StringBuilder where = null;

    private StringBuilder orderBy = null;

    private Entity t;

    private Class<?> entityClass;

    private boolean selected = false;

    public QuerySql(Entity t) {
        this.t = t;
        entityClass = t.getClass();
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
        return result + " limit 1";
    }

    public String toSql() {
        StringBuilder result = new StringBuilder();
        String table = "";
        String columns = "";
        String defWhere = "";
        String defOrderBy = "";
        try {
            Object tableObj = entityClass.getMethod("baseGenTable").invoke(t);
            if (tableObj != null) {
                table = tableObj.toString();
            } else {
                throw new RuntimeException("没有找到有效的实体");
            }
            Object baseGenColumnNames = entityClass.getMethod("baseGenColumnNames").invoke(t);
            if (baseGenColumnNames != null) {
                columns = baseGenColumnNames.toString();
            } else {
                throw new RuntimeException("没有找到有效的实体");
            }
            Object baseGenDefWhere = entityClass.getMethod("baseGenDefWhere").invoke(t);
            if (baseGenDefWhere != null) {
                defWhere = baseGenDefWhere.toString();
            } else {
                throw new RuntimeException("没有找到有效的实体.");
            }
            Object baseGenOrderBy = entityClass.getMethod("baseGenOrderBy").invoke(t);
            if (baseGenOrderBy != null) {
                defOrderBy = baseGenOrderBy.toString();
            } else {
                throw new RuntimeException("没有找到有效的实体");
            }

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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

}
