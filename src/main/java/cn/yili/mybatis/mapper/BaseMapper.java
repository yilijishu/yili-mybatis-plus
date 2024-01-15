package cn.yili.mybatis.mapper;

import cn.yili.mybatis.entity.Page;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;

@Mapper
public interface BaseMapper<T> {

    String PAGE_BASE = "      <if test=\"page != null\">" +
            "        limit ${page.start}, ${page.pageSize}" +
            "      </if>";
    String PARAM_PAGE = "page";
    String LIMIT_1 = " limit 1";
    String PARAM_OBJECT = "p";

    //--------------------------------------------------------- 标准产物.
    default void defaultMethod() {

    }

    @Insert("insert into ${p.genTable} (${p.genColumnNames}) values (${p.genNames})")
    @ResultType(Integer.class)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(@Param(PARAM_OBJECT) T p);


    @Update("<script>" +
            "update ${p.genTable}" +
            "     <set>" +
            " ${p.genUpdateSet} " +
            "    </set>" +
            "    ${p.genDefWhere} ${p.genUpdateWhere} " +
            "</script>")
    @ResultType(Integer.class)
    Integer update(@Param(PARAM_OBJECT) T p);

    @Insert("<script>" +
            "insert into ${p.genTable} (${p.genColumnNames})" +
            "    values " +
            " <foreach collection=\"list\" item=\"" + PARAM_OBJECT + " \" separator=\",\">" +
            "(  ${p.genNames}  )" +
            "</foreach></script>")
    @ResultType(Integer.class)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertAll(@Param("list") Collection<T> p);

    @Select("<script>" +
            "select ${p.genColumnNames} from ${p.genTable} ${p.genDefWhere} ${p.genSelectWhere} ${p.genOrderBy}" +
            PAGE_BASE +
            "</script>")
    List<T> select(@Param(PARAM_OBJECT) T p, @Param(PARAM_PAGE) Page page);

    @Select("<script>" +
            "select ${p.genColumnNames} from ${p.genTable} ${p.genDefWhere} ${p.genSelectWhere} " +
            LIMIT_1 +
            "</script>")
    T get(@Param(PARAM_OBJECT) T p);


    @Select("<script>" +
            "select count(1) from ${p.genTable} ${p.genDefWhere} ${p.genSelectWhere}" +
            "</script>")
    @ResultType(Integer.class)
    Integer count(@Param(PARAM_OBJECT) T p);
}
