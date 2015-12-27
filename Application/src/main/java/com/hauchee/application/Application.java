package com.hauchee.application; // PackageElement

import com.hauchee.annotation.Priority;
import com.hauchee.annotation.Tag;
import com.hauchee.annotation.Tag.Kind;

@Tag( // AnnotationMirror
    // ExecutableElement:AnnotationValue pairs
    stringValue = "A String Value",
    intValue = 100, 
    enumValue = Kind.STANDARD, 
    annotationTypeValue = @Priority(3), 
    classValue = Application.class, 
    classesValue = {Application.class}
)
public class Application { // TypeElement
    
    private int id; // VariableElement
    
    public void setId(int id) { // ExecutableElement
        this.id = id;
    }
    
    public int getId() { // ExecutableElement
        return this.id;
    }
}
