package cn.yili.mybatis.ann;


import cn.yili.mybatis.entity.ComBean;
import cn.yili.mybatis.iter.BaseBeanInterface;
import cn.yili.mybatis.util.CamelUnderUtil;
import com.google.auto.service.AutoService;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
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
                            // 获取所有属性
                            for (JCTree tree : jcClassDecl.defs) {
                                if (tree.getKind().equals(Tree.Kind.VARIABLE)) {
                                    JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;
                                    jcVariableDeclList = jcVariableDeclList.append(jcVariableDecl);
                                }
                            }
                            messager.printMessage(Diagnostic.Kind.NOTE, "获取到属性列表：" + jcVariableDeclList.size());
                            java.util.List<JCTree.JCMethodDecl> printMethods = buildMethods(makeColumnNamesMethodDecl(table.supClass(), jcVariableDeclList), element, table);
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
        messager.printMessage(Diagnostic.Kind.NOTE, "jcVariableDeclList.size()=" + jcVariableDeclList.size());
        if (jcVariableDeclList != null && jcVariableDeclList.size() > 0) {
            comBeans = new ArrayList<>();
            for (JCTree.JCVariableDecl jcVariableDecl : jcVariableDeclList) {
                messager.printMessage(Diagnostic.Kind.NOTE, "得到ComBean对象" + jcVariableDecl);
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
                                    comBean.setColumValue(a.snd.getValue().toString());
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
                                    comBean.setOrderByVal(a.snd.getValue().toString());
                                } else if ("order()".equals(a.fst.toString())) {
                                    comBean.setOrder(Integer.parseInt(a.snd.getValue().toString()));
                                }
                            }
                            break;
                        }
                        case "cn.yili.mybatis.ann.IgnoreColumn": {
                            comBean.setIgnore(true);
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
            messager.printMessage(Diagnostic.Kind.NOTE, "得到Field对象" + field.getName());
            messager.printMessage(Diagnostic.Kind.NOTE, "得到注解对象" + field.getDeclaredAnnotation(IgnoreColumn.class));
            ComBean comBean = new ComBean();
            comBeans.add(comBean);
            comBean.setName(field.getName());
            // 获取属性名
            IgnoreColumn ignoreColumn = field.getDeclaredAnnotation(IgnoreColumn.class);
            if (ignoreColumn != null) {
                comBean.setIgnore(true);
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
    public java.util.List<JCTree.JCMethodDecl> buildMethods(java.util.List<ComBean> comBeans, Element element, Table table) {
        messager.printMessage(Diagnostic.Kind.NOTE, "执行buildMethods" + comBeans);
        java.util.List<JCTree.JCMethodDecl> results = new ArrayList<>();
        StringBuffer columns = new StringBuffer();
        StringBuffer names2 = new StringBuffer();
        StringBuffer names3 = new StringBuffer();
        StringBuffer orderBy = new StringBuffer();
        StringBuffer defWhere = new StringBuffer();
        StringBuffer tableStr = new StringBuffer();
        StringBuffer updateWhere = new StringBuffer();
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
            for (ComBean comBean : comBeans) {
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
                            treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")), treeMaker.Literal("  `" + columnName + "` = #{" + PARAM_OBJECT + name + "} " + ", "))), null));
                }
                if (comBean.isDefWhere()) {
                    defWhereList.add(comBean);
                } else {
                    selectWhereStatement.append(treeMaker.If(
                            treeMaker.Binary(JCTree.Tag.NE, treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString("get" + CamelUnderUtil.camelName(name, true))), List.nil()), treeMaker.Literal(TypeTag.BOT, null)),
                            treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG, treeMaker.Ident(names.fromString("result")), treeMaker.Literal(" and `" + columnName + "` = #{" + PARAM_OBJECT + name + "} "))), null));
                }
                if (comBean.isOrderBy()) {
                    orderByList.add(comBean);
                }
            }
        }
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
        results.add(buildMethod("baseGenOrderBy", orderBy.toString()));
        results.add(buildMethod("baseGenTable", tableStr.toString()));
        results.add(buildMethod("baseGenColumnNames", columns.toString().substring(1)));
        results.add(buildMethod("baseGenNames", names2.toString().substring(1)));
        results.add(buildMethod("baseGenListNames", names3.toString().substring(1)));
        results.add(buildMethod("baseGenUpdateWhere", updateWhere.toString()));
        results.add(buildMethod("baseGenDefWhere", defWhere.toString()));
        messager.printMessage(Diagnostic.Kind.NOTE, "开始生成脚本");
        selectWhereStatement.append(treeMaker.Return(treeMaker.Ident(names.fromString("result"))));
        updateSetStatement.append(treeMaker.Return(treeMaker.Ident(names.fromString("result"))));
        results.add(buildMethod("baseGenSelectWhere", selectWhereStatement));
        results.add(buildMethod("baseGenUpdateSet", updateSetStatement));
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
