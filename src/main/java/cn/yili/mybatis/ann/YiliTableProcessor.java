package cn.yili.mybatis.ann;


import cn.yili.mybatis.entity.ComBean;
import cn.yili.mybatis.entity.ComMethod;
import cn.yili.mybatis.iter.BaseBeanInterface;
import cn.yili.mybatis.util.CamelUnderUtil;
import com.google.auto.service.AutoService;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Pair;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
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
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
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
                                } else if(tree.getKind().equals(Tree.Kind.METHOD)) {
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
//
//                            jcClassDecl.implementing = jcClassDecl.implementing.append(treeMaker.Ident(names.fromString("cn.yili.mybatis.iter.BaseBeanInterface")));
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
     * @param jcClassDecl
     * @param element
     * @param interfaceClass
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
        if(jcMethodDeclList!=null && jcMethodDeclList.size() > 0) {
            for (JCTree.JCMethodDecl jcMethodDecl : jcMethodDeclList) {
                if(jcMethodDecl != null) {
                    for (JCTree.JCAnnotation annotation : jcMethodDecl.mods.getAnnotations()) {
                        if(annotation != null) {
                            if(annotation.getAnnotationType() != null) {
                                if(annotation.getAnnotationType().type != null) {
                                    if ("cn.yili.mybatis.ann.OverrideOrderBy".equals(annotation.getAnnotationType().type.toString())) {
                                        comMethod.setOverrideOrderBy(true);
                                        comMethod.setOverriderOrderByMethod(jcMethodDecl.getName().toString());
                                    }
                                    if ("cn.yili.mybatis.ann.AddSelectCondition".equals(annotation.getAnnotationType().type.toString())) {
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
        if(methods != null && methods.length > 0) {
            for (Method method : methods) {
                OverrideOrderBy orderBy = method.getDeclaredAnnotation(OverrideOrderBy.class);
                if(orderBy != null && comMethod.isOverrideOrderBy()) {
                    comMethod.setOverrideOrderBy(true);
                    comMethod.setOverriderOrderByMethod(method.getName());
                }
                AddSelectCondition addSelectCondition = method.getDeclaredAnnotation(AddSelectCondition.class);
                if(addSelectCondition != null) {
                    java.util.List<String> selects = comMethod.getAddSelectConditionMethod();
                    if(selects == null) {
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
                // 获取属性名
                for (JCTree.JCAnnotation an : jcVariableDecl.mods.getAnnotations()) {
                    switch (an.getAnnotationType().type.toString()) {
                        case "cn.yili.mybatis.ann.Column": {
                            comBean.setColumn(true);
                            for (Pair<Symbol.MethodSymbol, Attribute> a : an.attribute.values) {
                                if ("value()".equals(a.fst.toString())) {
                                    String tmp = a.snd.getValue().toString();
                                    comBean.setColumValue(tmp);
                                }
                            }
                            break;
                        }
                        case "cn.yili.mybatis.ann.DefWhere": {
                            comBean.setDefWhere(true);
                            for (Pair<Symbol.MethodSymbol, Attribute> a : an.attribute.values) {
                                if ("value()".equals(a.fst.toString())) {
                                    comBean.setDefWhereValue(a.snd.getValue().toString());
                                }
                            }
                            break;
                        }
                        case "cn.yili.mybatis.ann.OrderBy": {
                            comBean.setOrderBy(true);
                            for (Pair<Symbol.MethodSymbol, Attribute> a : an.attribute.values) {
                                if ("value()".equals(a.fst.toString())) {
                                    messager.printMessage(Diagnostic.Kind.NOTE, a.snd.getValue().toString());
                                    comBean.setOrderByVal(a.snd.getValue().toString());
                                } else if ("order()".equals(a.fst.toString())) {
                                    comBean.setOrder(Integer.parseInt(a.snd.getValue().toString()));
                                }
                            }
                            if(comBean.getOrderByVal() ==  null || "".equals(comBean.getOrderByVal())) {
                                comBean.setOrderByVal("DESC");
                            }
                            if(comBean.getOrder() == null) {
                                comBean.setOrder(1);
                            }
                            break;
                        }
                        case "cn.yili.mybatis.ann.IfFieldCondition": {
                            comBean.setIfFieldCondition(true);
                            for (Pair<Symbol.MethodSymbol, Attribute> a : an.attribute.values) {
                                if ("value()".equals(a.fst.toString())) {
                                    comBean.setIfFieldConditionName(a.snd.getValue().toString());
                                }
                            }
                            break;
                        }
                        case "cn.yili.mybatis.ann.IgnoreColumn": {
                            comBean.setIgnore(true);
                            break;
                        }
                        case "cn.yili.mybatis.ann.IgnoreInsertColumn": {
                            comBean.setIgnoreInsert(true);
                            break;
                        }
                        case "cn.yili.mybatis.ann.TableId": {
                            comBean.setTableId(true);
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
            // 获取属性名
            IgnoreColumn ignoreColumn = field.getDeclaredAnnotation(IgnoreColumn.class);
            if (ignoreColumn != null) {
                comBean.setIgnore(true);
            }
            IgnoreInsertColumn ignoreInsertColumn = field.getDeclaredAnnotation(IgnoreInsertColumn.class);
            if(ignoreInsertColumn != null) {
                comBean.setIgnoreInsert(true);
            }
            IfFieldCondition ifFieldCondition = field.getDeclaredAnnotation(IfFieldCondition.class);
            if(ifFieldCondition != null) {
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
     * @param comBeans
     * @param element
     * @param table
     * @return
     */
    public java.util.List<JCTree.JCMethodDecl> buildMethods(java.util.List<ComBean> comBeans, Element element, Table table, ComMethod comMethod) {
        messager.printMessage(Diagnostic.Kind.NOTE, "执行buildMethods" + comBeans + comMethod);
        java.util.List<JCTree.JCMethodDecl> results = new ArrayList<>();
        StringBuffer columns = new StringBuffer();
        StringBuffer names2 = new StringBuffer();
        StringBuffer names3 = new StringBuffer();
        StringBuffer orderBy = new StringBuffer();
        StringBuffer defWhere = new StringBuffer();
        StringBuffer tableStr = new StringBuffer();
        StringBuffer updateWhere = new StringBuffer();
        StringBuffer insertColumns = new StringBuffer();
        StringBuffer insertNames2 = new StringBuffer();
        StringBuffer insertNames3 = new StringBuffer();
        java.util.List<ComBean> orderByList = new ArrayList<>();
        java.util.List<ComBean> defWhereList = new ArrayList<>();
        ListBuffer<JCTree.JCStatement> selectWhereStatement = new ListBuffer<>();
        ListBuffer<JCTree.JCStatement> updateSetStatement = new ListBuffer<>();
        selectWhereStatement.append(treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("result"), treeMaker.Ident(names.fromString("String")), treeMaker.Literal("")));
        updateSetStatement.append(treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("result"), treeMaker.Ident(names.fromString("String")), treeMaker.Literal("")));

        if (table.value() != null && !"".equals(table.value())) {
            tableStr.append(" ");
            tableStr.append(table.value());
            tableStr.append(" ");
        } else {
            tableStr.append(" ");
            tableStr.append(CamelUnderUtil.underName(element.getSimpleName().toString()));
            tableStr.append(" ");
        }
        if (comBeans != null && comBeans.size() > 0) {
            for (int jj = 0; jj<comBeans.size();jj ++) {
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
                if(!comBean.isIgnoreInsert()) {
                    insertColumns.append(",");
                    insertColumns.append("`");
                    insertColumns.append(columnName);
                    insertColumns.append("`");
                    insertNames2.append(",#{");
                    insertNames2.append(PARAM_OBJECT);
                    insertNames2.append(name);
                    insertNames2.append("}");
                    insertNames3.append(",#'{'list[{0}].");
                    insertNames3.append(name);
                    insertNames3.append("}");
                }

                columns.append(",");
                columns.append("`");
                columns.append(columnName);
                columns.append("`");
                names2.append(",#{");
                names2.append(PARAM_OBJECT);
                names2.append(name);
                names2.append("}");
                names3.append(",#'{'list[{0}].");
                names3.append(name);
                names3.append("}");


                if (comBean.isTableId()) {
                    updateWhere.append(" and `" + columnName + "` = #{" + PARAM_OBJECT + name + "}");
                } else {
                    updateSetStatement.append(treeMaker.If(
                            treeMaker.Binary(JCTree.Tag.NE, treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString("get" + CamelUnderUtil.camelName(name, true))), List.nil()), treeMaker.Literal(TypeTag.BOT, null)),
                            treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")), treeMaker.Literal("  `" + columnName + "` = #{" + PARAM_OBJECT + name + "} " +  ","))),
                            null));
                }
                if (comBean.isDefWhere()) {
                    defWhereList.add(comBean);
                } else {
                    selectWhereStatement.append(treeMaker.If(
                            treeMaker.Binary(JCTree.Tag.NE, treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString("get" + CamelUnderUtil.camelName(name, true))), List.nil()), treeMaker.Literal(TypeTag.BOT, null)),
                            comBean.isIfFieldCondition()?treeMaker.If(
                                    treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString(comBean.getIfFieldConditionName())), List.nil()),
                                    treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")), treeMaker.Literal(" and `" + columnName + "` = #{" + PARAM_OBJECT + name + "} "))), null)
                                    :treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")), treeMaker.Literal(" and `" + columnName + "` = #{" + PARAM_OBJECT + name + "} "))),
                            null));
                }
                if (comBean.isOrderBy()) {
                    orderByList.add(comBean);
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
        } else {
            String tmp = updateWhere.toString().replace("and", "where");
            updateWhere.delete(0, updateWhere.length());
            updateWhere.append(tmp);
        }

        results.add(buildMethod("baseGenTable", tableStr.toString()));
        results.add(buildMethod("baseGenColumnNames", columns.toString().substring(1)));
        results.add(buildMethod("baseGenNames", names2.toString().substring(1)));
        results.add(buildMethod("baseGenListNames", names3.toString().substring(1)));

        results.add(buildMethod("baseGenInertColumnNames", insertColumns.toString().substring(1)));
        results.add(buildMethod("baseGenInsertNames", insertNames2.toString().substring(1)));
        results.add(buildMethod("baseGenInsertListNames", insertNames3.toString().substring(1)));


        results.add(buildMethod("baseGenUpdateWhere", updateWhere.toString()));
        results.add(buildMethod("baseGenDefWhere", defWhere.toString()));
        messager.printMessage(Diagnostic.Kind.NOTE, "开始生成脚本");

        if(comMethod.isAddSelectCondition()) {
            for(String methodName : comMethod.getAddSelectConditionMethod()) {
                selectWhereStatement.append(treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")),treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString(methodName)), List.nil()))));
            }
        }
        selectWhereStatement.append(treeMaker.Return(treeMaker.Ident(names.fromString("result"))));
        results.add(buildMethod("baseGenSelectWhere", selectWhereStatement));

        updateSetStatement.append(treeMaker.Exec(treeMaker.Assign(treeMaker.Ident(names.fromString("result")),
                treeMaker.Apply(List.of(memberAccess("java.lang.Integer"), memberAccess("java.lang.Integer")), treeMaker.Select(treeMaker.Ident(names.fromString("result")), names.fromString("substring")),
                        List.of(treeMaker.Literal(1), treeMaker.Binary(JCTree.Tag.MINUS, treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("result")), names.fromString("length")), List.nil()), treeMaker.Literal(1)))))));
        updateSetStatement.append(treeMaker.Return(treeMaker.Ident(names.fromString("result"))));
        results.add(buildMethod("baseGenUpdateSet", updateSetStatement));

        if(comMethod.isOverrideOrderBy()) {
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
     * @param method
     * @param statements
     * @return
     */
    public JCTree.JCMethodDecl buildMethod(String method, ListBuffer<JCTree.JCStatement> statements) {
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        messager.printMessage(Diagnostic.Kind.NOTE, "代码块:" + body.toString());
        // 生成columnNames()方法
        return treeMaker
                .MethodDef(treeMaker.Modifiers(com.sun.tools.javac.code.Flags.PUBLIC), names.fromString(method),
                        treeMaker.Ident(names.fromString("String")),
                        List.nil(), List.nil(), List.nil(), body, null);
    }

    /**
     * 生成方法（方法名， 字符串）
     *
     * @param method
     * @param str
     * @return
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

}
