package lyu.network.connect.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;
import lyu.network.connect.annotation.NetworkSubscribe;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("lyu.network.connect.annotation.NetworkSubscribe")
public class NetworkConnectProcessor extends AbstractProcessor {

    private static final String TARGET = "target";
    
    private Messager messager;
    
    private Filer filer;
    
    private Map<TypeElement, List<Element>> tempConnectMap;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        messager.printMessage(Diagnostic.Kind.NOTE, "init");
        tempConnectMap = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE, "process");
        if(set == null || set.isEmpty()) return  false;

        // 获取所有的注解节点
        Set<? extends Element> connectElements = roundEnvironment.getElementsAnnotatedWith(NetworkSubscribe.class);

        if(connectElements == null || connectElements.isEmpty()) return false;

        valueOfMap(connectElements);

        try {
            createJavaFile();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void createJavaFile() throws Exception {
        if(tempConnectMap.isEmpty()) throw new IllegalStateException("没有找到该注解");

        ClassName netSubscribeType = ClassName.get("lyu.network.connect.annotation","NetSubscribe");
        ClassName netStatusType = ClassName.get("lyu.network.connect","NetStatus");
        ClassName networkConnectObserver = ClassName.get("lyu.network.connect.observer", "NetworkConnectObserver");
        ClassName networkManager =  ClassName.get("lyu.network.connect", "NetworkManager");
        ClassName networkConnectBinder = ClassName.get("lyu.network.connect", "NetworkConnectBinder");

        for (Map.Entry<TypeElement, List<Element>> entry : tempConnectMap.entrySet()) {

            List<Element> methodElements = entry.getValue();
            ClassName targetClassName = ClassName.get(entry.getKey());

            ParameterSpec parameterSpec = ParameterSpec.builder(targetClassName, TARGET).build();

            // 构造bind 方法
            MethodSpec.Builder bindMethodBuilder = MethodSpec.methodBuilder("bind")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(parameterSpec)
                    .returns(void.class)
                    .addStatement("$T<$T> observers = new $T<>()",
                            List.class,
                            networkConnectObserver,
                            ArrayList.class);


            for (int i = 0; i < methodElements.size(); i++) {

                ExecutableElement  methodElement = (ExecutableElement) methodElements.get(i);

                String methodName = methodElement.getSimpleName().toString();

                Object result = findAnnotationValueByDeclaredType(methodElement);

                String parameterName = getParameterNameAndCheck(methodElement, result);

                TypeSpec.Builder listenerBuilder;
                if (result == null) {
                    listenerBuilder = TypeSpec.anonymousClassBuilder("$T.$N", netSubscribeType, "ALL");
                } else {
                    listenerBuilder = TypeSpec.anonymousClassBuilder("$T.$L", netSubscribeType, result);
                }

                MethodSpec.Builder updateMethodBuilder = MethodSpec.methodBuilder("update")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Observable.class, "observer")
                        .addParameter(Object.class, "netStatus")
                        .returns(void.class);
                MethodSpec updateMethod;
                if (parameterName == null) {
                    updateMethod = updateMethodBuilder
                            .addStatement("$N.$N()", TARGET, methodName)
                            .build();
                } else {
                    updateMethod = updateMethodBuilder
                            .addStatement("$N.$N(($T)$N)", TARGET, methodName, netStatusType, "netStatus")
                            .build();
                }

                TypeSpec listener = listenerBuilder
                    .superclass(networkConnectObserver)
                    .addMethod(updateMethod)
                    .build();


                bindMethodBuilder.addStatement("observers.add($L)", listener);
            };

            bindMethodBuilder.addStatement("$T.getInstance().addObservers($N, observers)",
                    networkManager,
                    TARGET);
            MethodSpec bindMethodSpec = bindMethodBuilder.build();

            // 构造unBind方法
            MethodSpec unBindMethodSpec = MethodSpec.methodBuilder("unBind")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(parameterSpec)
                    .returns(void.class)
                    .addStatement("$T.getInstance().removeObservers($N)",
                            networkManager,
                            TARGET)
                    .build();


            // 构造类
            ParameterizedTypeName binderTypeName = ParameterizedTypeName.get(
                    networkConnectBinder,
                    targetClassName);

            TypeSpec typeSpec = TypeSpec.classBuilder(targetClassName.simpleName() + "$NetworkSubscribe")
                    .addSuperinterface(binderTypeName)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(bindMethodSpec)
                    .addMethod(unBindMethodSpec)
                    .build();

            JavaFile.builder(targetClassName.packageName(), typeSpec)
                    .build()
                    .writeTo(filer);
        }

    }

    /**
     * 通过 DeclaredType 查找方法上 NetworkSubscribe 注解的值
     * @param methodElement
     * @return
     */
    private Object findAnnotationValueByDeclaredType(ExecutableElement  methodElement) {
        List<? extends AnnotationMirror> annotationMirrors = methodElement.getAnnotationMirrors();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            DeclaredType type = annotationMirror.getAnnotationType();
            if ("lyu.network.connect.annotation.NetworkSubscribe".equals(type.toString())) {
                Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
                return getAnnotationValue(elementValues, "value");
            }
        }
        return null;
    }

    /**
     * 获取方法中的第一个参数，并作规则校验
     * @param methodElement
     * @param result
     * @return
     * @throws Exception
     */
    private String getParameterNameAndCheck(ExecutableElement  methodElement, Object result) throws Exception {
        List<? extends VariableElement> parameters = methodElement.getParameters();

        if (parameters == null) {
            return null;
        }

        int size = parameters.size();
        String subscribeName = result.toString();
        if ("ALL".equals(subscribeName)) {
            if (size != 1) {
                throw new IllegalArgumentException("注解为 OTHER 时不需要传参数");
            }

            if (!"lyu.network.connect.NetStatus".equals(parameters.get(0).asType().toString())) {
                throw new IllegalArgumentException("注解为 ALL 时需要传入参数 NetStatus");
            }
            return parameters.get(0).getSimpleName().toString();
        } else {
            if (size != 0) {
                throw new IllegalArgumentException("注解为 OTHER 时不需要传参数");
            }
            return null;
        }
    }

    /**
     * 在注解中的所有值中取出我们的值
     * @param values
     * @param annotationFiledName
     * @return
     */
    private Object getAnnotationValue(
            Map<? extends ExecutableElement, ? extends AnnotationValue> values,
            String annotationFiledName) {

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : values.entrySet()) {
            if (entry.getKey().getSimpleName().contentEquals(annotationFiledName)) {
                return entry.getValue().getValue();
            }
        }
        return null;
    }

    /**
     * 缓存所有的节点
     * @param connectElements
     */
    private void valueOfMap(Set<? extends Element> connectElements) {

        for (Element connectElement : connectElements) {
            // 获取父类 节点
            TypeElement typeElement = (TypeElement) connectElement.getEnclosingElement();

            if (tempConnectMap.containsKey(typeElement)) {
                tempConnectMap.get(typeElement).add(connectElement);
            } else {
                List<Element> list = new ArrayList<>();
                list.add(connectElement);
                tempConnectMap.put(typeElement, list);
            }
        }
        
    }
}
