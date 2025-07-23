package com.yilijishu.mybatis.ann;


import com.sun.tools.javac.code.*;
import com.sun.tools.javac.util.*;
import com.yilijishu.mybatis.entity.ComBean;
import com.yilijishu.mybatis.entity.ComMethod;
import com.google.auto.service.AutoService;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.yilijishu.utils.CamelUnderUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;


@Slf4j
@AutoService(Processor.class)
public class YiliTableProcessor extends AbstractProcessor {

    private static final String PARAM_OBJECT = "p.";
    private Messager messager;
    private com.sun.tools.javac.api.JavacTrees trees;
    private com.sun.tools.javac.tree.TreeMaker treeMaker;
    private com.sun.tools.javac.util.Names names;
    private Class<?> supClass;

    private com.sun.tools.javac.code.Symtab symtab;

    private Types types;

    private static SetDataBase.DataBaseEnum DataBase = SetDataBase.DataBaseEnum.MYSQL;

    private static String EscapeSymbol = "`";

    public YiliTableProcessor() {
        super();
    }

    // 定义需要处理的注解
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Table.class.getCanonicalName());
        return annotations;
    }

    // 版本支持
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    // 初始化
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        com.sun.tools.javac.util.Context context = ((com.sun.tools.javac.processing.JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
        this.symtab = Symtab.instance(context);
        this.types = Types.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> setDataBases = roundEnv.getElementsAnnotatedWith(SetDataBase.class);
        if (setDataBases != null) {
            for (Element element : roundEnv.getElementsAnnotatedWith(SetDataBase.class)) {
                SetDataBase setDataBase = element.getAnnotation(SetDataBase.class);
                if (setDataBase != null) {
                    DataBase = setDataBase.value();
                    switch (DataBase) {
                        case MYSQL: {
                            EscapeSymbol = "`";
                            break;
                        }
                        default: {
                            EscapeSymbol = "\"";
                            break;
                        }
                    }
                }
            }
        }
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Table.class);
        if (set != null) {
            for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {
                Table table = element.getAnnotation(Table.class);
                JCTree jcTree = trees.getTree(element);
                if (table != null) {
                    jcTree.accept(new TreeTranslator() {
                        @Override
                        public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                            List<JCTree.JCVariableDecl> jcVariableDeclList = List.nil();
                            List<JCTree.JCMethodDecl> jcMethodDeclList = List.nil();
                            // 获取所有属性
                            for (JCTree tree : jcClassDecl.defs) {
                                if (tree.getKind().equals(Tree.Kind.VARIABLE)) {
                                    JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;
                                    jcVariableDeclList = jcVariableDeclList.append(jcVariableDecl);
                                } else if (tree.getKind().equals(Tree.Kind.METHOD)) {
                                    JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) tree;
                                    jcMethodDeclList = jcMethodDeclList.append(jcMethodDecl);
                                }
                            }
                            messager.printMessage(Diagnostic.Kind.NOTE, "获取到属性列表：" + jcVariableDeclList.size());
                            java.util.List<JCTree.JCMethodDecl> printMethods = buildMethods(makeColumnNamesMethodDecl(table.supClass(), jcVariableDeclList), element, table, makeMethodDecl(table.supClass(), jcMethodDeclList));
                            if (printMethods != null && printMethods.size() > 0) {
                                for (JCTree.JCMethodDecl printMethod : printMethods) {
                                    jcClassDecl.defs = jcClassDecl.defs.append(printMethod);
                                }
                            }
//                            JCTree.JCExpression interfaceType = treeMaker.Ident(names.fromString("com.yilijishu.mybatis.iter.BaseBeanInterface"));
//                            List<JCTree.JCExpression> implementing = jcClassDecl.getImplementsClause().prepend(interfaceType);
//                            JCTree.JCClassDecl modifiedClass = treeMaker.ClassDef(
//                                    jcClassDecl.getModifiers(),
//                                    jcClassDecl.getSimpleName(),
//                                    jcClassDecl.getTypeParameters(),
//                                    jcClassDecl.getExtendsClause(),
//                                    implementing,
//                                    jcClassDecl.getMembers()
//                            );

                            JCTree.JCExpression qualIdent = createQualIdent("com.yilijishu.mybatis.iter.BaseBeanInterface", treeMaker, names);
//                            List<JCTree.JCExpression> implementing = jcClassDecl.getImplementsClause();
//                            implementing = implementing.append(qualIdent);
                            jcClassDecl.implementing = jcClassDecl.implementing.append(qualIdent);

//
//                            jcClassDecl.implementing = jcClassDecl.implementing.append(treeMaker.Ident(names.fromString("com.yilijishu.mybatis.iter.BaseBeanInterface")));
                            //addTnterface(jcClassDecl, element, BaseBeanInterface.class);
                            super.visitClassDef(jcClassDecl);
                        }
                    });

                }
            }
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, "失败：" + set);
        }
        return true;
    }

    private JCTree.JCExpression createQualIdent(String qualifiedName, TreeMaker treeMaker, Names names) {
        int lastDotIndex = qualifiedName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return treeMaker.Ident(names.fromString(qualifiedName));
        }

        String packageName = qualifiedName.substring(0, lastDotIndex);
        String simpleName = qualifiedName.substring(lastDotIndex + 1);

        JCTree.JCExpression qualIdent = null;
        for (String part : packageName.split("\\.")) {
            JCTree.JCExpression segment = treeMaker.Ident(names.fromString(part));
            if (qualIdent == null) {
                qualIdent = segment;
            } else {
                qualIdent = treeMaker.Select(qualIdent, names.fromString(part));
            }
        }

        qualIdent = treeMaker.Select(qualIdent, names.fromString(simpleName));

        return qualIdent;
    }

    /**
     * 创建 域/方法 的多级访问, 方法的标识只能是最后一个
     *
     * @param components
     * @return
     */
    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getNameFromString(componentArray[i]));
        }
        return expr;
    }

    /**
     * 根据字符串获取Name，（利用Names的fromString静态方法）
     *
     * @param s
     * @return
     */
    private com.sun.tools.javac.util.Name getNameFromString(String s) {
        return names.fromString(s);
    }

    /**
     * 判断有没有实现指定的接口
     *
     * @param jcClassDecl
     * @param interfaceClass 接口
     * @return 如果类已经实现了指定接口则返回true，否则返回false
     */
    private boolean hasInterface(JCTree.JCClassDecl jcClassDecl, Class<?> interfaceClass) {
        messager.printMessage(Diagnostic.Kind.NOTE, "开始判断接口是否存在");
        for (JCTree.JCExpression impl : jcClassDecl.implementing) {
            if (impl.type.toString().equals(interfaceClass.getName())) {
                messager.printMessage(Diagnostic.Kind.NOTE, "开始判断接口存在");
                return true;
            }
        }
        return false;
    }

    /**
     * 导入包.
     *
     * @param element
     * @param importClass
     */
    private void importPackage(Element element, Class<?> importClass) {
        messager.printMessage(Diagnostic.Kind.NOTE, "导入包");
        JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit) trees.getPath(element).getCompilationUnit();
        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(importClass.getPackage().getName())), names.fromString(importClass.getSimpleName()));
        JCTree.JCImport jcImport = treeMaker.Import(fieldAccess, false);
        ListBuffer<JCTree> imports = new ListBuffer<>();
        imports.add(jcImport);
        for (int i = 0; i < compilationUnit.defs.size(); i++) {
            imports.append(compilationUnit.defs.get(i));
        }
        compilationUnit.defs = imports.toList();
        messager.printMessage(Diagnostic.Kind.NOTE, "导入完成" + compilationUnit.defs);
    }

    /**
     * 添加实现接口.
     *
     * @param jcClassDecl 传递class decl
     * @param element 传递 element
     * @param interfaceClass 传递interfaceClass
     */
    public void addTnterface(JCTree.JCClassDecl jcClassDecl, Element element, Class<?> interfaceClass) {
        //判断类有没有实现此接口
        if (!hasInterface(jcClassDecl, interfaceClass)) {
            // 导包（会自动去重）
            importPackage(element, interfaceClass);

            java.util.List<JCTree.JCExpression> implementing = jcClassDecl.implementing;
            ListBuffer<JCTree.JCExpression> statements = new ListBuffer<>();
            for (JCTree.JCExpression impl : implementing) {
                statements.append(impl);
            }
            messager.printMessage(Diagnostic.Kind.NOTE, "开始创建实现类");
            Symbol.ClassSymbol sym = new Symbol.ClassSymbol(Flags.AccessFlags, names.fromString(interfaceClass.getSimpleName()), null);
            statements.append(treeMaker.Ident(sym));
            messager.printMessage(Diagnostic.Kind.NOTE, "开始替换类");
            jcClassDecl.implementing = statements.toList();
            messager.printMessage(Diagnostic.Kind.NOTE, "替换完成" + ((JCTree.JCCompilationUnit) trees.getPath(element).getCompilationUnit()).defs);
        }
    }


    @SneakyThrows
    private ComMethod makeMethodDecl(String supClass, List<JCTree.JCMethodDecl> jcMethodDeclList) {
        messager.printMessage(Diagnostic.Kind.NOTE, "开始执行方法对象");
        ComMethod comMethod = new ComMethod();
        if (jcMethodDeclList != null && jcMethodDeclList.size() > 0) {
            for (JCTree.JCMethodDecl jcMethodDecl : jcMethodDeclList) {
                if (jcMethodDecl != null) {
                    for (JCTree.JCAnnotation annotation : jcMethodDecl.mods.getAnnotations()) {
                        if (annotation != null) {
                            if (annotation.getAnnotationType() != null) {
                                if (annotation.getAnnotationType().type != null) {
                                    if ("com.yilijishu.mybatis.ann.OverrideOrderBy".equals(annotation.getAnnotationType().type.toString())) {
                                        comMethod.setOverrideOrderBy(true);
                                        comMethod.setOverriderOrderByMethod(jcMethodDecl.getName().toString());
                                    }
                                    if ("com.yilijishu.mybatis.ann.AddSelectCondition".equals(annotation.getAnnotationType().type.toString())) {
                                        comMethod.setAddSelectCondition(true);
                                        java.util.List<String> selects = comMethod.getAddSelectConditionMethod();
                                        if (selects == null) {
                                            selects = new ArrayList<>();
                                            comMethod.setAddSelectConditionMethod(selects);
                                        }
                                        selects.add(jcMethodDecl.getName().toString());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "开始执行超类方法对象");
        Class<?> clss = Class.forName(supClass);
        Method[] methods = clss.getMethods();
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
                OverrideOrderBy orderBy = method.getDeclaredAnnotation(OverrideOrderBy.class);
                if (orderBy != null && comMethod.isOverrideOrderBy()) {
                    comMethod.setOverrideOrderBy(true);
                    comMethod.setOverriderOrderByMethod(method.getName());
                }
                AddSelectCondition addSelectCondition = method.getDeclaredAnnotation(AddSelectCondition.class);
                if (addSelectCondition != null) {
                    java.util.List<String> selects = comMethod.getAddSelectConditionMethod();
                    if (selects == null) {
                        selects = new ArrayList<>();
                        comMethod.setAddSelectCondition(true);
                        comMethod.setAddSelectConditionMethod(selects);
                    }
                    selects.add(method.getName());
                }
            }
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "方法返回：" + comMethod);
        return comMethod;
    }

    /**
     * 查询类信息、组装方法所需。并生成方法。
     *
     * @param supClass           父类
     * @param jcVariableDeclList 类中的所有属性
     * @return 返回method
     */
    @SneakyThrows
    private java.util.List<ComBean> makeColumnNamesMethodDecl(String supClass, List<JCTree.JCVariableDecl> jcVariableDeclList) {
        messager.printMessage(Diagnostic.Kind.NOTE, "执行makeColumnNamesMethodDecl");
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
                                    messager.printMessage(Diagnostic.Kind.NOTE, a.snd.getValue().toString());
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
        Class<?> clss = Class.forName(supClass);
        Field[] fields = clss.getDeclaredFields();

        for (Field field : fields) {
            ComBean comBean = new ComBean();
            comBeans.add(comBean);
            comBean.setName(field.getName());

            DelTag delTag = field.getDeclaredAnnotation(DelTag.class);
            if(delTag != null) {
                comBean.setDelTag(true);
                comBean.setDelTagValue(delTag.value());
            }

            AutoCreateTime autoCreateTime = field.getDeclaredAnnotation(AutoCreateTime.class);
            if(autoCreateTime != null) {
                comBean.setAutoCreateTime(true);
            }

            VirtualTableId virtualTableId = field.getDeclaredAnnotation(VirtualTableId.class);
            if(virtualTableId != null) {
                comBean.setVirtualTableId(true);
            }
            AutoModifyTime autoModifyTime = field.getDeclaredAnnotation(AutoModifyTime.class);
            if(autoModifyTime != null) {
                comBean.setAutoModifyTime(true);
            }

            ColumnType columnType = field.getDeclaredAnnotation(ColumnType.class);
            if(columnType != null) {
                comBean.setColumnType(columnType.value());
            }
            ColumnNotNull columnNotNull = field.getDeclaredAnnotation(ColumnNotNull.class);
            if(columnNotNull != null) {
                comBean.setNotNull(true);
            }
            comBean.setDefTypeColumn(field.getGenericType().getTypeName());
            // 获取属性名
            IgnoreColumn ignoreColumn = field.getDeclaredAnnotation(IgnoreColumn.class);
            if (ignoreColumn != null) {
                comBean.setIgnore(true);
            }
            IgnoreInsertColumn ignoreInsertColumn = field.getDeclaredAnnotation(IgnoreInsertColumn.class);
            if (ignoreInsertColumn != null) {
                comBean.setIgnoreInsert(true);
            }
            IfFieldCondition ifFieldCondition = field.getDeclaredAnnotation(IfFieldCondition.class);
            if (ifFieldCondition != null) {
                comBean.setIfFieldCondition(true);
                comBean.setIfFieldConditionName(ifFieldCondition.value());
            }
            TableId tableId = field.getDeclaredAnnotation(TableId.class);
            if (tableId != null) {
                comBean.setTableId(true);
            }
            Column column = field.getDeclaredAnnotation(Column.class);
            OrderBy orderBy = field.getDeclaredAnnotation(OrderBy.class);
            DefWhere defWhere = field.getDeclaredAnnotation(DefWhere.class);
            if (defWhere != null) {
                comBean.setDefWhere(true);
                comBean.setDefWhereValue(defWhere.value());

            }
            if (column != null) {
                comBean.setColumn(true);
                comBean.setColumValue(column.value());
            }
            if (orderBy != null) {
                comBean.setOrderBy(true);
                comBean.setOrderByVal(orderBy.value());
                comBean.setOrder(orderBy.order());
            }
            messager.printMessage(Diagnostic.Kind.NOTE, "字段信息: " + comBean);
        }
        return comBeans;
    }

    /**
     * 加载数据、生成方法.
     *
     * @param comBeans 传递Combean列表
     * @param element element对象
     * @param table 表名
     * @param comMethod 传递自定义方法
     * @return 方法定义列表
     */
    public java.util.List<JCTree.JCMethodDecl> buildMethods(java.util.List<ComBean> comBeans, Element element, Table table, ComMethod comMethod) {
        messager.printMessage(Diagnostic.Kind.NOTE, "执行buildMethods" + comBeans + comMethod);
        java.util.List<JCTree.JCMethodDecl> results = new ArrayList<>();
        StringBuffer columns = new StringBuffer();
//        StringBuffer names2 = new StringBuffer();
//        StringBuffer names3 = new StringBuffer();
        StringBuffer orderBy = new StringBuffer();
        StringBuffer defWhere = new StringBuffer();
        StringBuffer tableStr = new StringBuffer();
        //StringBuffer updateWhere = new StringBuffer();
        StringBuffer baseGenUpdateAllSet = new StringBuffer();
        StringBuffer insertColumns = new StringBuffer();
        StringBuffer insertNames2 = new StringBuffer();
        StringBuffer insertNames3 = new StringBuffer();
        StringBuffer tableId = new StringBuffer();
        StringBuffer virTableId = new StringBuffer();
        StringBuffer createBuffer = new StringBuffer();
        StringBuffer sqlType = new StringBuffer();

        java.util.List<ComBean> orderByList = new ArrayList<>();
        java.util.List<ComBean> defWhereList = new ArrayList<>();
        ListBuffer<JCTree.JCStatement> selectWhereStatement = new ListBuffer<>();
        ListBuffer<JCTree.JCStatement> updateSetStatement = new ListBuffer<>();
        ListBuffer<JCTree.JCStatement> updateWhereStatement = new ListBuffer<>();
        selectWhereStatement.append(treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("result"), treeMaker.Ident(names.fromString("String")), treeMaker.Literal("")));
        updateSetStatement.append(treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("result"), treeMaker.Ident(names.fromString("String")), treeMaker.Literal("")));
        updateWhereStatement.append(treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("result"), treeMaker.Ident(names.fromString("String")), treeMaker.Literal("")));
        if (table.value() != null && !"".equals(table.value())) {
            tableStr.append(" " + EscapeSymbol);
            tableStr.append(table.value());
            tableStr.append(EscapeSymbol + " ");
            createBuffer.append("");
        } else {
            tableStr.append(" " + EscapeSymbol);
            tableStr.append(CamelUnderUtil.underName(element.getSimpleName().toString()));
            tableStr.append(EscapeSymbol + " ");
        }

        sqlType.append(DataBase.name());

        String tableIdVal = null;
        String tableIdColumnVal = null;
        String virtualTableId = null;
        String virtualTableIdColumn = null;
        boolean genDelTag = false;
        StringBuffer genDelTagBuffer = new StringBuffer();
        StringBuffer genDelTagValueBuffer = new StringBuffer();
        if (comBeans != null && comBeans.size() > 0) {
            for (int jj = 0; jj < comBeans.size(); jj++) {
                ComBean comBean = comBeans.get(jj);
                if (comBean.isIgnore()) {
                    continue;
                }
                String columnName = "";
                String name = "";
                name = comBean.getName();
                if (comBean.isColumn()) {
                    columnName = comBean.getColumValue();
                } else {
                    columnName = CamelUnderUtil.underName(comBean.getName());
                }
                if (!comBean.isIgnoreInsert()) {
                    insertColumns.append(",");
                    insertColumns.append(EscapeSymbol);
                    insertColumns.append(columnName);
                    insertColumns.append(EscapeSymbol);

                    //判断是否为默认创建时间
                    if(!comBean.isAutoCreateTime()) {
                        insertNames2.append(",#{");
                        insertNames2.append(PARAM_OBJECT);
                        insertNames2.append(name);
                        insertNames2.append("}");
                    } else {
                        switch (DataBase) {
                            case ORACLE: {
                                insertNames2.append(",SYSDATE");
                                break;
                            }
                            default: {
                                insertNames2.append(",NOW()");
                                break;
                            }
                        }
                    }
                    if(!comBean.isAutoCreateTime()) {
                        insertNames3.append(",#'{'list[{0}].");
                        insertNames3.append(name);
                        insertNames3.append("}");
                    } else {
                        switch (DataBase) {
                            case ORACLE: {
                                insertNames3.append(",SYSDATE");
                                break;
                            }
                            default: {
                                insertNames3.append(",NOW()");
                                break;
                            }
                        }
                    }

                }

                columns.append(",");
                columns.append(EscapeSymbol);
                columns.append(columnName);
                columns.append(EscapeSymbol);


                createBuffer.append(" ");
                createBuffer.append(columnName);
                if(StringUtils.isNotBlank(comBean.getColumnType())) {
                    createBuffer.append(" ");
                    createBuffer.append(comBean.getColumnType());
                    createBuffer.append(" ");
                } else {
                    switch (comBean.getDefTypeColumn()) {
                        case "java.lang.Integer":
                        case "int": {
                            switch (DataBase) {
                                case POSTGRESQL: {
                                    createBuffer.append(" INTEGER ");
                                    break;
                                }
                                case ORACLE: {
                                    createBuffer.append(" NUMBER(11,0) ");
                                    break;
                                }
                                default: {
                                    createBuffer.append(" INT ");
                                    break;
                                }
                            }
                            break;
                        }
                        case "long":
                        case "java.lang.Long": {
                            switch (DataBase) {
                                case POSTGRESQL: {
                                    createBuffer.append(" BIGINT ");
                                    break;
                                }
                                case ORACLE: {
                                    createBuffer.append(" NUMBER(20,0) ");
                                    break;
                                }
                                default: {
                                    createBuffer.append(" BIGINT ");
                                    break;
                                }
                            }
                            break;
                        }
                        case "java.lang.Boolean":
                        case "boolean": {
                            switch (DataBase) {
                                case POSTGRESQL: {
                                    createBuffer.append(" BOOLEAN ");
                                    break;
                                }
                                case ORACLE: {
                                    createBuffer.append(" NUMBER(1) ");
                                    break;
                                }
                                default: {
                                    createBuffer.append(" TINYINT(1) ");
                                    break;
                                }
                            }
                            break;
                        }
                        case "java.sql.Date":
                        case "java.util.Date":
                        case "java.time.LocalDateTime": {
                            createBuffer.append(" TIMESTAMP ");
                            break;
                        }
                        case "java.time.LocalDate": {
                            createBuffer.append(" DATE ");
                            break;
                        }
                        case "java.math.BigDecimal": {
                            switch (DataBase) {
                                case POSTGRESQL: {
                                    createBuffer.append(" NUMERIC(14,2) ");
                                    break;
                                }
                                case ORACLE: {
                                    createBuffer.append(" NUMBER(14,2) ");
                                    break;
                                }
                                default: {
                                    createBuffer.append(" DECIMAL(14,2) ");
                                    break;
                                }
                            }
                            break;
                        }
                        case "java.lang.Double":
                        case "double": {
                            switch (DataBase) {
                                case POSTGRESQL: {
                                    createBuffer.append(" DOUBLE PRECISION ");
                                    break;
                                }
                                case ORACLE: {
                                    createBuffer.append(" BINARY_DOUBLE ");
                                    break;
                                }
                                default: {
                                    createBuffer.append(" DOUBLE ");
                                    break;
                                }
                            }
                            break;
                        }
                        case "java.lang.Float":
                        case "float": {
                            switch (DataBase) {
                                case POSTGRESQL: {
                                    createBuffer.append(" REAL ");
                                    break;
                                }
                                case ORACLE: {
                                    createBuffer.append(" BINARY_FLOAT ");
                                    break;
                                }
                                default: {
                                    createBuffer.append(" FLOAT ");
                                    break;
                                }
                            }
                            break;
                        }
                        case "byte[]":
                        case "java.lang.Byte[]": {
                            switch (DataBase) {
                                case POSTGRESQL: {
                                    createBuffer.append(" BYTEA ");
                                    break;
                                }
                                case ORACLE: {
                                    createBuffer.append(" BLOB ");
                                    break;
                                }
                                default: {
                                    createBuffer.append(" BLOB ");
                                    break;
                                }
                            }
                            break;
                        }
                        case "byte":
                        case "java.lang.Byte": {
                            switch (DataBase) {
                                case POSTGRESQL: {
                                    createBuffer.append(" SMALLINT ");
                                    break;
                                }
                                case ORACLE: {
                                    createBuffer.append(" NUMBER(3) ");
                                    break;
                                }
                                default: {
                                    createBuffer.append(" TINYINT ");
                                    break;
                                }
                            }
                            break;
                        }
                        default: {
                            switch (DataBase) {
                                case POSTGRESQL: {
                                    createBuffer.append(" VARCHAR(255) ");
                                    break;
                                }
                                case ORACLE: {
                                    createBuffer.append(" VARCHAR2(255) ");
                                    break;
                                }
                                default: {
                                    createBuffer.append(" VARCHAR(255) ");
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }


                if(comBean.isTableId()) {
                    switch (DataBase) {
                        case POSTGRESQL: {
                            createBuffer.append(" SERIAL PRIMARY KEY ");
                            break;
                        }
                        case ORACLE: {
                            createBuffer.append(" PRIMARY KEY ");
                            break;
                        }
                        default: {
                            createBuffer.append(" AUTO_INCREMENT PRIMARY KEY ");
                            break;
                        }
                    }

                }
                if(comBean.getNotNull() || comBean.isVirtualTableId()) {
                    createBuffer.append(" not null");
                }


                createBuffer.append(" ,");

//                names2.append(",#{");
//                names2.append(PARAM_OBJECT);
//                names2.append(name);
//                names2.append("}");
//                names3.append(",#'{'list[{0}].");
//                names3.append(name);
//                names3.append("}");

                if (comBean.isVirtualTableId()) {
                    virtualTableId = name;
                    virtualTableIdColumn = columnName;
                    virTableId.append(EscapeSymbol + columnName + EscapeSymbol);
                }
                if (comBean.isTableId()) {
                    //updateWhere.append(" and " + EscapeSymbol + columnName + EscapeSymbol + " = #{" + PARAM_OBJECT + name + "}");
                    tableId.append(EscapeSymbol + columnName + EscapeSymbol);
                    tableIdVal = name;
                    tableIdColumnVal = columnName;
                } else if (!comBean.isVirtualTableId()) {


                    if(!comBean.isAutoCreateTime()) {
                        if(!comBean.isAutoModifyTime()) {
                            updateSetStatement.append(treeMaker.If(
                                    treeMaker.Binary(JCTree.Tag.NE, treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString("get" + CamelUnderUtil.camelName(name, true))), List.nil()), treeMaker.Literal(TypeTag.BOT, null)),
                                    treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")), treeMaker.Literal("  " + EscapeSymbol + columnName + EscapeSymbol + " = #{" + PARAM_OBJECT + name + "} " + ","))),
                                    null));
                            baseGenUpdateAllSet.append(EscapeSymbol + columnName + EscapeSymbol + " = #{" + name + "} ,");
                        } else {
                            switch (DataBase) {
                                case ORACLE: {
                                    updateSetStatement.append(treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")), treeMaker.Literal("  " + EscapeSymbol + columnName + EscapeSymbol + " = SYSDATE " + ","))));
                                    baseGenUpdateAllSet.append(columnName + " = SYSDATE ,");
                                    break;
                                }
                                default: {
                                    updateSetStatement.append(treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")), treeMaker.Literal("  " + EscapeSymbol + columnName + EscapeSymbol + " = NOW() " + ","))));
                                    baseGenUpdateAllSet.append(columnName + " = NOW() ,");
                                    break;
                                }
                            }
                        }

                    }
                }
                if (comBean.isDefWhere()) {
                    defWhereList.add(comBean);
                } else {
                    selectWhereStatement.append(treeMaker.If(
                            treeMaker.Binary(JCTree.Tag.NE, treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString("get" + CamelUnderUtil.camelName(name, true))), List.nil()), treeMaker.Literal(TypeTag.BOT, null)),
                            comBean.isIfFieldCondition() ? treeMaker.If(
                                    treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString(comBean.getIfFieldConditionName())), List.nil()),
                                    treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")), treeMaker.Literal(" and " +EscapeSymbol + columnName + EscapeSymbol+" = #{" + PARAM_OBJECT + name + "} "))), null)
                                    : treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")), treeMaker.Literal(" and "+EscapeSymbol + columnName + EscapeSymbol+" = #{" + PARAM_OBJECT + name + "} "))),
                            null));
                }
                if (comBean.isOrderBy()) {
                    orderByList.add(comBean);
                }
                if(comBean.isDelTag()) {
                    genDelTag = true;
                    genDelTagBuffer.append(columnName);
                    genDelTagValueBuffer.append(comBean.getDelTagValue());

                }
            }
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "开始设置排序" + orderByList);
        if (orderByList != null && orderByList.size() > 0) {
            orderByList.sort((a, b) -> a.getOrder() - b.getOrder());
            orderBy.append(" order by ");
            for (int i = 0; i < orderByList.size(); i++) {
                ComBean comBean = orderByList.get(i);
                if (comBean.isColumn()) {
                    orderBy.append(comBean.getColumValue());
                } else {
                    orderBy.append(CamelUnderUtil.underName(comBean.getName()));
                }
                orderBy.append(" ");
                orderBy.append(comBean.getOrderByVal());
                if (i + 1 < orderByList.size()) {
                    orderBy.append(",");
                }
            }
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "开始设置默认WHERE");
        if (defWhereList != null && defWhereList.size() > 0) {
            defWhere.append(" where ");
            for (int i = 0; i < defWhereList.size(); i++) {
                ComBean comBean = defWhereList.get(i);
                if (comBean.isColumn()) {
                    defWhere.append(comBean.getColumValue());
                } else {
                    defWhere.append(CamelUnderUtil.underName(comBean.getName()));
                }
                defWhere.append(" = ");
                defWhere.append(comBean.getDefWhereValue());
                if (i + 1 < defWhereList.size()) {
                    defWhere.append(" and ");
                }
            }
        }
//        } else {
//            //String tmp = updateWhere.toString().replace("and", "where");
//            //updateWhere.delete(0, updateWhere.length());
//            //updateWhere.append(tmp);
//        }

        if (StringUtils.isNotBlank(tableIdVal)) {
            updateWhereStatement.append(treeMaker.If(treeMaker.Binary(JCTree.Tag.NE, treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString("get" + CamelUnderUtil.camelName(tableIdVal, true))), List.nil()), treeMaker.Literal(TypeTag.BOT, null)),
                    treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")), treeMaker.Literal("  "+EscapeSymbol + tableIdColumnVal + EscapeSymbol+" = #{" + PARAM_OBJECT + tableIdVal + "} "))),
                    StringUtils.isNotBlank(virtualTableId) ? treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")), treeMaker.Literal("  "+EscapeSymbol + virtualTableIdColumn + EscapeSymbol+" = #{" + PARAM_OBJECT + virtualTableId + "} "))) : null
            ));
        }


        results.add(buildMethod("baseGenTable", tableStr.toString()));
        results.add(buildMethod("baseGenColumnNames", columns.toString().substring(1)));
//        results.add(buildMethod("baseGenNames", names2.toString().substring(1)));
//        results.add(buildMethod("baseGenListNames", names3.toString().substring(1)));

        results.add(buildMethod("baseGenInertColumnNames", insertColumns.toString().substring(1)));
        results.add(buildMethod("baseGenInsertNames", insertNames2.toString().substring(1)));
        results.add(buildMethod("baseGenInsertListNames", insertNames3.toString().substring(1)));

        results.add(buildMethod("baseSqlDatabase", sqlType.toString()));
        results.add(buildMethod("baseCreateTable", createBuffer.toString().substring(0, createBuffer.length()-1)));

        //results.add(buildMethod("baseGenUpdateWhere", updateWhere.toString()));
        results.add(buildMethod("baseGenDefWhere", defWhere.toString()));
        results.add(buildMethod("baseGenId", tableId.toString()));
        results.add(buildMethod("baseGenVirtualId", virTableId.toString()));
        results.add(buildMethod("genDelTag", genDelTag));
        if(genDelTag) {
            results.add(buildMethod("genDelTagColumn", genDelTagBuffer.toString()));
            results.add(buildMethod("genDelTagValue", genDelTagValueBuffer.toString()));
        }

        results.add(buildMethod("baseGenUpdateAllSet", baseGenUpdateAllSet.substring(0, baseGenUpdateAllSet.length() - 1)));
        messager.printMessage(Diagnostic.Kind.NOTE, "开始生成脚本");

        if (comMethod.isAddSelectCondition()) {
            for (String methodName : comMethod.getAddSelectConditionMethod()) {
                selectWhereStatement.append(treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")), treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString(methodName)), List.nil()))));
            }
        }
        selectWhereStatement.append(treeMaker.Return(treeMaker.Ident(names.fromString("result"))));
        results.add(buildMethod("baseGenSelectWhere", selectWhereStatement));

        updateSetStatement.append(treeMaker.Exec(treeMaker.Assign(treeMaker.Ident(names.fromString("result")),
                treeMaker.Apply(List.of(memberAccess("java.lang.Integer"), memberAccess("java.lang.Integer")), treeMaker.Select(treeMaker.Ident(names.fromString("result")), names.fromString("substring")),
                        List.of(treeMaker.Literal(1), treeMaker.Binary(JCTree.Tag.MINUS, treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("result")), names.fromString("length")), List.nil()), treeMaker.Literal(1)))))));
        updateSetStatement.append(treeMaker.Return(treeMaker.Ident(names.fromString("result"))));
        results.add(buildMethod("baseGenUpdateSet", updateSetStatement));

        updateWhereStatement.append(treeMaker.Return(treeMaker.Ident(names.fromString("result"))));
        results.add(buildMethod("baseGenUpdateWhere", updateWhereStatement));

        if (comMethod.isOverrideOrderBy()) {
            ListBuffer<JCTree.JCStatement> orderByStatement = new ListBuffer<>();
            orderByStatement.append(treeMaker.Return(treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString(comMethod.getOverriderOrderByMethod())), List.nil())));
            results.add(buildMethod("baseGenOrderBy", orderByStatement));
        } else {
            results.add(buildMethod("baseGenOrderBy", orderBy.toString()));
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "生成脚本结束");
        return results;
    }

    //insert
    //insertAll
    //update
    //select
    //

    /**
     * 生成方法（方法名， 步骤）
     *
     * @param method 要生成的名法名
     * @param statements 声明代码树列表
     * @return 返回方法声明
     */
    public JCTree.JCMethodDecl buildMethod(String method, ListBuffer<JCTree.JCStatement> statements) {
        return buildMethod(method, statements, "String");
    }

    /**
     * 生成方法（方法名， 步骤）
     *
     * @param method 要生成的名法名
     * @param statements 声明代码树列表
     * @param returnType 设定返回类型
     * @return 返回方法声明
     */
    public JCTree.JCMethodDecl buildMethod(String method, ListBuffer<JCTree.JCStatement> statements, String returnType) {
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        messager.printMessage(Diagnostic.Kind.NOTE, "代码块:" + body.toString());
        // 生成columnNames()方法
        return treeMaker
                .MethodDef(treeMaker.Modifiers(com.sun.tools.javac.code.Flags.PUBLIC), names.fromString(method),
                        treeMaker.Ident(names.fromString(returnType)),
                        List.nil(), List.nil(), List.nil(), body, null);
    }

    /**
     * 生成返回String的方法（方法名， 字符串）
     *
     * @param method 方法名
     * @param str 返回的字符串
     * @return 返回方法声明
     */
    public JCTree.JCMethodDecl buildMethod(String method, String str) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(treeMaker.Return(treeMaker.Literal(str)));
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        // 生成columnNames()方法
        return treeMaker
                .MethodDef(treeMaker.Modifiers(com.sun.tools.javac.code.Flags.PUBLIC), names.fromString(method),
                        treeMaker.Ident(names.fromString("String")),
                        List.nil(), List.nil(), List.nil(), body, null);
    }

    /**
     * 生成返回boolean方法（方法名， 字符串）
     *
     * @param method 方法名
     * @param bool 是否
     * @return 返回方法声明
     */
    public JCTree.JCMethodDecl buildMethod(String method, boolean bool) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(treeMaker.Return(treeMaker.Literal(bool)));
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        // 生成columnNames()方法
        return treeMaker
                .MethodDef(treeMaker.Modifiers(com.sun.tools.javac.code.Flags.PUBLIC), names.fromString(method),
                        treeMaker.Type(symtab.booleanType),
                        List.nil(), List.nil(), List.nil(), body, null);
    }

}
