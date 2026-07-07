package com.yilijishu.mybatis.wapper;

import com.yilijishu.mybatis.ann.SetDataBase;
import com.yilijishu.mybatis.constant.Constant;

import java.util.ArrayList;
import java.util.List;


public class YiliSql<Entity, B, Children extends YiliSql<Entity, B, Children>> extends YiliBaseSql<Entity> {

    protected final Children child = (Children) this;
    protected Entity entity;
    protected List<QueryConditions> where;

    protected List<QueryConditions> orderBy;


    @Override
    public Entity getEntity() {
        return this.entity;
    }

    public YiliSql(Entity entity) {
        this.entity = entity;
        this.where = new ArrayList<>();
        if (Constant.dataBase == null) {
            Constant.dataBase = SetDataBase.DataBaseEnum.convert(getBaseBeanInterface().baseSqlDatabase());
        }
    }


    public List<QueryConditions> getWhere() {
        return this.where;
    }

    protected <T> void addQuery(SqlKey sqlKey, B column, T... data) {
        where.add(new QueryConditions(sqlKey, columnToString(column), data));
    }


    public String columnToString(B column) {
        return (String) column;
    }


    public String[] columnToString(B... column) {
        String[] result = new String[column.length];
        for (int i = 0; i < column.length; i++) {
            result[i] = (String) column[i];
        }
        return result;
    }

    /**
     * in
     *
     * @param column 字段名
     * @param data   列表
     * @return 返回子类
     */

    public Children in(B column, List data) {
        if (column != null) {
            addQuery(SqlKey.IN, column, data);
        }
        return this.child;
    }

    /**
     * in
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @param data      列表
     * @return 返回子类
     */

    public Children in(boolean condition, B column, List data) {
        if (condition) {
            return in(column, data);
        }
        return this.child;
    }

    /**
     * not in
     *
     * @param column 字段名
     * @param data   列表
     * @return 返回子类
     */

    public Children notIn(B column, List data) {
        if (column != null) {
            addQuery(SqlKey.NOT_IN, column, data);
        }
        return this.child;
    }

    /**
     * not in
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @param data      列表
     * @return 返回子类
     */

    public Children notIn(boolean condition, B column, List data) {
        if (condition) {
            return in(column, data);
        }
        return this.child;
    }

    /**
     * 不为空
     *
     * @param column 字段名
     * @return 返回子类
     */

    public Children isNotNull(B column) {
        if (column != null) {
            addQuery(SqlKey.IS_NOT_NULL, column);
        }
        return this.child;
    }

    /**
     * 不为空
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @return 返回子类
     */

    public Children isNotNull(boolean condition, B column) {
        if (condition) {
            return isNotNull(column);
        }
        return this.child;
    }

    /**
     * 为空
     *
     * @param column 字段名
     * @return 返回子类
     */

    public Children isNull(B column) {
        if (column != null) {
            addQuery(SqlKey.IS_NULL, column);
        }
        return this.child;
    }

    /**
     * 为空
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @return 返回子类
     */

    public Children isNull(boolean condition, B column) {
        if (condition) {
            return isNull(column);
        }
        return this.child;
    }


    /**
     * 小于
     *
     * @param column 字段名
     * @param data   值
     * @return 返回子类
     */

    public Children lt(B column, Object data) {
        if (column != null) {
            addQuery(SqlKey.LT, column, data);
        }
        return this.child;
    }

    /**
     * 小于
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @param data      值
     * @return 返回子类
     */

    public Children lt(boolean condition, B column, Object data) {
        if (condition) {
            return lt(column, data);
        }
        return this.child;
    }


    /**
     * 小于等于
     *
     * @param column 字段名
     * @param data   值
     * @return 返回子类
     */

    public Children lteq(B column, Object data) {
        if (column != null) {
            addQuery(SqlKey.LE, column, data);
        }
        return this.child;
    }

    /**
     * 小于等于
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @param data      值
     * @return 返回子类
     */

    public Children lteq(boolean condition, B column, Object data) {
        if (condition) {
            return lteq(column, data);
        }
        return this.child;
    }


    /**
     * 等于
     *
     * @param column 字段名
     * @param data   值
     * @return 返回子类
     */

    public Children eq(B column, Object data) {
        if (column != null) {
            addQuery(SqlKey.EQ, column, data);
        }
        return this.child;
    }

