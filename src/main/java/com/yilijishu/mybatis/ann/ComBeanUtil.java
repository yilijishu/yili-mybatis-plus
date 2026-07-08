package com.yilijishu.mybatis.ann;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Pair;
import com.yilijishu.mybatis.constant.Constant;
import com.yilijishu.mybatis.entity.ComBean;
import com.yilijishu.mybatis.entity.ComMethod;
import com.yilijishu.mybatis.util.CamelUnderUtil;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;

public class ComBeanUtil {

    private static final String PARAM_OBJECT = "p.";

    private static final String PARAM_LIST = "";

    private static final String STR_HANDLER = ", typeHandler=";

    private Messager messager;

    private ProcessingEnvironment processingEnv;

    public ComBeanUtil(Messager messager, ProcessingEnvironment processingEnv) {
        this.messager = messager;
        this.processingEnv = processingEnv;
    }


    public static String getColumnName(ComBean comBean) {
        return comBean.isColumn() ? comBean.getColumValue() : CamelUnderUtil.underName(comBean.getName());
    }

    public static String getName(ComBean comBean) {
        return comBean.getName();
    }

    public void createBuffer(String columnName, ComBean comBean, StringBuffer createBuffer) {
        createBuffer.append(Constant.SPACE.concat(columnName));
        createBuffer.append(Constant.SPACE);
        createBuffer.append(comBean.getColumnType() != null && !"".equals(comBean.getColumnType()) ? comBean.getColumnType() : SqlTypeUtil.getSqlType(comBean.getDefTypeColumn()));
        createBuffer.append(Constant.SPACE);
        createBuffer.append(SqlTypeUtil.getTableIdSqlType(comBean.isTableId()));
        createBuffer.append(" ,");
        if (comBean.getNotNull() || comBean.isVirtualTableId()) {
            createBuffer.append(" not null");
        }
    }

