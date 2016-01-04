package com.hauchee.annotationprocessor;

import com.hauchee.annotation.PrivateField;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * This annotation processor demonstrates the best way of handling error in annotation processing.
 *
 * @author HauChee
 */
public class PrivateFieldProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        
        useMessager(roundEnv);
        
//        throwRuntimeExceptionDirectly(roundEnv);
        
//        return returnFalseFromProcessMethod(roundEnv);
        
        return true;
    }
    
    /**
     * Improper approach.
     * 
     * The error is giving a misleading impression that the compiler is not functioning properly.
     * The error does not tell specific detail such as which Java class introduces the problem.
     */
    private void throwRuntimeExceptionDirectly(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(PrivateField.class)) {
            if (!element.getModifiers().contains(Modifier.PRIVATE)) {
                throw new IllegalArgumentException(
                        String.format("The field \"%s\" is not private.",
                                element.getSimpleName()));
            }
        }
    }
    
    /**
     * Incorrect approach.
     * 
     * Misused of the return parameter of process() method. By returning false, the annotation processor
     * will excuse itself from claiming this annotation type.
     */
    private boolean returnFalseFromProcessMethod(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(PrivateField.class)) {
            if (!element.getModifiers().contains(Modifier.PRIVATE)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Preferable approach.
     * 
     * It specifically tells us that which Java class, at which line causing the compilation error.
     */
    private void useMessager(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(PrivateField.class)) {
            if (!element.getModifiers().contains(Modifier.PRIVATE)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    String.format("The field \"%s\" is not private.",
                            element.getSimpleName()), element);
            }
        }
    }
}
