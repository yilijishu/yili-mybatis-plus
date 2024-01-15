package cn.yili.mybatis.mapper;

import cn.yili.mybatis.entity.Page;
import lombok.SneakyThrows;
import org.apache.ibatis.annotations.*;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    @InsertProvider(method = "insert", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    <T> Integer insert(@Param(PARAM_OBJECT) T p);


    @UpdateProvider(method = "update", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer update(@Param(PARAM_OBJECT) T p);

    @InsertProvider(method = "insertAll", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertAll(@Param("list") Collection<T> p);

    @SelectProvider(method = "select", type = BaseSqlProvider.class)
    List<T> select(@Param(PARAM_OBJECT) T p, @Param(PARAM_PAGE) Page page);

    @SelectProvider(method = "get", type = BaseSqlProvider.class)
    T get(@Param(PARAM_OBJECT) T p);


    @SelectProvider(method = "count", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer count(@Param(PARAM_OBJECT) T p);


    class BaseSqlProvider {
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
                sbf.append("        limit ${page.start}, ${page.pageSize}");
            }
            return sbf.toString();
        }


        @SneakyThrows
        public String count(Map<String, Object> map) {
            Object obj = map.get("p");
            StringBuffer sbf = new StringBuffer();
            sbf.append("select ");
            sbf.append("count(1)");
            sbf.append(" from ");
            sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
            sbf.append(obj.getClass().getMethod("baseGenDefWhere").invoke(obj));
            sbf.append(obj.getClass().getMethod("baseGenSelectWhere").invoke(obj));

            return sbf.toString();
        }

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
            sbf.append(" limit 1");

            return sbf.toString();
        }

        /**
         * insert
         *
         * @param map
         * @return
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
                    sbf.append(obj.getClass().getMethod("baseGenColumnNames").invoke(obj));
                    sbf.append(") values ");
                    MessageFormat messageFormat = new MessageFormat((String) obj.getClass().getMethod("baseGenListNames").invoke(obj));
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

        @SneakyThrows
        public String insert(Map<String, Object> map) {

            Object obj = map.get("p");
            StringBuffer sbf = new StringBuffer();
            sbf.append("insert into ");
            sbf.append(obj.getClass().getMethod("baseGenTable").invoke(obj));
            sbf.append(" (");
            sbf.append(obj.getClass().getMethod("baseGenColumnNames").invoke(obj));
            sbf.append(") values (");
            sbf.append(obj.getClass().getMethod("baseGenNames").invoke(obj));
            sbf.append(")");

            return sbf.toString();
        }

        @SneakyThrows
        public String update(Map<String, Object> map) {
            Object obj = map.get("p");
            StringBuffer sbf = new StringBuffer();
            sbf.append("update ");
            sbf.append(" set ");
            sbf.append(obj.getClass().getMethod("baseGenUpdateSet").invoke(obj));
            sbf.append(obj.getClass().getMethod("baseGenDefWhere").invoke(obj));
            sbf.append(obj.getClass().getMethod("baseGenUpdateWhere").invoke(obj));

            return sbf.toString();
        }
    }
}
