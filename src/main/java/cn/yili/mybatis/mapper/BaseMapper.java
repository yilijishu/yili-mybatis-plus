package cn.yili.mybatis.mapper;

import cn.yili.mybatis.entity.BaseEntity;
import cn.yili.mybatis.entity.Page;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;
@Mapper
public interface BaseMapper<T extends BaseEntity> {

    //--------------------------------------------------------- 标准产物.
    default void defaultMethod() {

    }

    String PAGE_BASE = "      <if test=\"page != null\">" +
            "        limit #{page.start}, #{page.pageSize}" +
            "      </if>";
    String PARAM_PAGE = "page";

    String LIMIT_1 = " limit 1";

    @Insert("insert into #{genTable()} (#{genColumnNames()}) values (#{genNames()})")
    @ResultType(Integer.class)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(T p);


    @Update("<script>" +
            "update #{genTable()}" +
            "     <set>" +
            " #{genUpdateSet()} " +
            "    </set>" +
            "    #{genDefWhere} #{genUpdateWhere()} " +
            "</script>")
    @ResultType(Integer.class)
    Integer update(T p);

    @Insert("<script>" +
            "insert into #{genTable()} (#{genColumnNames()})" +
            "    values " +
            " <foreach collection=\"list\" separator=\",\">" +
            "(  #{genNames()}  )" +
            "</foreach></script>")
    @ResultType(Integer.class)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertAll(@Param("list") Collection<T> p);

    @Select("<script>" +
            "select #{genColumnNames()} from #{genTable()} #{genDefWhere()} #{genSelectWhere()} #{genOrderBy()}" +
            PAGE_BASE +
            "</script>")
    List<T> select(T p, @Param(PARAM_PAGE) Page page);

    @Select("<script>" +
            "select #{genColumnNames()} from #{genTable()} #{genDefWhere()} #{genSelectWhere()} " +
            LIMIT_1 +
            "</script>")
    T get(T p);


    @Select("<script>" +
            "select count(1) from #{genTable()} #{genDefWhere()} #{genSelectWhere()}" +
            "</script>")
    @ResultType(Integer.class)
    Integer count(T p);
}
