package com.hauchee.annotationprocessor;

import com.google.auto.service.AutoService;
import com.hauchee.annotation.Tag;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor7;

/**
 * This annotation processor demonstrating different approaches of getting class
 * annotation value.
 * 
 * @author HauChee
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.hauchee.annotation.Tag")
public class TagProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element typeElement : roundEnv.getElementsAnnotatedWith(Tag.class)) {
            System.out.println("From the Annotation object:");
            // Get the annotation object
            Tag tagAnnotation = typeElement.getAnnotation(Tag.class);
            System.out.printf(">> stringValue: %s\n",
                    tagAnnotation.stringValue());
            System.out.printf(">> intValue: %s\n",
                    tagAnnotation.intValue());
            System.out.printf(">> enumValue: %s\n",
                    tagAnnotation.enumValue().toString());
            System.out.printf(">> annotationTypeValue: %s\n",
                    tagAnnotation.annotationTypeValue().toString());
            try {
                System.out.printf(">> classValue: %s\n", // never print
                        tagAnnotation.classValue().getName());
            } catch (MirroredTypeException e) {
                TypeMirror typeMirror = e.getTypeMirror();
                System.out.printf(">> classValue(MirroredTypeException): %s\n",
                        typeMirror.toString());
            }
            try {
                System.out.printf(">> classesValue: %s\n", // never print
                        tagAnnotation.classesValue()[0].getName());
            } catch (MirroredTypesException e) {
                List<? extends TypeMirror> typeMirrors = e.getTypeMirrors();
                System.out.printf(">> classValue(MirroredTypesException): %s\n",
                        typeMirrors.get(0).toString());
            }
            
            ////////////////////////////////////////////////////////////////////
            
            System.out.println("From the Annotation element:");
            // Get the annotation element from the type element
            List<? extends AnnotationMirror> annotationMirrors = typeElement.getAnnotationMirrors();
            for (AnnotationMirror annotationMirror : annotationMirrors) {

                // Get the ExecutableElement:AnnotationValue pairs from the annotation element
                Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues
                        = annotationMirror.getElementValues();
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                        : elementValues.entrySet()) {
                    String key = entry.getKey().getSimpleName().toString();
                    Object value = entry.getValue().getValue();
                    switch (key) {
                        case "intValue":
                            int intVal = (Integer) value;
                            System.out.printf(">> intValue: %d\n", intVal);
                            break;
                        case "stringValue":
                            String strVal = (String) value;
                            System.out.printf(">> stringValue: %s\n", strVal);
                            break;
                        case "enumValue":
                            VariableElement enumVal = ((VariableElement) value);
                            System.out.printf(">> enumValue: %s\n", enumVal.getSimpleName());
                            break;
                        case "annotationTypeValue":
                            AnnotationMirror anoTypeVal = (AnnotationMirror) value;
                            System.out.printf(">> annotationTypeValue: %s\n", anoTypeVal.toString());
                            break;
                        case "classValue":
                            TypeMirror typeMirror = (TypeMirror) value;
                            System.out.printf(">> classValue: %s\n", typeMirror.toString());
                            break;
                        case "classesValue":
                            List<? extends AnnotationValue> typeMirrors
                                = (List<? extends AnnotationValue>) value;
                            System.out.printf(">> classesValue: %s\n",
                                ((TypeMirror) typeMirrors.get(0).getValue()).toString());
                            break;
                    }
                }
            }
            
            ////////////////////////////////////////////////////////////////////////////
            
            System.out.println("From the Annotation Value Visitor:");
            // The annotation value visitor that is able to assess all annotation value
            MyValueVisitor valueVisitor = new MyValueVisitor();
            // Get the annotation element from the type element
            //List<? extends AnnotationMirror> annotationMirrors = typeElement.getAnnotationMirrors();
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues
                        = annotationMirror.getElementValues();
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                        : elementValues.entrySet()) {
                    entry.getValue().accept(valueVisitor, null);
                }
            }
        }
        return true;
    }

    private static class MyValueVisitor extends SimpleAnnotationValueVisitor7<Void, Void> {

        @Override
        public Void visitInt(int i, Void p) {
            System.out.printf(">> intValue: %d\n", i);
            return p;
        }

        @Override
        public Void visitString(String s, Void p) {
            System.out.printf(">> stringValue: %s\n", s);
            return p;
        }

        @Override
        public Void visitEnumConstant(VariableElement c, Void p) {
            System.out.printf(">> enumValue: %s\n", c.getSimpleName());
            return p;
        }

        @Override
        public Void visitAnnotation(AnnotationMirror a, Void p) {
            System.out.printf(">> annotationTypeValue: %s\n", a.toString());
            return p;
        }

        @Override
        public Void visitType(TypeMirror t, Void p) {
            System.out.printf(">> classValue: %s\n", t.toString());
            return p;
        }

        @Override
        public Void visitArray(List<? extends AnnotationValue> vals, Void p) {
            for (AnnotationValue val : vals) {
                val.accept(this, p);
            }
            return p;
        }
    }
}
