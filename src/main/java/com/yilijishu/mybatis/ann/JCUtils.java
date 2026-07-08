package com.yilijishu.mybatis.ann;

import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

import javax.tools.Diagnostic;

public class JCUtils {

    private TreeMaker treeMaker;

    private Names names;

    private Symtab symtab;

    public JCUtils(TreeMaker treeMaker, Names names, Symtab symtab) {
        this.treeMaker = treeMaker;
        this.names = names;
        this.symtab = symtab;
    }

    /**
     * new一个类
     *
     * @param className class名称
     * @return new类
     */
    public JCTree.JCNewClass newClass(String className) {
        return treeMaker.NewClass(
                null,
                List.nil(),
                treeMaker.Ident(names.fromString(className)),
                List.nil(),
                null
        );
    }

    /**
     * 根据方法名 获得 this.方法名调用
     *
     * @param methodName 方法名
     * @return 方法体
     */
    public JCTree.JCMethodInvocation getMethod(String methodName) {

        JCTree.JCIdent thisIdent = treeMaker.Ident(names.fromString("this"));
        JCTree.JCMethodInvocation getStatusCall = treeMaker.Apply(
                List.nil(),
                treeMaker.Select(thisIdent, names.fromString(methodName)),
                List.nil()
        );
        return getStatusCall;
    }


    /**
     * 获取方法不为空
     * @param methodName 方法名
     * @return 条件语句
     */
    public JCTree.JCBinary getMethodIsNotNull(String methodName) {

        JCTree.JCExpression nullLiteral = treeMaker.Literal(TypeTag.BOT, null);
        JCTree.JCBinary condition = treeMaker.Binary(
                JCTree.Tag.NE,
                getMethod(methodName),
                nullLiteral
        );
        return condition;
    }

    /**
     * 获取方法不为空
     * @param condition1 condition1
     * @param condition2 condition2
     * @return 条件语句
     */
    public JCTree.JCBinary getMethodIsNotNull(JCTree.JCBinary condition1, JCTree.JCBinary condition2) {
        return treeMaker.Binary(JCTree.Tag.AND, condition1, condition2);
    }


    /**
     * 执行方法， 比如 result.append("1111");
     *
     * @param instanceName 传递result
     * @param methodName   传递append
     * @param param        参数1111
     * @return 返回block
     */
    public JCTree.JCStatement applyMethod(String instanceName, String methodName, String param) {
        JCTree.JCIdent sbfIdent = treeMaker.Ident(names.fromString(instanceName));
        JCTree.JCMethodInvocation appendCall = treeMaker.Apply(
                List.nil(),
                treeMaker.Select(sbfIdent, names.fromString(methodName)),
                param != null ? List.of(treeMaker.Literal(param)) : List.nil()
        );
        JCTree.JCStatement appendStmt = treeMaker.Exec(appendCall);
        return appendStmt;
    }

    /**
     * 执行方法， 比如 result.append("1111");
     *
     * @param instanceName 传递result
     * @param methodName   传递append
     * @param instanceName2 传递result
     * @param methodName2        参数
     * @return 返回block
     */
    public JCTree.JCStatement applyMethod(String instanceName, String methodName, String instanceName2, String methodName2) {
        JCTree.JCIdent sbfIdent = treeMaker.Ident(names.fromString(instanceName));
        JCTree.JCIdent ident2 = treeMaker.Ident(names.fromString(instanceName2));
        JCTree.JCMethodInvocation call = treeMaker.Apply(
                List.nil(),
                treeMaker.Select(ident2, names.fromString(methodName2)),
                List.nil()
        );
        JCTree.JCMethodInvocation appendCall = treeMaker.Apply(
                List.nil(),
                treeMaker.Select(sbfIdent, names.fromString(methodName)),
                List.of(call)
        );
        JCTree.JCStatement appendStmt = treeMaker.Exec(appendCall);
        return appendStmt;
    }

    /**
     * IF语句拼接
     *
     * @param condition 条件语句
     * @param apply     执行
     * @return
     */
    public JCTree.JCIf ifApply(JCTree.JCBinary condition, JCTree.JCStatement apply) {
        ListBuffer<JCTree.JCStatement> ifBodyStmts = new ListBuffer<>();
        ifBodyStmts.append(apply);
        JCTree.JCBlock ifBlock = treeMaker.Block(0, ifBodyStmts.toList());
        JCTree.JCIf ifStmt = treeMaker.If(condition, ifBlock, null);
        return ifStmt;
    }