    /**
     * 等于
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @param data      值
     * @return 返回子类
     */

    public Children eq(boolean condition, B column, Object data) {
        if (condition) {
            return eq(column, data);
        }
        return this.child;
    }

    /**
     * 不等于
     *
     * @param column 字段名
     * @param data   值
     * @return 返回子类
     */

    public Children notEq(B column, Object data) {
        if (column != null) {
            addQuery(SqlKey.NE, column, data);
        }
        return this.child;
    }

    /**
     * 不等于
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @param data      值
     * @return 返回子类
     */

    public Children notEq(boolean condition, B column, Object data) {
        if (condition) {
            return notEq(column, data);
        }
        return this.child;
    }

    /**
     * 大于
     *
     * @param column 字段名
     * @param data   值
     * @return 返回子类
     */

    public Children gt(B column, Object data) {
        if (column != null) {
            addQuery(SqlKey.GT, column, data);
        }
        return this.child;
    }

    /**
     * 大于
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @param data      值
     * @return 返回子类
     */

    public Children gt(boolean condition, B column, Object data) {
        if (condition) {
            return gt(column, data);
        }
        return this.child;
    }

    /**
     * 大于等于
     *
     * @param column 字段名
     * @param data   值
     * @return 返回子类
     */

    public Children gtEq(B column, Object data) {
        if (column != null) {
            addQuery(SqlKey.GE, column, data);
        }
        return this.child;
    }

    /**
     * 大于等于
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @param data      值
     * @return 返回子类
     */

    public Children gtEq(boolean condition, B column, Object data) {
        if (condition) {
            return gtEq(column, data);
        }
        return this.child;
    }

    /**
     * between .. and..
     *
     * @param column 字段名
     * @param data1  值1
     * @param data2  值2
     * @return 返回子类
     */

    public Children between(B column, Object data1, Object data2) {
        if (column != null) {
            addQuery(SqlKey.BETWEEN, column, data1, data2);
        }
        return this.child;
    }

    /**
     * between .. and..
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @param data1     值1
     * @param data2     值2
     * @return 返回子类
     */

    public Children between(boolean condition, B column, Object data1, Object data2) {
        if (condition) {
            return between(column, data1, data2);
        }
        return this.child;
    }

    /**
     * left like '%aa'
     *
     * @param column 字段名
     * @param data   值1
     * @return 返回子类
     */
    public Children leftLike(B column, Object data) {
        if (column != null) {
            addQuery(SqlKey.LEFTLIKE, column, data);
        }
        return this.child;
    }

    /**
     * left like '%aa'
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @param data      值1
     * @return 返回子类
     */
    public Children leftLike(boolean condition, B column, Object data) {
        if (condition) {
            return leftLike(column, data);
        }
        return this.child;
    }

    /**
     * right like 'aa%'
     *
     * @param column 字段名
     * @param data   值1
     * @return 返回子类
     */
    public Children rightLike(B column, Object data) {
        if (column != null) {
            addQuery(SqlKey.RIGHTLIKE, column, data);
        }
        return this.child;
    }

    /**
     * right like 'aa%'
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @param data      值1
     * @return 返回子类
     */
    public Children rightLike(boolean condition, B column, Object data) {
        if (condition) {
            return rightLike(column, data);
        }
        return this.child;
    }

    /**
     * like '%aa%'
     *
     * @param column 字段名
     * @param data   值1
     * @return 返回子类
     */
    public Children like(B column, Object data) {
        if (column != null) {
            addQuery(SqlKey.LIKE, column, data);
        }
        return this.child;
    }

    /**
     * like '%aa%'
     *
     * @param condition 判断条件 为 true 则 应用。fasle 则不使用
     * @param column    字段名
     * @param data      值1
     * @return 返回子类
     */
    public Children like(boolean condition, B column, Object data) {
        if (condition) {
            return like(column, data);
        }
        return this.child;
    }

    /**
     * AND
     *
     * @return 返回子类
     */
    public Children and() {
        where.add(new QueryAndOrConditions(SqlKey.AND));
        return this.child;
    }

    /**
     * AND， 多语句
     *
     * @param c 子类
     * @return 返回子类
     */
    public Children and(Children c) {
        where.add(new QueryAndOrConditions(SqlKey.AND, c.getWhere()));
        return this.child;
    }

