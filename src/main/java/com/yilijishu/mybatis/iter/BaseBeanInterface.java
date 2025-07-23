package com.yilijishu.mybatis.iter;

public interface BaseBeanInterface {

    /**
     * 生成的表名  table_name
     * @return table_name
     */
    default String baseGenTable() {
        return "";
    }

    /**
     * 生成的全部列名 逗号分割  base_1, base_2
     * @return base_1, base_2
     */
    default String baseGenColumnNames() {
        return "";
    }


    /**
     * 生成的插入列名 逗号分割 base_1, base_2
     * @return base_1, base_2
     */
    default String baseGenInertColumnNames() {
        return "";
    }

    /**
     * 生成的插入Name 逗号分隔   base1, base2
     * @return base1, base2
     */
    default String baseGenInsertNames() {
        return "";
    }

    /**
     * 生成的插入列表Name  逗号分隔 list[0,1,2].name
     * @return 逗号分隔 list[0,1,2].name
     */
    default String baseGenInsertListNames() {
        return "";
    }

    /**
     * 生成的默认where条件， where 1=1
     * @return where 1=1
     */
    default String baseGenDefWhere() {
        return "";
    }


    /**
     * 生成的主键列名， 主键列名。
     * @return 主键列名
     */
    default String baseGenId() {
        return "";
    }

    /**
     * 生成的修改全部的set语法
     * @return 生成的修改全部的set语法
     */
    default String baseGenUpdateAllSet() {
        return "";
    }

    /**
     * 生成的查询where
     * @return 生成的查询where
     */
    default String baseGenSelectWhere() {
        return "";
    }

    /**
     * 生成的修改set
     * @return 生成的修改set
     */
    default String baseGenUpdateSet() {
        return "";
    }

    /**
     * 生成的修改where条件
     * @return 生成的修改where条件
     */
    default String baseGenUpdateWhere() {
        return "";
    }

    /**
     * 生成的order by
     * @return 生成的order by
     */
    default String baseGenOrderBy() {
        return " where 1=1 ";
    }

    /**
     * 生成的database 如：MYSQL ORACLE POSTGRESQL
     * @return 生成的database 如：MYSQL ORACLE POSTGRESQL
     */
    default String baseSqlDatabase() {
        return "";
    }

    /**
     * 生成的创建Table脚本 create table xxx ();
     * @return 生成的创建Table脚本 create table xxx ();
     */
    default String baseCreateTable() {
        return "";
    }

    /**
     * 生成的虚拟ID，列名
     * @return 生成的虚拟ID，列名
     */
    default String baseGenVirtualId() {
        return "";
    }

    /**
     * 生成逻辑删除位 true false
     * @return 生成逻辑删除位 true false
     */
    default boolean genDelTag() {
        return false;
    }

    /**
     * 生成逻辑删除属性 base_1
     * @return 生成逻辑删除属性 base_1
     */
    default String genDelTagColumn() {
        return "";
    }

    /**
     * 生成删除标记值 1
     * @return 生成删除标记值 1
     */
    default String genDelTagValue() {
        return "";
    }

}