    public java.util.List<JCTree.JCMethodDecl> analyser(java.util.List<ComBean> comBeans, TreeMaker treeMaker, Names names, Symtab symtab, Element element, Table table, ComMethod comMethod) {
        messager.printMessage(Diagnostic.Kind.WARNING, "执行buildMethods" + table.value() + Constant.escape(CamelUnderUtil.underName(element.getSimpleName().toString())));
        JCUtils jcUtils = new JCUtils(treeMaker, names, symtab);
        messager.printMessage(Diagnostic.Kind.WARNING, "new" + table.value() + Constant.escape(CamelUnderUtil.underName(element.getSimpleName().toString())));
        //生成的表名  table_name
        StringBuffer baseGenTable = new StringBuffer();
        if (table.value() != null && !"".equals(table.value())) {
            baseGenTable.append(Constant.escape(table.value()));
        } else {
            baseGenTable.append(Constant.escape(CamelUnderUtil.underName(element.getSimpleName().toString())));
        }
        //生成的全部列名 逗号分割  base_1, base_2
        StringBuffer baseGenColumnNames = new StringBuffer();
        //生成的插入列名
        ListBuffer<JCTree.JCStatement> baseGenInertColumnNames = new ListBuffer<>();
        baseGenInertColumnNames.append(jcUtils.initResult());
        baseGenInertColumnNames.append(jcUtils.applyMethod("result", "append", " "));
        //生成的插入Name
        ListBuffer<JCTree.JCStatement> baseGenInsertNames = new ListBuffer<>();
        baseGenInsertNames.append(jcUtils.initResult());
        baseGenInsertNames.append(jcUtils.applyMethod("result", "append", " "));
        //生成的插入列表Name
        ListBuffer<JCTree.JCStatement> baseGenInsertListNames = new ListBuffer<>();
        baseGenInsertListNames.append(jcUtils.initResult());
        baseGenInsertListNames.append(jcUtils.applyMethod("result", "append", " "));
        //生成的主键列名， 主键列名
        StringBuffer baseGenId = new StringBuffer();
        //生成的修改全部的set语法
        StringBuffer baseGenUpdateAllSet = new StringBuffer();
        //生成的查询where
        ListBuffer<JCTree.JCStatement> baseGenSelectWhere = new ListBuffer<>();
        baseGenSelectWhere.append(jcUtils.initResult());
        baseGenSelectWhere.append(jcUtils.applyMethod("result", "append", "   "));
        //生成的修改set
        ListBuffer<JCTree.JCStatement> baseGenUpdateSet = new ListBuffer<>();
        baseGenUpdateSet.append(jcUtils.initResult());
        baseGenUpdateSet.append(jcUtils.applyMethod("result", "append", " "));
        //生成的修改where条件
        ListBuffer<JCTree.JCStatement> baseGenUpdateWhere = new ListBuffer<>();
        baseGenUpdateWhere.append(jcUtils.initResult());
        baseGenUpdateWhere.append(jcUtils.applyMethod("result", "append", " "));
        //生成的order by
        StringBuffer baseGenOrderBy = new StringBuffer();
        java.util.List<ComBean> orderByList = new ArrayList<>();
        //生成的database 如：MYSQL ORACLE POSTGRESQL
        StringBuffer baseSqlDatabase = new StringBuffer();
        baseSqlDatabase.append(Constant.dataBase.name());
        //生成的创建Table脚本 create table
        StringBuffer baseCreateTable = new StringBuffer();
        //生成的虚拟ID，列名
        StringBuffer baseGenVirtualId = new StringBuffer();
        //生成逻辑删除位 true false
        boolean genDelTag = false;
        //生成逻辑删除属性 base_1
        StringBuffer genDelTagColumn = new StringBuffer();
        //生成删除标记值 1
        StringBuffer genDelTagValue = new StringBuffer();
        //ID
        String tableIdVal = null;
        //ID
        String tableIdColumnVal = null;
        //虚拟ID
        String virtualTableId = null;
        //虚拟ID
        String virtualTableIdColumn = null;


        for (int jj = 0; jj < comBeans.size(); jj++) {
            ComBean comBean = comBeans.get(jj);
            //忽略字段 跳过
            if (comBean.isIgnore()) {
                continue;
            }
            String columnName = getColumnName(comBean);
            String name = getName(comBean);
            baseGenColumnNames.append(",".concat(Constant.escape(columnName)));
            //家在create
            createBuffer(columnName, comBean, baseCreateTable);
            //非忽略insert的逻辑 START
            if (!comBean.isIgnoreInsert()) {
                //判断是否为默认创建时间
                if (!comBean.isAutoCreateTime()) {
                    baseGenInertColumnNames.append(jcUtils.forExec(
                            "get".concat(CamelUnderUtil.nameUpper(name)),
                            Constant.escape(columnName).concat(" ,")
                    ));
                    baseGenInsertNames.append(jcUtils.forExec(
                            "get".concat(CamelUnderUtil.nameUpper(name)),
                            "#{".concat(PARAM_OBJECT).concat(name).concat(comBean.isHandler() ? STR_HANDLER.concat(comBean.getHandlerStr()) : "").concat("} ,")
                    ));
                    baseGenInsertListNames.append(jcUtils.forExec(
                            "get".concat(CamelUnderUtil.nameUpper(name)),
                            "#'{'list[{0}].".concat(name).concat(comBean.isHandler() ? STR_HANDLER.concat(comBean.getHandlerStr()) : "").concat("} ,")
                    ));
                } else {
                    baseGenInsertNames.append(jcUtils.forExec(
                            "get".concat(CamelUnderUtil.nameUpper(name)),
                            Constant.escapeDate().concat(" ,")
                    ));
                    baseGenInsertListNames.append(jcUtils.forExec(
                            "get".concat(CamelUnderUtil.nameUpper(name)),
                            Constant.escapeDate().concat(" ,")
                    ));
                }
            }
            //非忽略insert的逻辑 END
            if (comBean.isTableId()) {
                //设定主键ID
                baseGenId.append(Constant.escape(columnName));
                tableIdVal = name;
                tableIdColumnVal = columnName;
            } else if (comBean.isVirtualTableId()) {
                //设定虚拟ID
                virtualTableId = name;
                virtualTableIdColumn = columnName;
                baseGenVirtualId.append(Constant.escape(columnName));
            } else {
                if (!comBean.isAutoCreateTime()) {
                    baseGenUpdateAllSet.append(Constant.escape(columnName) + " = #{" + name + "} ,");
                    if (!comBean.isAutoModifyTime()) {
                        baseGenUpdateSet.append(jcUtils.forExec(
                                "get".concat(CamelUnderUtil.nameUpper(name)),
                                "  ".concat(Constant.escape(columnName)).concat(" = #{").concat(PARAM_OBJECT).concat(name).concat("} ,")
                        ));
                    } else {
                        baseGenUpdateSet.append(jcUtils.applyMethod("result", "append", "  ".concat(Constant.escape(columnName)).concat(" = ").concat(Constant.escapeDate()).concat(" ,")));
                    }
                }
            }
            if (comBean.isDefWhere()) {
                baseGenSelectWhere.append(jcUtils.applyMethod("result", "append", " ".concat(Constant.escape(columnName)).concat(" = #{").concat(PARAM_OBJECT).concat(name).concat("} AND")));
            } else {
                if (comBean.isIfFieldCondition()) {
                    baseGenSelectWhere.append(jcUtils.forExec2(
                            "get".concat(CamelUnderUtil.nameUpper(name)),
                            comBean.getIfFieldConditionName(),
                            " ".concat(Constant.escape(columnName)).concat(" = #{").concat(PARAM_OBJECT).concat(name).concat("} AND")
                    ));
                } else {
                    baseGenSelectWhere.append(jcUtils.forExec(
                            "get".concat(CamelUnderUtil.nameUpper(name)),
                            " ".concat(Constant.escape(columnName)).concat(" = #{").concat(PARAM_OBJECT).concat(name).concat("} AND")
                    ));
                }
            }

            if (comBean.isOrderBy()) {
                orderByList.add(comBean);
            }
            if (comBean.isDelTag()) {
                genDelTag = true;
                genDelTagColumn.append(columnName);
                genDelTagValue.append(comBean.getDelTagValue());

            }
        }

        messager.printMessage(Diagnostic.Kind.WARNING, "开始设置排序" + orderByList);
        if (orderByList != null && orderByList.size() > 0) {
            orderByList.sort((a, b) -> a.getOrder() - b.getOrder());
            baseGenOrderBy.append(" ORDER BY ");
            for (int i = 0; i < orderByList.size(); i++) {
                ComBean comBean = orderByList.get(i);
                baseGenOrderBy.append(Constant.escape(getColumnName(comBean)));
                baseGenOrderBy.append(Constant.SPACE);
                baseGenOrderBy.append(comBean.getOrderByVal());
                if (i + 1 < orderByList.size()) {
                    baseGenOrderBy.append(",");
                }
            }
        }

        if (tableIdVal != null && !"".equals(tableIdVal)) {
            if(virtualTableId != null && !"".equals(virtualTableId)) {
                baseGenUpdateWhere.append(jcUtils.ifApply(jcUtils.getMethodIsNotNull("get".concat(CamelUnderUtil.nameUpper(virtualTableId))),
                        jcUtils.applyMethod("result", "append", "  " + Constant.escape(virtualTableIdColumn) + " = #{" + PARAM_OBJECT + virtualTableId + "} "),
                        jcUtils.applyMethod("result", "append", "  " + Constant.escape(tableIdColumnVal) + " = #{" + PARAM_OBJECT + tableIdVal + "} ")));
            } else {
                baseGenUpdateWhere.append(jcUtils.ifApply(jcUtils.getMethodIsNotNull("get".concat(CamelUnderUtil.nameUpper(tableIdVal))),
                        jcUtils.applyMethod("result", "append", "  " + Constant.escape(tableIdColumnVal) + " = #{" + PARAM_OBJECT + tableIdVal + "} ")));
            }
        }

        //开始添加
        java.util.List<JCTree.JCMethodDecl> results = new ArrayList<>();
        results.add(jcUtils.buildMethod("baseGenTable", baseGenTable.toString()));
        results.add(jcUtils.buildMethod("baseGenColumnNames", baseGenColumnNames.toString().substring(1)));
        baseGenInertColumnNames.append(jcUtils.returnMethod());
        messager.printMessage(Diagnostic.Kind.WARNING, "baseGenInertColumnNames" + baseGenInertColumnNames);
        results.add(jcUtils.buildMethod("baseGenInertColumnNames", baseGenInertColumnNames));
        baseGenInsertNames.append(jcUtils.returnMethod());
        messager.printMessage(Diagnostic.Kind.WARNING, "baseGenInsertNames" + baseGenInertColumnNames);
        results.add(jcUtils.buildMethod("baseGenInsertNames", baseGenInsertNames));
        baseGenInsertListNames.append(jcUtils.returnMethod());
        messager.printMessage(Diagnostic.Kind.WARNING, "baseGenInsertListNames" + baseGenInsertListNames);
        results.add(jcUtils.buildMethod("baseGenInsertListNames", baseGenInsertListNames));
        messager.printMessage(Diagnostic.Kind.WARNING, "baseGenId" + baseGenId);
        results.add(jcUtils.buildMethod("baseGenId", baseGenId.toString()));
        messager.printMessage(Diagnostic.Kind.WARNING, "baseGenUpdateAllSet" + baseGenUpdateAllSet);
        results.add(jcUtils.buildMethod("baseGenUpdateAllSet", baseGenUpdateAllSet.substring(0, baseGenUpdateAllSet.length() - 1)));
        if (comMethod.isAddSelectCondition()) {
            for (String methodName : comMethod.getAddSelectConditionMethod()) {
                baseGenSelectWhere.append(jcUtils.applyMethod("result", "append", "this", methodName));
                baseGenSelectWhere.append(jcUtils.applyMethod("result", "append", " AND"));
            }
        }
        baseGenSelectWhere.append(jcUtils.returnMethod(3));
        messager.printMessage(Diagnostic.Kind.WARNING, "baseGenSelectWhere" + baseGenSelectWhere);
        results.add(jcUtils.buildMethod("baseGenSelectWhere", baseGenSelectWhere));
        baseGenUpdateSet.append(jcUtils.returnMethod());
        messager.printMessage(Diagnostic.Kind.WARNING, "baseGenUpdateSet" + baseGenUpdateSet);
        results.add(jcUtils.buildMethod("baseGenUpdateSet", baseGenUpdateSet));
        baseGenUpdateWhere.append(jcUtils.returnMethod());
        messager.printMessage(Diagnostic.Kind.WARNING, "baseGenUpdateWhere" + baseGenUpdateWhere);
        results.add(jcUtils.buildMethod("baseGenUpdateWhere", baseGenUpdateWhere));
        if (comMethod.isOverrideOrderBy()) {
            ListBuffer<JCTree.JCStatement> orderByStatement = new ListBuffer<>();
            orderByStatement.append(treeMaker.Return(treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString(comMethod.getOverriderOrderByMethod())), List.nil())));
            results.add(jcUtils.buildMethod("baseGenOrderBy", orderByStatement));
        } else {
            results.add(jcUtils.buildMethod("baseGenOrderBy", baseGenOrderBy.toString()));
        }
        results.add(jcUtils.buildMethod("baseSqlDatabase", baseSqlDatabase.toString()));
        results.add(jcUtils.buildMethod("baseCreateTable", baseCreateTable.toString().substring(0, baseCreateTable.length() - 1)));
        results.add(jcUtils.buildMethod("baseGenVirtualId", baseGenVirtualId.toString()));
        results.add(jcUtils.buildMethod("genDelTag", genDelTag));
        if (genDelTag) {
            results.add(jcUtils.buildMethod("genDelTagColumn", genDelTagColumn.toString()));
            results.add(jcUtils.buildMethod("genDelTagValue", genDelTagValue.toString()));
        }
        messager.printMessage(Diagnostic.Kind.WARNING, "生成脚本结束");
        return results;
    }

    /**
     * 解析树。 返回combean list
     *
     * @param supClass           超类
     * @param jcVariableDeclList 定义decl
     * @return list combean
     */
    public java.util.List<ComBean> makeColumnNamesMethodDecl(String supClass, List<JCTree.JCVariableDecl> jcVariableDeclList) {
        messager.printMessage(Diagnostic.Kind.WARNING, "执行makeColumnNamesMethodDecl");
        java.util.List<ComBean> comBeans = null;
        if (jcVariableDeclList != null && jcVariableDeclList.size() > 0) {
            comBeans = new ArrayList<>();
            for (JCTree.JCVariableDecl jcVariableDecl : jcVariableDeclList) {
                ComBean comBean = new ComBean();
                comBeans.add(comBean);
                comBean.setName(jcVariableDecl.getName().toString());
                comBean.setDefTypeColumn(jcVariableDecl.vartype.type.tsym.getQualifiedName().toString());
                // 获取属性名
                for (JCTree.JCAnnotation an : jcVariableDecl.mods.getAnnotations()) {
                    switch (an.getAnnotationType().type.toString()) {
                        case "com.yilijishu.mybatis.ann.DefaultValue": {
                            comBean.setDefaultValueBool(true);
                            for (Pair<Symbol.MethodSymbol, Attribute> a : an.attribute.values) {
                                if ("value()".equals(a.fst.toString())) {
                                    String tmp = a.snd.getValue().toString();
                                    comBean.setDefaultValue(tmp);
                                }
                            }
                            break;
                        }
                        case "com.yilijishu.mybatis.ann.DelTag": {
                            comBean.setDelTag(true);
                            for (Pair<Symbol.MethodSymbol, Attribute> a : an.attribute.values) {
                                if ("value()".equals(a.fst.toString())) {
                                    String tmp = a.snd.getValue().toString();
                                    comBean.setDelTagValue(tmp);
                                }
                            }
                            break;
                        }
                        case "com.yilijishu.mybatis.ann.ColumnType": {
                            for (Pair<Symbol.MethodSymbol, Attribute> a : an.attribute.values) {
                                if ("value()".equals(a.fst.toString())) {
                                    String tmp = a.snd.getValue().toString();
                                    comBean.setColumnType(tmp);
                                }
                            }
                            break;
                        }
                        case "com.yilijishu.mybatis.ann.ColumnNotNull": {
                            comBean.setNotNull(true);
                            break;
                        }
                        case "com.yilijishu.mybatis.ann.Column": {
                            comBean.setColumn(true);
                            for (Pair<Symbol.MethodSymbol, Attribute> a : an.attribute.values) {
                                if ("value()".equals(a.fst.toString())) {
                                    String tmp = a.snd.getValue().toString();
                                    comBean.setColumValue(tmp);
                                } else if ("typeHandler()".equals(a.fst.toString())) {
                                    // JDK9+ : Attribute.Class.getValue() 返回 Type 而非 Class
                                    comBean.setHandler(true);
                                    comBean.setHandlerStr(classNameFromAttributeValue(a.snd.getValue()));
                                }

                            }
                            break;
                        }
                        case "com.yilijishu.mybatis.ann.DefWhere": {
                            comBean.setDefWhere(true);
                            for (Pair<Symbol.MethodSymbol, Attribute> a : an.attribute.values) {
                                if ("value()".equals(a.fst.toString())) {
                                    comBean.setDefWhereValue(a.snd.getValue().toString());
                                }
                            }
                            break;
                        }
                        case "com.yilijishu.mybatis.ann.OrderBy": {
                            comBean.setOrderBy(true);
                            for (Pair<Symbol.MethodSymbol, Attribute> a : an.attribute.values) {
                                if ("value()".equals(a.fst.toString())) {
                                    messager.printMessage(Diagnostic.Kind.WARNING, a.snd.getValue().toString());
                                    comBean.setOrderByVal(a.snd.getValue().toString());
                                } else if ("order()".equals(a.fst.toString())) {
                                    comBean.setOrder(Integer.parseInt(a.snd.getValue().toString()));
                                }
                            }
                            if (comBean.getOrderByVal() == null || "".equals(comBean.getOrderByVal())) {
                                comBean.setOrderByVal("DESC");
                            }
                            if (comBean.getOrder() == null) {
                                comBean.setOrder(1);
                            }
                            break;
                        }
                        case "com.yilijishu.mybatis.ann.IfFieldCondition": {
                            comBean.setIfFieldCondition(true);
                            for (Pair<Symbol.MethodSymbol, Attribute> a : an.attribute.values) {
                                if ("value()".equals(a.fst.toString())) {
                                    comBean.setIfFieldConditionName(a.snd.getValue().toString());
                                }
                            }
                            break;
                        }
                        case "com.yilijishu.mybatis.ann.IgnoreColumn": {
                            comBean.setIgnore(true);
                            break;
                        }
                        case "com.yilijishu.mybatis.ann.IgnoreInsertColumn": {
                            comBean.setIgnoreInsert(true);
                            break;
                        }
                        case "com.yilijishu.mybatis.ann.TableId": {
                            comBean.setTableId(true);
                            break;
                        }
                        case "com.yilijishu.mybatis.ann.VirtualTableId": {
                            comBean.setVirtualTableId(true);
                            break;
                        }
                        case "com.yilijishu.mybatis.ann.AutoCreateTime": {
                            comBean.setAutoCreateTime(true);
                            break;
                        }
                        case "com.yilijishu.mybatis.ann.AutoModifyTime": {
                            comBean.setAutoModifyTime(true);
                            break;
                        }
                        default:
                            break;
                    }

                }
            }
        }
//        Class<?> clss = Class.forName(supClass);
//        Field[] fields = clss.getDeclaredFields();
        //替换
        if (supClass != null && supClass.length() > 1) {
            AptUtil aptUtil = new AptUtil(processingEnv);
            TypeElement typeElement = aptUtil.getTypeElement(supClass);
            java.util.List<VariableElement> variableElements = aptUtil.getDeclaredFields(typeElement);
            for (VariableElement field : variableElements) {
                // 字段名
                String fieldName = field.getSimpleName().toString();
                // 字段类型
                String fieldType = field.asType().toString();

                ComBean comBean = new ComBean();
                comBeans.add(comBean);
                comBean.setName(fieldName);
                comBean.setDefTypeColumn(fieldType);

                //默认值。
                DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);
                if (defaultValue != null) {
                    comBean.setDefaultValueBool(true);
                    comBean.setDefaultValue(defaultValue.value());
                }
                DelTag delTag = field.getAnnotation(DelTag.class);
                if (delTag != null) {
                    comBean.setDelTag(true);
                    comBean.setDelTagValue(delTag.value());
                }

                AutoCreateTime autoCreateTime = field.getAnnotation(AutoCreateTime.class);
                if (autoCreateTime != null) {
                    comBean.setAutoCreateTime(true);
                }

                VirtualTableId virtualTableId = field.getAnnotation(VirtualTableId.class);
                if (virtualTableId != null) {
                    comBean.setVirtualTableId(true);
                }
                AutoModifyTime autoModifyTime = field.getAnnotation(AutoModifyTime.class);
                if (autoModifyTime != null) {
                    comBean.setAutoModifyTime(true);
                }

                ColumnType columnType = field.getAnnotation(ColumnType.class);
                if (columnType != null) {
                    comBean.setColumnType(columnType.value());
                }
                ColumnNotNull columnNotNull = field.getAnnotation(ColumnNotNull.class);
                if (columnNotNull != null) {
                    comBean.setNotNull(true);
                }

                // 获取属性名
                IgnoreColumn ignoreColumn = field.getAnnotation(IgnoreColumn.class);
                if (ignoreColumn != null) {
                    comBean.setIgnore(true);
                }
                IgnoreInsertColumn ignoreInsertColumn = field.getAnnotation(IgnoreInsertColumn.class);
                if (ignoreInsertColumn != null) {
                    comBean.setIgnoreInsert(true);
                }
                IfFieldCondition ifFieldCondition = field.getAnnotation(IfFieldCondition.class);
                if (ifFieldCondition != null) {
                    comBean.setIfFieldCondition(true);
                    comBean.setIfFieldConditionName(ifFieldCondition.value());
                }
                TableId tableId = field.getAnnotation(TableId.class);
                if (tableId != null) {
                    comBean.setTableId(true);
                }
                Column column = field.getAnnotation(Column.class);
                OrderBy orderBy = field.getAnnotation(OrderBy.class);
                DefWhere defWhere = field.getAnnotation(DefWhere.class);
                if (defWhere != null) {
                    comBean.setDefWhere(true);
                    comBean.setDefWhereValue(defWhere.value());

                }
                if (column != null) {
                    comBean.setColumn(true);
                    comBean.setColumValue(column.value());
                    Class<?> cls = column.typeHandler();
                    if (cls != null) {
                        comBean.setHandler(true);
                        comBean.setHandlerStr(cls.getName());
                    }
                }
                if (orderBy != null) {
                    comBean.setOrderBy(true);
                    comBean.setOrderByVal(orderBy.value());
                    comBean.setOrder(orderBy.order());
                }

                messager.printMessage(Diagnostic.Kind.WARNING, "字段信息: " + comBean);
            }
        }

        return comBeans;
    }


    /**
     * 从注解属性值中提取类名（兼容 JDK8~JDK21）。
     * JDK8: Attribute.Class.getValue() 返回 java.lang.Class
     * JDK9+: Attribute.Class.getValue() 返回 com.sun.tools.javac.code.Type
     */
    private String classNameFromAttributeValue(Object value) {
        if (value instanceof Class) {
            return ((Class<?>) value).getName();
        }
        if (value instanceof com.sun.tools.javac.code.Type) {
            return ((com.sun.tools.javac.code.Type) value).tsym.getQualifiedName().toString();
        }
        return value.toString();
    }
}
