package com.yilijishu.mybatis.wapper;


import com.yilijishu.mybatis.iter.BaseBeanInterface;
import com.yilijishu.utils.exceptions.BizException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class UpdateSql<Entity> {

    private StringBuilder set = null;

    private StringBuilder where = null;

    private StringBuilder orderBy = null;

    private BaseBeanInterface bbi;

    private Entity t;

    public UpdateSql(Entity t) {
        this.t = t;
        if (!(t instanceof BaseBeanInterface)) {
            throw new BizException("无效的实体");
        }
        this.bbi = (BaseBeanInterface) t;
        this.set = new StringBuilder();
        this.where = new StringBuilder();
        this.orderBy = new StringBuilder();
    }


    public UpdateSql in(String column, List ins) {
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

    public UpdateSql isNotNull(String column) {
        if (StringUtils.isNotBlank(column)) {
            where.append(" and ");
            where.append(column);
            where.append(" is not null ");
        }
        return this;
    }

    public UpdateSql isNull(String column) {
        if (StringUtils.isNotBlank(column)) {
            where.append(" and ");
            where.append(column);
            where.append(" is null ");
        }
        return this;
    }


    public UpdateSql lt(String column, Object obj) {
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

    public UpdateSql eq(String column, Object obj) {
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

    public UpdateSql notEq(String column, Object obj) {
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

    public UpdateSql ltEq(String column, Object obj) {
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

    public UpdateSql gt(String column, Object obj) {
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

    public UpdateSql gtEq(String column, Object obj) {
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

    public UpdateSql set(String column, Object obj) {
        if (StringUtils.isNotBlank(column) && obj != null) {
            if (set.length() > 2) {
                set.append(" , ");
            }
            set.append(column);
            set.append(" = ");
            if (obj instanceof Integer || obj instanceof Long || obj instanceof Boolean) {
                set.append(obj);
            } else {
                set.append("'" + obj + "'");
            }
        }
        return this;
    }


    public String toSql() {
        StringBuilder result = new StringBuilder();
        String table = "";

        Object tableObj = bbi.baseGenTable();
        if (tableObj != null) {
            table = tableObj.toString();
        } else {
            throw new RuntimeException("没有找到有效的实体");
        }
        result.append(" update ");
        result.append(table);
        result.append(" set ");
        if (StringUtils.isNotBlank(set)) {
            result.append(set);
        } else {
            throw new RuntimeException("无效的修改,没有修改任何字段及其值");
        }
        if (where.length() > 5) {
            result.append(" where ");
            result.append(where.substring(4));
        } else {
            throw new RuntimeException("无效的修改，没有条件语句存在");
        }

        return result.toString();
    }

}