    /**
     * IF语句拼接
     *
     * @param condition 条件语句
     * @param apply     执行
     * @param apply2  else执行
     * @return
     */
    public JCTree.JCIf ifApply(JCTree.JCBinary condition, JCTree.JCStatement apply, JCTree.JCStatement apply2) {
        ListBuffer<JCTree.JCStatement> ifBodyStmts = new ListBuffer<>();
        ifBodyStmts.append(apply);
        JCTree.JCBlock ifBlock = treeMaker.Block(0, ifBodyStmts.toList());
        ListBuffer<JCTree.JCStatement> elseBodyStmts = new ListBuffer<>();
        elseBodyStmts.append(apply2);
        JCTree.JCBlock elseBlock = treeMaker.Block(0, elseBodyStmts.toList());
        JCTree.JCIf ifStmt = treeMaker.If(condition, ifBlock, elseBlock);
        return ifStmt;
    }


    /**
     * 初始化 定义result  StringBuilder result = new StringBuilder();
     * @return
     */
    public JCTree.JCStatement initResult() {
        return treeMaker.VarDef(treeMaker.Modifiers(0), names.fromString("reuslt"), treeMaker.Ident(names.fromString("StringBuilder")), this.newClass("StringBuilder"));
    }


    /**
     * 执行
     * @param methodName
     * @return
     */
    public JCTree.JCStatement forExec(String methodName, String appendStr) {
        return ifApply(getMethodIsNotNull(methodName), applyMethod("result", "append", appendStr));
    }


    /**
     * 执行
     * @param methodName
     * @return
     */
    public JCTree.JCStatement forExec2(String methodName, String methodName2, String appendStr) {
        JCTree.JCBinary jcBinary = getMethodIsNotNull(getMethodIsNotNull(methodName), getMethodIsNotNull(methodName2));
        return ifApply(jcBinary, applyMethod("result", "append", appendStr));
    }


    /**
     * 返回数据
     * @return
     */
    public JCTree.JCReturn returnMethod() {
        return returnMethod(1);
    }
    public JCTree.JCReturn returnMethod(int subNum) {
        JCTree.JCIdent ident = treeMaker.Ident(names.fromString("result"));
        JCTree.JCMethodInvocation toStringCall = treeMaker.Apply(
                List.nil(),
                treeMaker.Select(ident, names.fromString("toString")),
                List.nil()
        );
        // sbf.length() - 1
        JCTree.JCMethodInvocation lengthCall = treeMaker.Apply(
                List.nil(),
                treeMaker.Select(ident, names.fromString("length")),
                List.nil()
        );
        JCTree.JCBinary minusExpr = treeMaker.Binary(JCTree.Tag.MINUS, lengthCall, treeMaker.Literal(1));
        // substring(0, ...)
        JCTree.JCMethodInvocation substringCall = treeMaker.Apply(
                List.nil(),
                treeMaker.Select(toStringCall, names.fromString("substring")),
                List.of(treeMaker.Literal(0), minusExpr)
        );
        // return
        JCTree.JCReturn returnStmt = treeMaker.Return(substringCall);
        return returnStmt;
    }



    /**
     * 生成返回String的方法（方法名， 字符串）
     *
     * @param method 方法名
     * @param str    返回的字符串
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
                        com.sun.tools.javac.util.List.nil(), com.sun.tools.javac.util.List.nil(), com.sun.tools.javac.util.List.nil(), body, null);
    }

    /**
     * 生成返回boolean方法（方法名， 字符串）
     *
     * @param method 方法名
     * @param bool   是否
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
                        com.sun.tools.javac.util.List.nil(), com.sun.tools.javac.util.List.nil(), com.sun.tools.javac.util.List.nil(), body, null);
    }

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
        // 生成columnNames()方法
        return treeMaker
                .MethodDef(treeMaker.Modifiers(com.sun.tools.javac.code.Flags.PUBLIC), names.fromString(method),
                        treeMaker.Ident(names.fromString(returnType)),
                        List.nil(), List.nil(), List.nil(), body, null);
    }

    /**
     * 创建 域/方法 的多级访问, 方法的标识只能是最后一个
     *
     * @param components
     * @return
     */
    public JCTree.JCExpression memberAccess(String components) {
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
    public com.sun.tools.javac.util.Name getNameFromString(String s) {
        return names.fromString(s);
    }

}
