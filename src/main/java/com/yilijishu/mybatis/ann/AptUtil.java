package com.yilijishu.mybatis.ann;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.stream.Collectors;

public class AptUtil {


    private final Elements elements;
    private final ProcessingEnvironment processingEnv;

    public AptUtil(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.elements = processingEnv.getElementUtils();
    }


    /**
     * 根据全类名获取 TypeElement
     * @param fullClassName  类全名
     * @return type
     */
    public TypeElement getTypeElement(String fullClassName) {
        TypeElement typeElement = elements.getTypeElement(fullClassName);
        if (typeElement == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "未找到类：" + fullClassName);
        }
        return typeElement;
    }

    /**
     * 获取当前类 + 所有父类中【所有字段属性】
     * @param typeElement typeElement
     * @return list
     */
    public List<VariableElement> getAllFields(TypeElement typeElement) {
        return elements.getAllMembers(typeElement).stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * 只获取当前类直接定义的字段（不包含父类）
     * @param typeElement typeElement
     * @return list
     */
    public List<VariableElement> getDeclaredFields(TypeElement typeElement) {
        return typeElement.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * 获取当前类 + 所有父类中【所有方法】（包含构造器）
     * @param typeElement typeElement
     * @return list
     */
    public List<ExecutableElement> getAllMethods(TypeElement typeElement) {
        return elements.getAllMembers(typeElement).stream()
                .filter(e -> e.getKind() == ElementKind.METHOD || e.getKind() == ElementKind.CONSTRUCTOR)
                .map(ExecutableElement.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * 只获取当前类直接定义的方法（不含父类、不含构造器）
     * @param typeElement typeElement
     * @return list
     */
    public List<ExecutableElement> getDeclaredMethods(TypeElement typeElement) {
        return typeElement.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast).collect(Collectors.toList());
    }


}
