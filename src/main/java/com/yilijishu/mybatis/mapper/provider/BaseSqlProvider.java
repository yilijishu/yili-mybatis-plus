package com.yilijishu.mybatis.mapper.provider;

import com.yilijishu.mybatis.ann.SetDataBase;
import com.yilijishu.mybatis.constant.Constant;
import com.yilijishu.mybatis.entity.Page;
import com.yilijishu.mybatis.iter.BaseBeanInterface;
import com.yilijishu.mybatis.wapper.YiliBaseSql;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;

import java.text.MessageFormat;
import java.util.Collection;

public class BaseSqlProvider {

    static final String PARAM_PAGE = "page";
    static final String PARAM_OBJECT = "p";
    static final String PARAM_OFFSET = "offset";
    static final String PARAM_SIZE = "size";

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
     */


    /**
     * 设置数据库
     * @param p 实例
     * @param <T> 实例
     */
    public <T extends BaseBeanInterface> void setDb(T p) {
        if (Constant.dataBase == null) {
            Constant.dataBase = SetDataBase.DataBaseEnum.convert(p.baseSqlDatabase());
        }
    }

    /**
     * 创建
     * @param p 实例
     * @param <T> 实例范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String create(@Param(PARAM_OBJECT) T p) {
        setDb(p);
        StringBuffer sbf = new StringBuffer();
        String database = p.baseSqlDatabase();
        sbf.append(" CREATE TABLE ");
        sbf.append(p.baseGenTable());
        sbf.append(" (");
        sbf.append(p.baseCreateTable());
        sbf.append(" ) ");
        return sbf.toString();
    }

    /**
     * 插入语句组装
     * @param p 实例
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String insert(@Param(PARAM_OBJECT) T p) {
        setDb(p);
        StringBuffer sbf = new StringBuffer();
        sbf.append("insert into ");
        sbf.append(p.baseGenTable());
        sbf.append(" (");
        sbf.append(p.baseGenInertColumnNames());
        sbf.append(") values (");
        sbf.append(p.baseGenInsertNames());
        sbf.append(")");

        return sbf.toString();
    }

    /**
     * 修改语句组装
     *
     * @param p 实例
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String update(@Param(PARAM_OBJECT) T p) {
        setDb(p);
        StringBuffer sbf = new StringBuffer();
        sbf.append("update ");
        sbf.append(p.baseGenTable());
        sbf.append(" set ");
        sbf.append(p.baseGenUpdateSet());
        sbf.append(" where ");
        sbf.append(p.baseGenUpdateWhere());
        return sbf.toString();
    }

    /**
     * 判断是否不为空修改语句组装
     *
     * @param p 为空不修改
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String updateNotIfNull(@Param(PARAM_OBJECT) T p) {
        setDb(p);
        StringBuffer sbf = new StringBuffer();
        sbf.append("update ");
        sbf.append(p.baseGenTable());
        sbf.append(" set ");
        sbf.append(p.baseGenUpdateAllSet());
        sbf.append(" where ");
        sbf.append(p.baseGenUpdateWhere());

        return sbf.toString();
    }

    /**
     * 批量插入语句组装
     *
     * @param p 实例列表
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String insertAll(@Param("list") Collection<T> p) {

        StringBuffer sbf = new StringBuffer();
        if (p != null && p.size() > 0) {
            BaseBeanInterface obj = p.stream().findFirst().orElse(null);
            setDb(obj);
            sbf.append("insert into ");
            sbf.append(obj.baseGenTable());
            sbf.append(" (");
            sbf.append(obj.baseGenInertColumnNames());
            sbf.append(") values ");
            MessageFormat messageFormat = new MessageFormat(obj.baseGenInsertListNames());
            int i = 0;
            for (Object tmp : p) {
                if (i > 0) {
                    sbf.append(",");
                }
                sbf.append("(");
                sbf.append(messageFormat.format(new Object[]{i}));
                sbf.append(")");

                i++;
            }
        }

        return sbf.toString();
    }

    //----基础方法.

    /**
     * 查询语句组装
     *
     * @param p    实例
     * @param page 分页实例
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String selectPage(@Param(PARAM_OBJECT) T p, @Param(PARAM_PAGE) Page page) {
        setDb(p);
        StringBuffer sbf = new StringBuffer();
        sbf.append(" SELECT ");
        sbf.append(p.baseGenColumnNames());
        sbf.append(" FROM ");
        sbf.append(p.baseGenTable());
        String whereSql =  p.baseGenSelectWhere();
        if(whereSql != null && whereSql.length() > 2) {
            sbf.append(" WHERE ");
            sbf.append(whereSql);
        }
        sbf.append(p.baseGenOrderBy());
        if (page != null) {
            sbf.append(Constant.limitEscape(page.getStart().toString(), page.getPageSize().toString()));
        }
        return sbf.toString();
    }


    /**
     * 查询语句组装
     *
     * @param p    实例
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String select(@Param(PARAM_OBJECT) T p) {
        setDb(p);
        StringBuffer sbf = new StringBuffer();
        sbf.append(" SELECT ");
        sbf.append(p.baseGenColumnNames());
        sbf.append(" FROM ");
        sbf.append(p.baseGenTable());
        String whereSql =  p.baseGenSelectWhere();
        if(whereSql != null && whereSql.length() > 2) {
            sbf.append(" WHERE ");
            sbf.append(whereSql);
        }
        sbf.append(p.baseGenOrderBy());
        return sbf.toString();
    }

    /**
     * 获取单条语句组装
     *
     * @param p 实体
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String get(@Param(PARAM_OBJECT) T p) {
        setDb(p);
        StringBuffer sbf = new StringBuffer();
        sbf.append(" SELECT ");
        sbf.append(p.baseGenColumnNames());
        sbf.append(" FROM ");
        sbf.append(p.baseGenTable());
        String whereSql =  p.baseGenSelectWhere();
        if(whereSql != null && whereSql.length() > 2) {
            sbf.append(" WHERE ");
            sbf.append(whereSql);
        }
        sbf.append(p.baseGenOrderBy());
        sbf.append(Constant.limitEscape("0", "1"));
        return sbf.toString();
    }

    /**
     * 查询符合条件的语句总条数
     *
     * @param p 实例
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String count(@Param(PARAM_OBJECT) T p) {
        setDb(p);
        StringBuffer sbf = new StringBuffer();
        sbf.append(" SELECT ");
        sbf.append(" COUNT(1) ");
        sbf.append(" FROM ");
        sbf.append(p.baseGenTable());
        String whereSql =  p.baseGenSelectWhere();
        if(whereSql != null && whereSql.length() > 2) {
            sbf.append(" WHERE ");
            sbf.append(whereSql);
        }
        return sbf.toString();
    }

    /**
     * 自定义sql查询
     *
     * @param querySql YiliBaseSql实例
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String querySql(@Param(PARAM_OBJECT) YiliBaseSql<T> querySql) {
        return querySql.toSql();
    }

    /**
     * 自定义sql查询
     *
     * @param querySql YiliBaseSql实例
     * @param offset 偏移量
     * @param size size
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String querySqlOffset(@Param(PARAM_OBJECT) YiliBaseSql<T> querySql, @Param(PARAM_OFFSET) Integer offset, @Param(PARAM_SIZE) Integer size) {
        StringBuffer sbf = new StringBuffer();
        sbf.append(querySql.toSql());
        sbf.append(Constant.limitEscape(offset.toString(), size.toString()));
        throw new RuntimeException("使用了无效的标准组件");
    }


    /**
     * 自定义sql查询，分页版本
     *
     * @param querySql YiliBaseSql实例
     * @param page 分页实例
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String querySqlOfPage(@Param(PARAM_OBJECT) YiliBaseSql<T> querySql, @Param(PARAM_PAGE) Page page) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(querySql.toSql());
        stringBuffer.append(Constant.limitEscape(page.getStart().toString(), page.getPageSize().toString()));
        return stringBuffer.toString();
    }


    /**
     * 自定义sql查询 取第一条
     *
     * @param querySql YiliBaseSql实例
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String querySqlOne(@Param(PARAM_OBJECT) YiliBaseSql<T> querySql) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(querySql.toSql());
        stringBuffer.append(Constant.limitEscape("0", "1"));
        return stringBuffer.toString();
    }

    /**
     * 自定义sql修改
     *
     * @param updateSql YiliBaseSql实例
     * @param <T>       泛型类
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String updateSql(@Param(PARAM_OBJECT) YiliBaseSql<T> updateSql) {
        return updateSql.toSql();
    }

    /**
     * 根据主键ID组装删除语句
     *
     * @param t 实例
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String delete(@Param(PARAM_OBJECT) T t) {
        setDb(t);
        StringBuilder sbf = new StringBuilder();
        sbf.append(" DELETE FROM ");
        sbf.append(t.baseGenTable());
        sbf.append(t.baseGenDefWhere());
        sbf.append(t.baseGenSelectWhere());
        return sbf.toString();
    }

    /**
     * 根据主键ID删除语句组装
     *
     * @param t 实例
     * @param ids id列表
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String deleteByIds(@Param(PARAM_OBJECT) T t, @Param("ids") Collection<?> ids) {
        setDb(t);
        StringBuilder sbf = new StringBuilder();
        sbf.append(" DELETE FROM ");
        sbf.append(t.baseGenTable());
        sbf.append(" WHERE ");
        sbf.append(t.baseGenId());
        sbf.append(" IN (");
        if (ids != null && ids.size() > 0) {
            int i = 0;
            for (Object o : ids) {
                if (i > 0) {
                    sbf.append(", ");
                }
                sbf.append(Constant.convertObject(o));
                i++;
            }
        }
        sbf.append(")");
        return sbf.toString();
    }

    /**
     * 根据虚拟ID组装批量删除语句
     *
     * @param t 实例
     * @param ids id列表
     * @param <T> 范型
     * @return 返回sql
     */
    @SneakyThrows
    public <T extends BaseBeanInterface> String deleteByVirtualIds(@Param(PARAM_OBJECT) T t, @Param("ids") Collection<?> ids) {
        setDb(t);
        StringBuilder sbf = new StringBuilder();
        sbf.append(" DELETE FROM ");
        sbf.append(t.baseGenTable());
        sbf.append(" WHERE ");
        String str = t.baseGenVirtualId();
        if (StringUtils.isNotBlank(str)) {
            sbf.append(str);
            sbf.append(" IN (");
            if (ids != null && ids.size() > 0) {
                int i = 0;
                for (Object o : ids) {
                    if (i > 0) {
                        sbf.append(", ");
                    }
                    sbf.append(Constant.convertObject(o));
                    i++;
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
     *
     * @param deleteSql YiliBaseSql实例
     * @param <T> 范型
     * @return 返回sql
     */
    public <T extends BaseBeanInterface> String deleteSql(@Param(PARAM_OBJECT) YiliBaseSql<T> deleteSql) {
        return deleteSql.toSql();
    }


}
