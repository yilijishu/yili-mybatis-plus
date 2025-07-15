package com.yilijishu.mybatis.mapper.provider;

import com.yilijishu.mybatis.wapper.DeleteSql;
import com.yilijishu.mybatis.wapper.QuerySql;
import com.yilijishu.mybatis.wapper.UpdateSql;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BaseSqlProvider {

    /**
     * baseGenTable  生成的表名  table_name
     * baseGenColumnNames 生成的全部列名 逗号分割  base_1, base_2
     * baseGenNames  生成的全部列名     废弃
     * baseGenListNames  生成的列表名称   废弃
     * baseGenInertColumnNames  生成的插入列名 逗号分割 base_1, base_2
     * baseGenInsertNames   生成的插入Name 逗号分隔   base1, base2
     * baseGenInsertListNames   生成的插入列表Name  逗号分隔 list[0,1,2].name
     * baseGenDefWhere  生成的默认where条件， where 1=1
     * baseGenId  生成的主键列名， 主键列名。
     * baseGenUpdateAllSet  生成的修改全部的set语法
     * baseGenSelectWhere  生成的查询where
     * baseGenUpdateSet   生成的修改set
     * baseGenUpdateWhere  生成的修改where条件
     * baseGenOrderBy  生成的order by
     * baseSqlDatabase 生成的database 如：MYSQL ORACLE POSTGRESQL
     * baseCreateTable 生成的创建Table脚本
     *
     *
     */

    /**
     * 组装创建语句
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String create(Map<String, Object> map) {
        Object obj = map.get("p");
        StringBuffer sbf = new StringBuffer();
        String table = obj.getClass().getMethod("baseGenTable").invoke(obj).toString();
        String database = obj.getClass().getMethod("baseSqlDatabase").invoke(obj).toString();
        sbf.append("create table ");
        switch (database) {
            case "MYSQL" :
            case "POSTGRESQL" : {
                sbf.append(" IF NOT EXISTS ");
                break;
            }
            default: break;
        }
        sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
        sbf.append(" (");
        sbf.append(obj.getClass().getMethod("baseCreateTable").invoke(obj));
        sbf.append(" ); ");
        switch (database) {
            case "ORACLE": {
                sbf.append("CREATE SEQUENCE " + table + "_seq START WITH 1 INCREMENT BY 1;");
                sbf.append("CREATE OR REPLACE TRIGGER " + table + "_trigger\n" +
                        "BEFORE INSERT ON "+table+"\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT " + table + "_seq.NEXTVAL INTO :NEW." + obj.getClass().getMethod("baseGenId").invoke(obj).toString() +" FROM dual;\n" +
                        "END;");
                break;
            }
            default: break;
        }

        return sbf.toString();
    }

    /**
     *
     * 插入语句组装
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String insert(Map<String, Object> map) {

        Object obj = map.get("p");
        StringBuffer sbf = new StringBuffer();
        sbf.append("insert into ");
        sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
        sbf.append(" (");
        sbf.append(obj.getClass().getMethod("baseGenInertColumnNames").invoke(obj));
        sbf.append(") values (");
        sbf.append(obj.getClass().getMethod("baseGenInsertNames").invoke(obj));
        sbf.append(")");

        return sbf.toString();
    }
    /**
     * 修改语句组装
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String update(Map<String, Object> map) {
        Object obj = map.get("p");
        StringBuffer sbf = new StringBuffer();
        sbf.append("update ");
        sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
        sbf.append(" set ");
        sbf.append(obj.getClass().getMethod("baseGenUpdateSet").invoke(obj));
        sbf.append(" where ");
        sbf.append(obj.getClass().getMethod("baseGenUpdateWhere").invoke(obj));

        return sbf.toString();
    }
    /**
     * 判断是否不为空修改语句组装
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String updateNotIfNull(Map<String, Object> map) {
        Object obj = map.get("p");
        StringBuffer sbf = new StringBuffer();
        sbf.append("update ");
        sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
        sbf.append(" set ");
        sbf.append(obj.getClass().getMethod("baseGenUpdateAllSet").invoke(obj));
        sbf.append(" where ");
        sbf.append(obj.getClass().getMethod("baseGenUpdateWhere").invoke(obj));

        return sbf.toString();
    }

    /**
     * 批量插入语句组装
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String insertAll(Map<String, Object> map) {
        StringBuffer sbf = new StringBuffer();
        Object obj2 = map.get("list");
        if (obj2 != null && obj2 instanceof List) {
            List<?> list = (List<?>) obj2;
            if (list != null && list.size() > 0) {
                Object obj = list.get(0);
                sbf.append("insert into ");
                sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
                sbf.append(" (");
                sbf.append(obj.getClass().getMethod("baseGenInertColumnNames").invoke(obj));
                sbf.append(") values ");
                MessageFormat messageFormat = new MessageFormat((String) obj.getClass().getMethod("baseGenInsertListNames").invoke(obj));
                for (int i = 0; i < list.size(); i++) {
                    sbf.append("(");
                    sbf.append(messageFormat.format(new Object[]{i}));
                    sbf.append(")");
                    if (i + 1 < list.size()) {
                        sbf.append(",");
                    }
                }
            }
        }
        return sbf.toString();
    }

    //----基础方法.
    /**
     * 查询语句组装
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String select(Map<String, Object> map) {
        Object obj = map.get("p");
        StringBuffer sbf = new StringBuffer();
        sbf.append("select ");
        sbf.append(obj.getClass().getMethod("baseGenColumnNames").invoke(obj));
        sbf.append(" from ");
        sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
        sbf.append(obj.getClass().getMethod("baseGenDefWhere").invoke(obj));
        sbf.append(obj.getClass().getMethod("baseGenSelectWhere").invoke(obj));
        sbf.append(obj.getClass().getMethod("baseGenOrderBy").invoke(obj));
        if (map.get("page") != null) {
            String database = obj.getClass().getMethod("baseSqlDatabase").invoke(obj).toString();
            switch (database) {
                case "MYSQL" :
                case "POSTGRESQL" : {
                    sbf.append("        LIMIT ${page.start} OFFSET ${page.pageSize} ");
                    break;
                }
                case "ORACLE" : {
                    sbf.append("    OFFSET ${page.pageSize} ROWS FETCH NEXT ${page.start} ROWS ONLY ");
                    break;
                }
                default: break;
            }
        }
        return sbf.toString();
    }
    /**
     * 获取单条语句组装
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String get(Map<String, Object> map) {
        Object obj = map.get("p");
        StringBuffer sbf = new StringBuffer();
        sbf.append("select ");
        sbf.append(obj.getClass().getMethod("baseGenColumnNames").invoke(obj));
        sbf.append(" from ");
        sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
        sbf.append(obj.getClass().getMethod("baseGenDefWhere").invoke(obj));
        sbf.append(obj.getClass().getMethod("baseGenSelectWhere").invoke(obj));
        sbf.append(obj.getClass().getMethod("baseGenOrderBy").invoke(obj));

        String database = obj.getClass().getMethod("baseSqlDatabase").invoke(obj).toString();
        switch (database) {
            case "MYSQL" :
            case "POSTGRESQL" : {
                sbf.append("  limit 1 ");
                break;
            }
            case "ORACLE" : {
                sbf.append("  FETCH FIRST 1 ROW ONLY ");
                break;
            }
            default: break;
        }

        return sbf.toString();
    }
    /**
     * 查询符合条件的语句总条数
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String count(Map<String, Object> map) {
        Object obj = map.get("p");
        StringBuffer sbf = new StringBuffer();
        sbf.append("select ");
        sbf.append("count(*)");
        sbf.append(" from ");
        sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
        sbf.append(obj.getClass().getMethod("baseGenDefWhere").invoke(obj));
        sbf.append(obj.getClass().getMethod("baseGenSelectWhere").invoke(obj));

        return sbf.toString();
    }
    /**
     * 自定义sql查询
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String querySql(Map<String, Object> map) {
        Object obj = map.get("p");
        if (obj instanceof QuerySql) {
            QuerySql<?> querySql = (QuerySql<?>) obj;
            return querySql.toSql();
        }
        throw new RuntimeException("使用了无效的标准组件");
    }

    /**
     * 自定义sql查询，分页版本
     * @param map mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String querySqlOfPage(Map<String, Object> map) {
        Object obj = map.get("p");
        if (obj instanceof QuerySql) {
            QuerySql<?> querySql = (QuerySql<?>) obj;
            String result = querySql.toSql();
            String database = obj.getClass().getMethod("baseSqlDatabase").invoke(obj).toString();
            switch (database) {
                case "ORACLE" : {
                    result += "    OFFSET ${page.pageSize} ROWS FETCH NEXT ${page.start} ROWS ONLY ";
                    break;
                }
                case "MYSQL" :
                case "POSTGRESQL" :
                default: {
                    result += "        LIMIT ${page.start} OFFSET ${page.pageSize} ";
                    break;
                }
            }
            return result;
        }
        throw new RuntimeException("使用了无效的标准组件");
    }

    /**
     * 自定义sql查询 取第一条
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String querySqlOne(Map<String, Object> map) {
        Object obj = map.get("p");
        if (obj instanceof QuerySql) {
            QuerySql<?> querySql = (QuerySql<?>) obj;
            return querySql.toSqlOne();
        }
        throw new RuntimeException("使用了无效的标准组件");
    }
    /**
     * 自定义sql修改
     * @param updateSql  mybatis 数据参数map
     * @param <T> 泛型类
     * @return 返回sql
     */
    @SneakyThrows
    public <T> String updateSql(UpdateSql<T> updateSql) {
        return updateSql.toSql();
    }
    /**
     * 根据主键ID组装删除语句
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String deleteById(Map<String, Object> map) {
        Object obj = map.get("p");
        StringBuilder sbf = new StringBuilder();
        Object o = map.get("id");
        sbf.append("delete from ");
        sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
        sbf.append(" where ");
        String str = (String) obj.getClass().getMethod("baseGenId").invoke(obj);
        if (StringUtils.isNotBlank(str)) {
            sbf.append(str);
            sbf.append(" = ");
            if (o != null) {

                if (o instanceof Integer || o instanceof Long) {
                    sbf.append(o);
                } else {
                    sbf.append("'");
                    sbf.append(o);
                    sbf.append("'");
                }

            }
        } else {
            throw new RuntimeException("没有定义主键");
        }
        return sbf.toString();
    }
    /**
     * 根据虚拟ID组装删除语句
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String deleteByVirtualId(Map<String, Object> map) {
        Object obj = map.get("p");
        StringBuilder sbf = new StringBuilder();
        Object o = map.get("id");
        sbf.append("delete from ");
        sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
        sbf.append(" where ");
        String str = (String) obj.getClass().getMethod("baseGenVirtualId").invoke(obj);
        if (StringUtils.isNotBlank(str)) {
            sbf.append(str);
            sbf.append(" = ");
            if (o != null) {

                if (o instanceof Integer || o instanceof Long) {
                    sbf.append(o);
                } else {
                    sbf.append("'");
                    sbf.append(o);
                    sbf.append("'");
                }

            }
        } else {
            throw new RuntimeException("没有定义主键");
        }
        return sbf.toString();
    }
    /**
     * 根据虚拟ID组装批量删除语句
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String deleteByVirtualIds(Map<String, Object> map) {
        Object obj = map.get("p");
        StringBuilder sbf = new StringBuilder();
        Collection<?> collection = (Collection<?>) map.get("ids");
        sbf.append("delete from ");
        sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
        sbf.append(" where ");
        String str = (String) obj.getClass().getMethod("baseGenVirtualId").invoke(obj);
        if (StringUtils.isNotBlank(str)) {
            sbf.append(str);
            sbf.append(" in (");
            if (collection != null && collection.size() > 0) {
                for (Object o : collection) {
                    if (o instanceof Integer || o instanceof Long) {
                        sbf.append(o);
                    } else {
                        sbf.append("'");
                        sbf.append(o);
                        sbf.append("'");
                    }
                }
            }
            sbf.append(")");
        } else {
            throw new RuntimeException("没有定义主键");
        }
        return sbf.toString();
    }
    /**
     * 根据主键ID删除语句组装
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    @SneakyThrows
    public String deleteByIds(Map<String, Object> map) {
        Object obj = map.get("p");
        StringBuilder sbf = new StringBuilder();
        Collection<?> collection = (Collection<?>) map.get("ids");
        sbf.append("delete from ");
        sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
        sbf.append(" where ");
        String str = (String) obj.getClass().getMethod("baseGenId").invoke(obj);
        if (StringUtils.isNotBlank(str)) {
            sbf.append(str);
            sbf.append(" in (");
            if (collection != null && collection.size() > 0) {
                for (Object o : collection) {
                    if (o instanceof Integer || o instanceof Long) {
                        sbf.append(o);
                    } else {
                        sbf.append("'");
                        sbf.append(o);
                        sbf.append("'");
                    }
                }
            }
            sbf.append(")");
        } else {
            throw new RuntimeException("没有定义主键");
        }
        return sbf.toString();
    }
    /**
     * 自定义删除语句
     * @param map  mybatis 数据参数map
     * @return 返回sql
     */
    public String delete(Map<String, Object> map) {
        Object obj = map.get("p");
        if (obj instanceof DeleteSql) {
            DeleteSql<?> deleteSql = (DeleteSql<?>) obj;
            return deleteSql.toSql();
        }
        throw new RuntimeException("使用了无效的标准组件");
    }


}
