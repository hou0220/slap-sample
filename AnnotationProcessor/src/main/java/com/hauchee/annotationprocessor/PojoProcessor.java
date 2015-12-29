package com.hauchee.annotationprocessor;

import com.hauchee.annotationprocessor.visitor.StringArrayVisitor;
import com.hauchee.annotationprocessor.visitor.TypeArrayVisitor;
import com.google.auto.service.AutoService;
import com.hauchee.annotation.Pojo;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.hauchee.annotation.Pojo")
public class PojoProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    private VariableElement currentVariableElement;
    private AnnotationMirror currentAnnotationMirror;
    
    private final TypeArrayVisitor typeArrayVisitor = new TypeArrayVisitor();
    private final StringArrayVisitor stringArrayVisitor = new StringArrayVisitor();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Pojo.class)) {

                preProcess((VariableElement) annotatedElement);
                
                PackageElement pkgElement = elementUtils.getPackageOf(currentVariableElement.getEnclosingElement());
                PojoSourceCodeBuilder sourceCodeBuilder = new PojoSourceCodeBuilder(filer, pkgElement);
                
                List<TypeName> fieldTypes = new ArrayList<>();
                List<String> fieldNames = new ArrayList<>();
                List<String> pojoAnnotatedFields = new ArrayList<>();
                Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = currentAnnotationMirror.getElementValues();
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
                    String key = entry.getKey().getSimpleName().toString();
                    switch (key) {
                        case "fieldTypes":
                            entry.getValue().accept(typeArrayVisitor, fieldTypes);
                            break;
                        case "fieldNames":
                            entry.getValue().accept(stringArrayVisitor, fieldNames);
                            break;
                        case "pojoAnnotatedFields":
                            entry.getValue().accept(stringArrayVisitor, pojoAnnotatedFields);
                            break;
                    }
                }

                validateValues(fieldNames, fieldTypes, pojoAnnotatedFields);

                /**
                 * Process field names and types. *
                 */
                for (int i = 0; i < fieldTypes.size(); i++) {
                    TypeName fieldType = fieldTypes.get(i);
                    String fieldName = fieldNames.get(i);

                    // Skip for default value.
                    if (fieldName.equals("N/A") && fieldType.toString().equals("java.lang.Void")) {
                        i++;
                        continue;
                    }

                    sourceCodeBuilder.addFieldWithGetterSetter(fieldName, fieldType);
                }

                /**
                 * Process fields that to be annotated with Pojo annotation. *
                 */
                for (String oriPojoAnnotatedField : pojoAnnotatedFields) {

                    // Skip for default value.
                    if (oriPojoAnnotatedField.equals("N/A")) {
                        continue;
                    }

                    String pojoAnnotatedField = oriPojoAnnotatedField;
                    String pojoAnnotatedFieldName = pojoAnnotatedField.substring(0, 1).toUpperCase()
                            + pojoAnnotatedField.substring(1);
                    ClassName pojoAnnotatedFieldType = ClassName.get(sourceCodeBuilder.getPackageName(), pojoAnnotatedFieldName);

                    sourceCodeBuilder.addAnnotatedFiedWithGetterSetter(pojoAnnotatedField, pojoAnnotatedFieldType, Pojo.class);
                }

                sourceCodeBuilder.writeToJavaFile(currentVariableElement.asType().toString());
            }
        } catch (ProcessingException e) {
            error(e, e.getMessage());
        } catch (IOException e) {
            error(null, e.getMessage());
        }

        return true;
    }

    private void preProcess(VariableElement varElement) {
        this.currentVariableElement = varElement;
        
        List<? extends AnnotationMirror> allAnnotationMirrors
                = elementUtils.getAllAnnotationMirrors(currentVariableElement);
        for (AnnotationMirror annotationMirror : allAnnotationMirrors) {
            if (annotationMirror.getAnnotationType().toString().equals(Pojo.class.getName())) {
                currentAnnotationMirror = annotationMirror;
                break;
            }
        }
    }

    private void validateValues(List<String> fieldNames, List<TypeName> fieldTypes, List<String> pojoAnnotatedFields) throws ProcessingException {
        if (fieldNames.size() != fieldTypes.size()) {
            throw new ProcessingException(currentVariableElement, currentAnnotationMirror,
                    "The length of 'fieldNames' must be same as the length of 'fieldTypes'.");
        }
        validateEmptyElement(fieldNames, "fieldNames");
        validateEmptyElement(pojoAnnotatedFields, "pojoAnnotatedFields");
    }

    private void validateEmptyElement(List<String> elements, String name) throws ProcessingException {
        int i = 0;
        for (String fieldName : elements) {
            if (fieldName.trim().isEmpty()) {
                throw new ProcessingException(currentVariableElement, currentAnnotationMirror,
                        "\"%s\" with element index \"%d\" must not be an empty String.", name, i);
            }
            i++;
        }
    }

    private void error(ProcessingException pe, String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, pe.getElement(), pe.getAnnotationMirror());
    }
}