    /**
     * AND 多语句
     *
     * @param condition 条件
     * @param c         子类
     * @return 返回子类
     */
    public Children and(boolean condition, Children c) {
        if (condition) {
            return and(c);
        }
        return this.child;
    }

    /**
     * OR
     *
     * @return 返回子类
     */
    public Children or() {
        where.add(new QueryAndOrConditions(SqlKey.OR));
        return this.child;
    }

    /**
     * OR 多语句
     *
     * @param c 子类
     * @return 返回子类
     */
    public Children or(Children c) {
        where.add(new QueryAndOrConditions(SqlKey.OR, c.getWhere()));
        return this.child;
    }

    /**
     * OR 多语句
     *
     * @param condition 条件
     * @param c         子类
     * @return 返回子类
     */
    public Children or(boolean condition, Children c) {
        if (condition) {
            return or(c);
        }
        return this.child;
    }

    /**
     * group by
     *
     * @param column 字段数组
     * @return 返回子类
     */
    public Children groupBy(B... column) {
        where.add(new GroupByConditions(SqlKey.GROUP_BY, columnToString(column)));
        return this.child;
    }

    /**
     * group by
     *
     * @param condition 条件
     * @param column    字段数组
     * @return 返回子类
     */
    public Children groupBy(boolean condition, B... column) {
        if (condition) {
            return groupBy(column);
        }
        return this.child;
    }

    /**
     * having
     *
     * @param c 字段
     * @return 返回子类
     */
    public Children having(Children c) {
        where.add(new QueryAndOrConditions(SqlKey.HAVING, c.getWhere()));
        return this.child;
    }

    /**
     * having
     *
     * @param condition 条件 是否真
     * @param c         字段
     * @return 返回子类
     */
    public Children having(boolean condition, Children c) {
        if (condition) {
            return having(c);
        }
        return this.child;
    }

    /**
     * order by desc
     *
     * @param column 字段
     * @return 返回子类
     */
    public Children orderByDesc(B column) {
        if (orderBy == null) {
            orderBy = new ArrayList<>();
            where.add(new OrderByConditions(SqlKey.ORDER_BY, orderBy));
        }
        orderBy.add(new QueryConditions(SqlKey.DESC, columnToString(column)));
        return this.child;
    }

    /**
     * order by desc
     *
     * @param condition 条件 是否真
     * @param column    字段
     * @return 返回子类
     */
    public Children orderByDesc(boolean condition, B column) {
        if (condition) {
            return orderByDesc(column);
        }
        return this.child;
    }

    /**
     * order by  asc
     *
     * @param column 字段
     * @return 返回子类
     */
    public Children orderByAsc(B column) {
        if (orderBy == null) {
            orderBy = new ArrayList<>();
            where.add(new OrderByConditions(SqlKey.ORDER_BY, orderBy));
        }
        orderBy.add(new QueryConditions(SqlKey.ASC, columnToString(column)));
        return this.child;
    }

    /**
     * order by asc
     *
     * @param condition 条件 是否真
     * @param column    字段
     * @return 返回子类
     */
    public Children orderByAsc(boolean condition, B column) {
        if (condition) {
            return orderByAsc(column);
        }
        return this.child;
    }

    /**
     * 获取 where sql 不会生成where字段
     *
     * @return SQL
     */
    private String toSqlString() {
        StringBuffer stringBuffer = new StringBuffer();
        int lastOr = -1;
        for (int i = 0; i < where.size(); i++) {
            QueryConditions qc = where.get(i);
            if (i == 0 && where.get(i).getSqlKey() != SqlKey.GROUP_BY) {
                stringBuffer.append(" WHERE ");
            }
            if (!(qc instanceof QueryAndOrConditions) && !(qc instanceof OrderByConditions) && !(qc instanceof GroupByConditions)) {
                if (i > 0 && (i - 1 != lastOr)) {
                    stringBuffer.append(" AND ");
                }
            } else {
                lastOr = i;
            }
            stringBuffer.append(qc.toSqlString());
        }
        return stringBuffer.toString();
    }

    /**
     * 生成sql
     *
     * @return 返回sql
     */
    public String toSql() {
        return toSqlString();
    }
}
