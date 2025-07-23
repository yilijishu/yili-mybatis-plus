package com.yilijishu.mybatis.mapper.provider;

import com.yilijishu.mybatis.iter.BaseBeanInterface;
import com.yilijishu.mybatis.wapper.DeleteSql;
import com.yilijishu.mybatis.wapper.QuerySql;
import com.yilijishu.mybatis.wapper.UpdateSql;
import com.yilijishu.utils.exceptions.BizException;
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
     * baseCreateTable 生成的创建Table脚本 create table xxx ();
     * baseGenVirtualId 生成的虚拟ID，列名
     * genDelTag 生成逻辑删除位 true false
     * genDelTagColumn 生成逻辑删除属性 base_1
     * genDelTagValue 生成删除标记值 1
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
        if(!(obj instanceof BaseBeanInterface)){
            throw new BizException("无效的实体");
        }
        BaseBeanInterface bbi = (BaseBeanInterface) obj;
        String table = bbi.baseGenTable();
        String database = bbi.baseSqlDatabase();
        sbf.append("create table ");
        switch (database) {
            case "MYSQL" :
            case "POSTGRESQL" : {
                sbf.append(" IF NOT EXISTS ");
                break;
            }
            default: break;
        }
        sbf.append(table);
        sbf.append(" (");
        sbf.append(bbi.baseCreateTable());
        sbf.append(" ); ");
        switch (database) {
            case "ORACLE": {
                sbf.append("CREATE SEQUENCE " + table + "_seq START WITH 1 INCREMENT BY 1;");
                sbf.append("CREATE OR REPLACE TRIGGER " + table + "_trigger\n" +
                        "BEFORE INSERT ON "+table+"\n" +
                        "FOR EACH ROW\n" +
                        "BEGIN\n" +
                        "    SELECT " + table + "_seq.NEXTVAL INTO :NEW." + bbi.baseGenId() +" FROM dual;\n" +
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
        if(!(obj instanceof BaseBeanInterface)){
            throw new BizException("无效的实体");
        }
        BaseBeanInterface bbi = (BaseBeanInterface) obj;
        sbf.append(bbi.baseGenTable());
        sbf.append(" (");
        sbf.append(bbi.baseGenInertColumnNames());
        sbf.append(") values (");
        sbf.append(bbi.baseGenInsertNames());
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
        if(!(obj instanceof BaseBeanInterface)){
            throw new BizException("无效的实体");
        }
        BaseBeanInterface bbi = (BaseBeanInterface) obj;
        sbf.append("update ");
        sbf.append(bbi.baseGenTable());
        sbf.append(" set ");
        sbf.append(bbi.baseGenUpdateSet());
        sbf.append(" where ");
        sbf.append(bbi.baseGenUpdateWhere());

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
        if(!(obj instanceof BaseBeanInterface)){
            throw new BizException("无效的实体");
        }
        BaseBeanInterface bbi = (BaseBeanInterface) obj;
        sbf.append("update ");
        sbf.append(bbi.baseGenTable());
        sbf.append(" set ");
        sbf.append(bbi.baseGenUpdateAllSet());
        sbf.append(" where ");
        sbf.append(bbi.baseGenUpdateWhere());

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
                if(!(obj instanceof BaseBeanInterface)){
                    throw new BizException("无效的实体");
                }
                BaseBeanInterface bbi = (BaseBeanInterface) obj;
                sbf.append("insert into ");
                sbf.append(bbi.baseGenTable());
                sbf.append(" (");
                sbf.append(bbi.baseGenInertColumnNames());
                sbf.append(") values ");
                MessageFormat messageFormat = new MessageFormat(bbi.baseGenInsertListNames());
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
        if(!(obj instanceof BaseBeanInterface)){
            throw new BizException("无效的实体");
        }
        BaseBeanInterface bbi = (BaseBeanInterface) obj;
        sbf.append("select ");
        sbf.append(bbi.baseGenColumnNames());
        sbf.append(" from ");
        sbf.append(bbi.baseGenTable());
        sbf.append(bbi.baseGenDefWhere());
        sbf.append(bbi.baseGenSelectWhere());
        sbf.append(bbi.baseGenOrderBy());
        if (map.get("page") != null) {
            String database = bbi.baseSqlDatabase();
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
        if(!(obj instanceof BaseBeanInterface)){
            throw new BizException("无效的实体");
        }
        BaseBeanInterface bbi = (BaseBeanInterface) obj;
        sbf.append("select ");
        sbf.append(bbi.baseGenColumnNames());
        sbf.append(" from ");
        sbf.append(bbi.baseGenTable());
        sbf.append(bbi.baseGenDefWhere());
        sbf.append(bbi.baseGenSelectWhere());
        sbf.append(bbi.baseGenOrderBy());

        String database = bbi.baseSqlDatabase();
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
        if(!(obj instanceof BaseBeanInterface)){
            throw new BizException("无效的实体");
        }
        BaseBeanInterface bbi = (BaseBeanInterface) obj;
        sbf.append("select ");
        sbf.append("count(*)");
        sbf.append(" from ");
        sbf.append(bbi.baseGenTable());
        sbf.append(bbi.baseGenDefWhere());
        sbf.append(bbi.baseGenSelectWhere());

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
        if(!(obj instanceof BaseBeanInterface)){
            throw new BizException("无效的实体");
        }
        BaseBeanInterface bbi = (BaseBeanInterface) obj;
        if (obj instanceof QuerySql) {
            QuerySql<?> querySql = (QuerySql<?>) obj;
            String result = querySql.toSql();
            String database = bbi.baseSqlDatabase();
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
    public String delete(Map<String, Object> map) {
        Object obj = map.get("p");
        if(!(obj instanceof BaseBeanInterface)){
            throw new BizException("无效的实体");
        }
        BaseBeanInterface bbi = (BaseBeanInterface) obj;
        StringBuilder sbf = new StringBuilder();
        sbf.append("delete from ");
        sbf.append(bbi.baseGenTable());
        sbf.append(bbi.baseGenDefWhere());
        sbf.append(bbi.baseGenSelectWhere());
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
        if(!(obj instanceof BaseBeanInterface)){
            throw new BizException("无效的实体");
        }
        BaseBeanInterface bbi = (BaseBeanInterface) obj;
        StringBuilder sbf = new StringBuilder();
        Collection<?> collection = (Collection<?>) map.get("ids");
        sbf.append("delete from ");
        sbf.append(bbi.baseGenTable());
        sbf.append(" where ");
        String str = bbi.baseGenVirtualId();
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
        if(!(obj instanceof BaseBeanInterface)){
            throw new BizException("无效的实体");
        }
        BaseBeanInterface bbi = (BaseBeanInterface) obj;
        StringBuilder sbf = new StringBuilder();
        Collection<?> collection = (Collection<?>) map.get("ids");
        sbf.append("delete from ");
        sbf.append(bbi.baseGenTable());
        sbf.append(" where ");
        String str = bbi.baseGenId();
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
    public String deleteSql(Map<String, Object> map) {
        Object obj = map.get("p");
        if (obj instanceof DeleteSql) {
            DeleteSql<?> deleteSql = (DeleteSql<?>) obj;
            return deleteSql.toSql();
        }
        throw new RuntimeException("使用了无效的标准组件");
    }


}
