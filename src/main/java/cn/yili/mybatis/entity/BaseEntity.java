package cn.yili.mybatis.entity;

import cn.yili.mybatis.ann.*;
import cn.yili.mybatis.constant.Constant;
import cn.yili.mybatis.util.CamelUnderUtil;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BaseEntity {


    /**
     * 主键ID
     */
    @TableId
    @OrderBy(order = 999)
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 是否删除
     */
    @DefWhere("0")
    private Boolean delTag;

    /**
     * 渠道
     */
    private String channelCode;

    /**
     * 应用
     */
    private String program;

    /**
     * 创建人系统用户ID
     */
    private Long createUserId;

    /**
     * 平台编号
     */
    private String platformNumber;
    /**
     * 修改人系统用户ID
     */
    private Long modifyUserId;

    // 查询字段
    /**
     * 批量查询ID字段
     */
    @IgnoreColumn
    private List<Long> ids;

    /**
     * 查询全部
     */
    @IgnoreColumn
    private Boolean all = false;

    public Boolean getAll() {
        return all != null && all;
    }

    public String getOrderBy() {
        List<Field> fields = getFieldByAnns(OrderBy.class);
        fields.sort((a,b)-> {
            OrderBy a1 = a.getAnnotation(OrderBy.class);
            OrderBy b1 = b.getAnnotation(OrderBy.class);
            return a1.order() - b1.order();
        });
        StringBuffer sbf = new StringBuffer();
        sbf.append("order by ");
        fields.forEach(e->{
            Column column = e.getAnnotation(Column.class);
            sbf.append((column!=null?column.value():CamelUnderUtil.underName(e.getName())) + " " + e.getAnnotation(OrderBy.class).value() + ",");
        });

        return  sbf.toString().substring(0, sbf.length() -1 );
    }

    /**
     * 获取where条件、
     * @return
     */
    public String getSelectWhere() {
        return getContition(Constant.Oper.WHERE, false);
    }

    /**
     * 获取默认的where条件
     * @return
     */
    public String getSelectDefaultWhere() {
        String def = " where 1=1 ";
        Field defWhere = getFieldByAnn(DefWhere.class);
        Annotation annotation = defWhere.getAnnotation(DefWhere.class);
        Annotation column = defWhere.getAnnotation(Column.class);
        if(annotation != null ) {
            DefWhere d = (DefWhere)annotation;
            if(column != null) {
                Column c = (Column) column;
                def = " where " + c.value() + " = " + d.value();
            } else {
                def = " where " + CamelUnderUtil.underName(defWhere.getName()) + " = " + d.value();
            }
        }
        return def;
    }



    /**
     * 获取表名
     *
     * @return
     */
    public String getTable() {
        Annotation annotation = this.getClass().getAnnotation(Table.class);
        if (annotation != null) {
            return "`" + ((Table) annotation).value() + "`";
        } else {
            return "`" + CamelUnderUtil.underName(this.getClass().getSimpleName()) + "`";
        }
    }


    /**
     * 获取修改set语句
     *
     * @return
     */
    public String getUpdateIsNull() {
        return getContition(Constant.Oper.UPDATE, true);
    }

    /**
     * 获取update
     *
     * @return
     */
    public String getUpdateWhereId() {
        Field field = getFieldByAnn(TableId.class);
        Annotation annotation = field.getAnnotation(Column.class);
        return (annotation != null ? ((Column) annotation).value() : CamelUnderUtil.underName(field.getName())) + " = #{" + field.getName() + "}";
    }



    /**
     * 获取条件
     *
     * @return
     */
    public String getContition(Constant.Oper operation, Boolean exclude) {
        List<Field> all = getFieldsNames(exclude, true);
        StringBuffer stringBuffer = new StringBuffer();
        all.forEach(e -> {
            if(!(e.isAnnotationPresent(DefWhere.class) && Constant.Oper.WHERE == operation)) {
                stringBuffer.append(Constant.IFNULL_TEMPLATE.replace("{CONDITION}", e.getName() + " != null "));
                Annotation annotation = e.getAnnotation(Column.class);
                String condition = " `" + (annotation != null ? ((Column) annotation).value() : CamelUnderUtil.underName(e.getName())) + " = #{" + e.getName() + "} ";
                stringBuffer.append(Constant.IFNULL_TEMPLATE.replace("{WHERE}",
                        (operation == Constant.Oper.WHERE ? " and " : " ") + condition + (operation == Constant.Oper.WHERE ? " " : " , ")));
            }
        });
        return stringBuffer.toString();
    }

    /**
     * 获取所有数据库字段名
     *
     * @return
     */
    public String getAllColumnName() {
        StringBuffer sbf = new StringBuffer();
        List<Field> all = getFieldsNames(true, true);
        for (Field field : all) {
            sbf.append("`");
            Annotation annotation = field.getAnnotation(Column.class);
            sbf.append(annotation != null ? ((Column) annotation).value() : CamelUnderUtil.underName(field.getName()));
            sbf.append("`");
            sbf.append(" ,");
        }
        String result = sbf.toString();
        return result.substring(0, result.length() - 1);
    }

    /**
     * 获取所有属性名称
     *
     * @return
     */
    public String getAllName() {
        StringBuffer sbf = new StringBuffer();
        List<Field> all = getFieldsNames(true, true);
        for (Field field : all) {
            sbf.append("#{");
            sbf.append(field.getName());
            sbf.append("} ,");

        }
        String result = sbf.toString();
        return result.substring(0, result.length() - 1);
    }

    private <T extends Annotation> List<Field> getFieldByAnns(Class<T> tClass) {
        List<Field> fields = getFieldsNames(false, false);
        List<Field> result = new ArrayList<>();
        if (fields != null) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(tClass)) {
                    result.add(field);
                }
            }
        }
        return result;
    }

    /**
     * 根据注解类获取属性字段
     *
     * @return
     */
    private <T extends Annotation> Field getFieldByAnn(Class<T> tClass) {
        List<Field> fields = getFieldsNames(false, true);
        if (fields != null) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(tClass)) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * 获取所有私有的字段，
     *
     * @param exclude 去掉主键ID
     * @return
     */
    private List<Field> getFieldsNames(Boolean exclude, Boolean ignore) {
        Field[] fields = this.getClass().getDeclaredFields();
        Field[] supFields = this.getClass().getSuperclass().getDeclaredFields();
        List<Field> result = getFieldsNames(supFields, exclude, ignore);
        result.addAll(getFieldsNames(fields, exclude, ignore));
        return result;
    }

    /**
     * 获取所有私有的字段，
     *
     * @param fields  所有的属性字段.
     * @param exclude 去掉主键ID
     * @return
     */
    private List<Field> getFieldsNames(Field[] fields, Boolean exclude, Boolean ignore) {
        List<Field> result = new ArrayList<>();
        for (Field field : fields) {
            if(ignore) {
                IgnoreColumn ignoreColumn = field.getAnnotation(IgnoreColumn.class);
                if (ignoreColumn != null) {
                    continue;
                }
            }
            if(exclude) {
                TableId id = field.getAnnotation(TableId.class);
                if (id != null) {
                    continue;
                }
            }
            result.add(field);
        }
        return result;
    }

    public String toJsonString() {
        Method[] methods = this.getClass().getMethods();
        boolean exec = false;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        int index = 0;
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("get") && !"getClass".equals(name)) {
                index++;
                stringBuilder.append("\"");
                name = name.substring(3);
                stringBuilder.append(name.substring(0, 1).toLowerCase() + name.substring(1));
                stringBuilder.append("\":");

                switch (method.getReturnType().getName()) {
                    case "java.lang.Long":
                    case "Long": {
                        stringBuilder.append("123219");
                        break;
                    }
                    case "java.time.LocalDateTime":
                    case "LocalDateTime": {
                        stringBuilder.append("\"");
                        stringBuilder.append("2012-09-12 19:01:03");
                        stringBuilder.append("\"");
                        break;
                    }
                    case "java.lang.Boolean":
                    case "Boolean": {
                        stringBuilder.append(false);
                        break;
                    }
                    case "java.lang.Integer":
                    case "Integer": {
                        stringBuilder.append("103");
                        break;
                    }
                    case "Float":
                    case "java.lang.Float":
                    case "Double":
                    case "java.lang.Double": {
                        stringBuilder.append(182.92);
                        break;
                    }
                    case "java.math.BigDecimal":
                    case "BigDecimal": {
                        stringBuilder.append(10.90);
                        break;
                    }
                    case "java.lang.String":
                    case "String":
                    default: {
                        stringBuilder.append("\"");
                        stringBuilder.append("字符串");
                        stringBuilder.append("\"");
                        break;
                    }
                }
                stringBuilder.append(",");
            }
        }
        String result = stringBuilder.toString();
        result = result.substring(0, result.length() - 1) + "}";
        return result;
    }


}
