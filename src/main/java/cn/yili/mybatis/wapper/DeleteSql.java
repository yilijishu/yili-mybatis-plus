package cn.yili.mybatis.wapper;


import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DeleteSql<Entity> {

    private StringBuilder where = null;

    private Entity t;

    private Class<?> entityClass;

    public DeleteSql(Entity t) {
        this.t = t;
        entityClass = t.getClass();
        this.where = new StringBuilder();
    }


    public DeleteSql in(String column, List ins) {
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
        }
        return this;
    }

    public DeleteSql isNotNull(String column) {
        if (StringUtils.isNotBlank(column)) {
            where.append(" and ");
            where.append(column);
            where.append(" is not null ");
        }
        return this;
    }

    public DeleteSql isNull(String column) {
        if (StringUtils.isNotBlank(column)) {
            where.append(" and ");
            where.append(column);
            where.append(" is null ");
        }
        return this;
    }


    public DeleteSql lt(String column, Object obj) {
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

    public DeleteSql eq(String column, Object obj) {
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

    public DeleteSql notEq(String column, Object obj) {
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

    public DeleteSql ltEq(String column, Object obj) {
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

    public DeleteSql gt(String column, Object obj) {
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

    public DeleteSql gtEq(String column, Object obj) {
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

//    public String toIdsSql(Collection collection) {
//        StringBuilder result = new StringBuilder();
//        Class cls = entity.getClass();
//        String id = "";
//        String table = "";
//        try {
//            Object tableObj = cls.getMethod("baseGenTable").invoke(entity);
//            if (tableObj != null) {
//                table = tableObj.toString();
//            } else {
//                throw new RuntimeException("没有找到有效的实体");
//            }
//
//            Object baseGenId = cls.getMethod("baseGenId").invoke(entity);
//            if (baseGenId != null) {
//                id = baseGenId.toString();
//            } else {
//                throw new RuntimeException("没有找到有效的实体");
//            }
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//        result.append(" delete from ");
//        result.append(table);
//        if(collection != null && collection.size() > 0) {
//            result.append(" where ");
//            result.append(id);
//            result.append(" in ( ");
//            collection.forEach(e->{
//                if(e instanceof Integer || e instanceof Long) {
//                    result.append(e);
//                } else {
//                    result.append("'");
//                    result.append(e);
//                    result.append("'");
//                }
//            });
//            result.append(" ) ");
//        } else {
//            throw new RuntimeException("没有条件语句");
//        }
//
//        return result.toString();
//    }

    public String toSql() {
        StringBuilder result = new StringBuilder();
        String table = "";
        try {
            Object tableObj = entityClass.getMethod("baseGenTable").invoke(t);
            if (tableObj != null) {
                table = tableObj.toString();
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
        result.append(" delete from ");
        result.append(table);
        if (where.length() > 5) {
            result.append("where ");
            result.append(where.substring(4));
        } else {
            throw new RuntimeException("无效的修改，没有条件语句存在");
        }

        return result.toString();
    }

}
