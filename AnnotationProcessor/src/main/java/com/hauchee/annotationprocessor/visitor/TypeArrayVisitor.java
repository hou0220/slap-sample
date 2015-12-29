package com.hauchee.annotationprocessor.visitor;

import com.squareup.javapoet.TypeName;
import java.util.List;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor7;

public class TypeArrayVisitor extends SimpleAnnotationValueVisitor7<Void, List<TypeName>> {
    
    private final TypeVisitor fieldTypeVisitor = new TypeVisitor();
    
    @Override
    public Void visitArray(List<? extends AnnotationValue> vals, List<TypeName> typeNames) {
        for (AnnotationValue val : vals) {
            TypeName typeName = val.accept(fieldTypeVisitor, null);
            typeNames.add(typeName);
        }
        return null;
    }
    
    private static class TypeVisitor extends SimpleAnnotationValueVisitor7<TypeName, Void> {

        @Override
        public TypeName visitType(TypeMirror typeMirror, Void p) {
            return TypeName.get(typeMirror);
        }
    }
}
