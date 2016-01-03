package com.hauchee.annotationprocessor;

import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor7;

class PojoAnnotationValueVisitor extends SimpleAnnotationValueVisitor7<Void, String> {

    private final List<TypeName> fieldTypes = new ArrayList<>();
    private final List<String> fieldNames = new ArrayList<>();
    private final List<String> fieldsAnnotatedWithPojo = new ArrayList<>();

    // Only need to override the visitArray() method because both 'fieldTypes' and
    // 'fieldNames' are array.
    @Override
    public Void visitArray(List<? extends AnnotationValue> vals, String elementName) {
        for (AnnotationValue val : vals) {
            Object value = val.getValue();
            switch (elementName) {
                case "fieldTypes":
                    fieldTypes.add(TypeName.get((TypeMirror) value));
                    break;
                case "fieldNames":
                    fieldNames.add((String) value);
                    break;
                case "fieldsAnnotatedWithPojo":
                    fieldsAnnotatedWithPojo.add((String) value);
                    break;
            }
        }

        return null;
    }

    void validateValues() throws ProcessingException {
        if (fieldNames.size() != fieldTypes.size()) {
            throw new ProcessingException(
                    "The length of 'fieldNames' must be same as the length of 'fieldTypes'.");
        }
        validateEmptyElement(fieldNames, "fieldNames");
        validateEmptyElement(fieldsAnnotatedWithPojo, "pojoAnnotatedFields");
    }

    private void validateEmptyElement(List<String> elements, String name)
            throws ProcessingException {
        
        int i = 0;
        for (String fieldName : elements) {
            if (fieldName.trim().isEmpty()) {
                throw new ProcessingException(
                        "\"%s\" with element index \"%d\" must not be an empty String.", name, i);
            }
            i++;
        }
    }

    public List<TypeName> getFieldTypes() {
        return fieldTypes;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public List<String> getFieldsAnnotatedWithPojo() {
        return fieldsAnnotatedWithPojo;
    }
}
