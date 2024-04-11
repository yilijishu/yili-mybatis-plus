package cn.yili.mybatis.mapper;

import cn.yili.mybatis.entity.Page;
import cn.yili.mybatis.wapper.DeleteSql;
import cn.yili.mybatis.wapper.QuerySql;
import cn.yili.mybatis.wapper.UpdateSql;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
public interface BaseMapper<T> {

    String PARAM_PAGE = "page";
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

    @UpdateProvider(method = "updateNotIfNull", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer updateNotIfNull(@Param(PARAM_OBJECT) T p);


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

    @SelectProvider(method = "querySql", type = BaseSqlProvider.class)
    List<T> querySql(@Param(PARAM_OBJECT) QuerySql<T> querySql);

    @SelectProvider(method = "querySql", type = BaseSqlProvider.class)
    <R> R querySqlResultR(@Param(PARAM_OBJECT) QuerySql<T> querySql);

    @SelectProvider(method = "querySqlOne", type = BaseSqlProvider.class)
    T querySqlOne(@Param(PARAM_OBJECT) QuerySql<T> querySql);

    @SelectProvider(method = "querySqlOne", type = BaseSqlProvider.class)
    <R> R querySqlResultROne(@Param(PARAM_OBJECT) QuerySql<T> querySql);

    @UpdateProvider(method = "updateSql", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer updateSql(UpdateSql<T> updateSql);


    @UpdateProvider(method = "deleteByIds", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer deleteByIds(@Param(PARAM_OBJECT) T t, @Param("ids") Collection<?> ids);

    @UpdateProvider(method = "deleteById", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer deleteById(@Param(PARAM_OBJECT) T t, @Param("id") Object id);


    @UpdateProvider(method = "delete", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer delete(@Param(PARAM_OBJECT) DeleteSql<T> deleteSql);

    class BaseSqlProvider {

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

        public String delete(Map<String, Object> map) {
            Object obj = map.get("p");
            if (obj instanceof DeleteSql) {
                DeleteSql<?> deleteSql = (DeleteSql<?>) obj;
                return deleteSql.toSql();
            }
            throw new RuntimeException("使用了无效的标准组件");
        }

        public String querySql(Map<String, Object> map) {
            Object obj = map.get("p");
            if (obj instanceof QuerySql) {
                QuerySql<?> querySql = (QuerySql<?>) obj;
                return querySql.toSql();
            }
            throw new RuntimeException("使用了无效的标准组件");
        }

        public String querySqlOne(Map<String, Object> map) {
            Object obj = map.get("p");
            if (obj instanceof QuerySql) {
                QuerySql<?> querySql = (QuerySql<?>) obj;
                return querySql.toSqlOne();
            }
            throw new RuntimeException("使用了无效的标准组件");
        }

        public <T> String updateSql(UpdateSql<T> updateSql) {
            return updateSql.toSql();
        }

        //----基础方法.
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

    }
}
