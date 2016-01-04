package com.hauchee.annotationprocessor;

import com.google.auto.service.AutoService;
import com.hauchee.annotation.Pojo;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import java.io.IOException;
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

/**
 * This annotation processor demonstrating code generation and processing rounds.
 * 
 * @author HauChee
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.hauchee.annotation.Pojo")
public class PojoProcessor extends AbstractProcessor {

    private Elements elementsUtil;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementsUtil = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv) {

        // The variable element which annotated with @Pojo annotation.
        VariableElement varElement = null;

        // The @Pojo annotation element.
        AnnotationMirror annotationMirror = null;

        try {
            // This return variable element that annotated with @Pojo annotation.
            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Pojo.class)) {
                varElement = (VariableElement) annotatedElement;

                // Getting all the annotation elements from the variable element.
                List<? extends AnnotationMirror> allAnnotationMirrors
                        = varElement.getAnnotationMirrors();
                for (AnnotationMirror aAnnotationMirror : allAnnotationMirrors) {

                    // Make sure the annotation element is belong to Pojo annotation type.
                    if (aAnnotationMirror.getAnnotationType().toString()
                            .equals(Pojo.class.getName())) {

                        // Found the @Pojo element.
                        annotationMirror = aAnnotationMirror;

                        // Create a visitor instance.
                        PojoAnnotationValueVisitor visitor = new PojoAnnotationValueVisitor();

                        // Getting annotation element values from the @Pojo element.
                        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues
                                = annotationMirror.getElementValues();
                        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
                            // The 'entry.getKey()' here is the annotation attribute name.
                            String key = entry.getKey().getSimpleName().toString(); 
                            
                            // The 'entry.getValue()' here is the annotation value which could accept a visitor.
                            entry.getValue().accept(visitor, key);
                        }

                        visitor.validateValues(); // Throw ProcessingException if annotation value is invalid.
                        buildPojoClass(varElement, visitor);
                        break;

                    }
                }

            }
        } catch (ProcessingException e) {
            error(varElement, annotationMirror, e.getMessage());
        } catch (IOException e) {
            error(null, null, e.getMessage());
        }

        return true;
    }

    private void error(VariableElement varElement, AnnotationMirror annotationMirror,
            String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, varElement, annotationMirror
        );
    }

    private void buildPojoClass(
            VariableElement varElement, PojoAnnotationValueVisitor visitor)
            throws IOException {

        List<String> fieldNames = visitor.getFieldNames();
        List<TypeName> fieldTypes = visitor.getFieldTypes();
        List<String> fieldsAnnotatedWithPojo = visitor.getFieldsAnnotatedWithPojo();

        PackageElement pkgElement = elementsUtil.getPackageOf(varElement.getEnclosingElement());
        
        // Create a source code builder instance.
        PojoSourceCodeBuilder sourceCodeBuilder = new PojoSourceCodeBuilder(filer, pkgElement);

        // Process field names and types.
        for (int i = 0; i < fieldTypes.size(); i++) {
            TypeName fieldType = fieldTypes.get(i);
            String fieldName = fieldNames.get(i);

            sourceCodeBuilder.addFieldWithGetterSetter(fieldName, fieldType);
        }

        // Process fields that to be annotated with Pojo annotation.
        for (String aFieldAnnotatedWithPojo : fieldsAnnotatedWithPojo) {
            String fieldAnnotatedWithPojo = aFieldAnnotatedWithPojo;
            String pojoAnnotatedFieldName = fieldAnnotatedWithPojo.substring(0, 1).toUpperCase()
                    + fieldAnnotatedWithPojo.substring(1);
            ClassName fieldType = ClassName.get(sourceCodeBuilder.getPackageName(), pojoAnnotatedFieldName);

            sourceCodeBuilder
                    .addAnnotatedFieldWithGetterSetter(fieldAnnotatedWithPojo, fieldType, Pojo.class);
        }

        // Write to the new Java source file.
        sourceCodeBuilder.writeToJavaFile(varElement.asType().toString());
    }
}
