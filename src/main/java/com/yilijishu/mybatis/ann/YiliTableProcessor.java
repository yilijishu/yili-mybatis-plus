package com.yilijishu.mybatis.ann;


import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import com.yilijishu.mybatis.constant.Constant;
import com.yilijishu.mybatis.entity.ComBean;
import com.yilijishu.mybatis.entity.ComMethod;
import com.yilijishu.mybatis.util.CamelUnderUtil;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;


public class YiliTableProcessor extends AbstractProcessor {

    private static final String PARAM_OBJECT = "p.";
    private Messager messager;
    private com.sun.tools.javac.api.JavacTrees trees;
    private com.sun.tools.javac.tree.TreeMaker treeMaker;
    private com.sun.tools.javac.util.Names names;

    private com.sun.tools.javac.code.Symtab symtab;

    private Types types;

    private ComBeanUtil comBeanUtil;

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
        comBeanUtil = new ComBeanUtil(messager, processingEnv);
        try {
            ProcessingEnvironment unwrappedEnv = jbUnwrap(ProcessingEnvironment.class, processingEnv);
            this.trees = JavacTrees.instance(unwrappedEnv);
            com.sun.tools.javac.processing.JavacProcessingEnvironment javacEnv
                    = (com.sun.tools.javac.processing.JavacProcessingEnvironment) unwrappedEnv;
            com.sun.tools.javac.util.Context context = javacEnv.getContext();
            this.treeMaker = TreeMaker.instance(context);
            this.names = Names.instance(context);
            this.symtab = Symtab.instance(context);
            this.types = Types.instance(context);

        } catch (Exception e) {
            // 解包失败降级使用原生env
            messager.printMessage(Diagnostic.Kind.WARNING, "解包ProcessingEnvironment失败，使用原生环境:" + e.getMessage());
            this.trees = JavacTrees.instance(processingEnv);
            com.sun.tools.javac.processing.JavacProcessingEnvironment javacEnv
                    = (com.sun.tools.javac.processing.JavacProcessingEnvironment) processingEnv;
            com.sun.tools.javac.util.Context context = javacEnv.getContext();
            this.treeMaker = TreeMaker.instance(context);
            this.names = Names.instance(context);
            this.symtab = Symtab.instance(context);
            this.types = Types.instance(context);
        }
//        super.init(processingEnv);
//        // IDEA 包装环境解包
//        ProcessingEnvironment unwrappedEnv = jbUnwrap(ProcessingEnvironment.class, processingEnv);
//
//        this.messager = unwrappedEnv.getMessager();
//        this.trees = JavacTrees.instance(unwrappedEnv);
//        com.sun.tools.javac.util.Context context = ((com.sun.tools.javac.processing.JavacProcessingEnvironment) unwrappedEnv).getContext();
//        this.treeMaker = TreeMaker.instance(context);
//        this.names = Names.instance(context);
//        this.symtab = Symtab.instance(context);
//        this.types = Types.instance(context);
    }

    private static <T> T jbUnwrap(Class<? extends T> iface, T wrapper) {
        T unwrapped = null;
        try {
            final Class<?> apiWrappers = wrapper.getClass().getClassLoader()
                    .loadClass("org.jetbrains.jps.javac.APIWrappers");
            final Method unwrapMethod = apiWrappers.getDeclaredMethod("unwrap", Class.class, Object.class);
            unwrapped = iface.cast(unwrapMethod.invoke(null, iface, wrapper));
        } catch (Throwable ignored) {
        }
        return unwrapped != null ? unwrapped : wrapper;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> setDataBases = roundEnv.getElementsAnnotatedWith(SetDataBase.class);
        if (setDataBases != null) {
            for (Element element : roundEnv.getElementsAnnotatedWith(SetDataBase.class)) {
                SetDataBase setDataBase = element.getAnnotation(SetDataBase.class);
                if (setDataBase != null) {
                    Constant.dataBase = setDataBase.value();
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
                            messager.printMessage(Diagnostic.Kind.WARNING, "获取到属性列表：" + jcVariableDeclList.size());
                            java.util.List<JCTree.JCMethodDecl> printMethods = buildMethods(comBeanUtil.makeColumnNamesMethodDecl(table.supClass(), jcVariableDeclList), element, table, makeMethodDecl(table.supClass(), jcMethodDeclList));
                            if (printMethods != null && printMethods.size() > 0) {
                                for (JCTree.JCMethodDecl printMethod : printMethods) {
                                    jcClassDecl.defs = jcClassDecl.defs.append(printMethod);
                                }
                            }

                            JCTree.JCExpression qualIdent = createQualIdent("com.yilijishu.mybatis.iter.BaseBeanInterface", treeMaker, names);
                            jcClassDecl.implementing = jcClassDecl.implementing.append(qualIdent);
                            super.visitClassDef(jcClassDecl);
                        }
                    });
                    messager.printMessage(Diagnostic.Kind.WARNING, "解析完成：" + table);
                }
            }
        } else {
            messager.printMessage(Diagnostic.Kind.WARNING, "失败：" + set);
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
     * 判断有没有实现指定的接口
     *
     * @param jcClassDecl
     * @param interfaceClass 接口
     * @return 如果类已经实现了指定接口则返回true，否则返回false
     */
    private boolean hasInterface(JCTree.JCClassDecl jcClassDecl, Class<?> interfaceClass) {
        messager.printMessage(Diagnostic.Kind.WARNING, "开始判断接口是否存在");
        for (JCTree.JCExpression impl : jcClassDecl.implementing) {
            if (impl.type.toString().equals(interfaceClass.getName())) {
                messager.printMessage(Diagnostic.Kind.WARNING, "开始判断接口存在");
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
        messager.printMessage(Diagnostic.Kind.WARNING, "导入包");
        JCTree.JCCompilationUnit compilationUnit = (JCTree.JCCompilationUnit) trees.getPath(element).getCompilationUnit();
        JCTree.JCFieldAccess fieldAccess = treeMaker.Select(treeMaker.Ident(names.fromString(importClass.getPackage().getName())), names.fromString(importClass.getSimpleName()));
        JCTree.JCImport jcImport = treeMaker.Import(fieldAccess, false);
        ListBuffer<JCTree> imports = new ListBuffer<>();
        imports.add(jcImport);
        for (int i = 0; i < compilationUnit.defs.size(); i++) {
            imports.append(compilationUnit.defs.get(i));
        }
        compilationUnit.defs = imports.toList();
        messager.printMessage(Diagnostic.Kind.WARNING, "导入完成" + compilationUnit.defs);
    }

    /**
     * 添加实现接口.
     *
     * @param jcClassDecl    传递class decl
     * @param element        传递 element
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
            messager.printMessage(Diagnostic.Kind.WARNING, "开始创建实现类");
            Symbol.ClassSymbol sym = new Symbol.ClassSymbol(Flags.AccessFlags, names.fromString(interfaceClass.getSimpleName()), null);
            statements.append(treeMaker.Ident(sym));
            messager.printMessage(Diagnostic.Kind.WARNING, "开始替换类");
            jcClassDecl.implementing = statements.toList();
            messager.printMessage(Diagnostic.Kind.WARNING, "替换完成" + ((JCTree.JCCompilationUnit) trees.getPath(element).getCompilationUnit()).defs);
        }
    }


    private ComMethod makeMethodDecl(String supClass, List<JCTree.JCMethodDecl> jcMethodDeclList) {
        messager.printMessage(Diagnostic.Kind.WARNING, "开始执行方法对象");
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
        messager.printMessage(Diagnostic.Kind.WARNING, "开始执行超类方法对象");
        //Class<?> clss = Class.forName(supClass);
        //替换写法
        if (supClass != null && supClass.length() > 1) {
            AptUtil aptUtil = new AptUtil(processingEnv);
            TypeElement superType = aptUtil.getTypeElement(supClass);
            java.util.List<ExecutableElement> executableElements = aptUtil.getDeclaredMethods(superType);
            for (ExecutableElement method : executableElements) {
                String methodName = method.getSimpleName().toString();
                // 获取方法上指定注解
                OverrideOrderBy orderBy = method.getAnnotation(OverrideOrderBy.class);
                if (orderBy != null && comMethod.isOverrideOrderBy()) {
                    comMethod.setOverrideOrderBy(true);
                    comMethod.setOverriderOrderByMethod(methodName);
                }
                AddSelectCondition addSelectCondition = method.getAnnotation(AddSelectCondition.class);
                if (addSelectCondition != null) {
                    java.util.List<String> selects = comMethod.getAddSelectConditionMethod();
                    if (selects == null) {
                        selects = new ArrayList<>();
                        comMethod.setAddSelectCondition(true);
                        comMethod.setAddSelectConditionMethod(selects);
                    }
                    selects.add(methodName);
                }
            }
        }
        messager.printMessage(Diagnostic.Kind.WARNING, "方法返回：" + comMethod);
        return comMethod;
    }

    /**
     * 查询类信息、组装方法所需。并生成方法。
     *
     * @param supClass           父类
     * @param jcVariableDeclList 类中的所有属性
     * @return 返回method
     */


    /**
     * 加载数据、生成方法.
     *
     * @param comBeans  传递Combean列表
     * @param element   element对象
     * @param table     表名
     * @param comMethod 传递自定义方法
     * @return 方法定义列表
     */
    public java.util.List<JCTree.JCMethodDecl> buildMethods(java.util.List<ComBean> comBeans, Element element, Table table, ComMethod comMethod) {
        return comBeanUtil.analyser(comBeans, treeMaker, names, symtab, element, table, comMethod);
    }

    //insert
    //insertAll
    //update
    //select
    //

    /**
     * 生成方法（方法名， 步骤）
     *
     * @param method     要生成的名法名
     * @param statements 声明代码树列表
     * @return 返回方法声明
     */
    public JCTree.JCMethodDecl buildMethod(String method, ListBuffer<JCTree.JCStatement> statements) {
        return buildMethod(method, statements, "String");
    }

    /**
     * 生成方法（方法名， 步骤）
     *
     * @param method     要生成的名法名
     * @param statements 声明代码树列表
     * @param returnType 设定返回类型
     * @return 返回方法声明
     */
    public JCTree.JCMethodDecl buildMethod(String method, ListBuffer<JCTree.JCStatement> statements, String returnType) {
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        messager.printMessage(Diagnostic.Kind.WARNING, "代码块:" + body.toString());
        // 生成columnNames()方法
        return treeMaker
                .MethodDef(treeMaker.Modifiers(com.sun.tools.javac.code.Flags.PUBLIC), names.fromString(method),
                        treeMaker.Ident(names.fromString(returnType)),
                        List.nil(), List.nil(), List.nil(), body, null);
    }



}
