package com.hauchee.annotationprocessor.visitor;

import java.util.List;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.util.SimpleAnnotationValueVisitor7;

public class StringArrayVisitor extends SimpleAnnotationValueVisitor7<Void, List<String>> {
    
    @Override
    public Void visitArray(List<? extends AnnotationValue> vals, List<String> list) {
        for (AnnotationValue val : vals) {
            String str = (String) val.getValue();
            list.add(str.trim().toLowerCase());
        }
        return null;
    }
}
