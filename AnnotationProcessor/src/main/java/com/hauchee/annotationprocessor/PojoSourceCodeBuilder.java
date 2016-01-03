package com.hauchee.annotationprocessor;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;

class PojoSourceCodeBuilder {
    
    private final Filer filer;
    private final String packageName;
    private final List<FieldSpec> fieldSpecs;
    private final List<MethodSpec> methodSpecs;
    
    public PojoSourceCodeBuilder(Filer filer, PackageElement pkgElement) {
        this.filer = filer;
        this.packageName = pkgElement.isUnnamed()
                ? null : pkgElement.getQualifiedName().toString();
        this.fieldSpecs = new ArrayList<>();
        this.methodSpecs = new ArrayList<>();
    }

    private void addGetterSetterMethods(String fieldName, TypeName fieldType) {
        String nameForGetterSetter
                = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);         

        /** Getter method spec. **/
        methodSpecs.add(MethodSpec.methodBuilder("get" + nameForGetterSetter)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return " + fieldName)
                .returns(fieldType).build());

        /** Setter method spec. **/
        methodSpecs.add(MethodSpec.methodBuilder("set" + nameForGetterSetter)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldType, fieldName, Modifier.FINAL)
                .addStatement("this." + fieldName + " = " + fieldName)
                .returns(TypeName.VOID).build());
    }
    
    public PojoSourceCodeBuilder addFieldWithGetterSetter(String fieldName, TypeName fieldType) {
        fieldSpecs.add(FieldSpec.builder(fieldType, fieldName, Modifier.PRIVATE).build());
        addGetterSetterMethods(fieldName, fieldType);
        return this;
    }
    
    // To add annotated field with getter and setter method.
    public PojoSourceCodeBuilder addAnnotatedFieldWithGetterSetter(
            String fieldName, TypeName fieldType, Class annotationClass) {
        
        fieldSpecs.add(FieldSpec.builder(fieldType, fieldName, Modifier.PRIVATE)
                .addAnnotation(annotationClass).build());
        addGetterSetterMethods(fieldName, fieldType);
        return this;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public void writeToJavaFile(String className) throws IOException {
        TypeSpec typeSpec = TypeSpec.classBuilder(className).addFields(fieldSpecs)
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methodSpecs).build();
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
    }
}
