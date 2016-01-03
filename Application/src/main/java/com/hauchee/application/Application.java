package com.hauchee.application; // PackageElement

import com.hauchee.annotation.Pojo;

public class Application { // TypeElement
    
    private String id; // VariableElement
    
    @Pojo( // AnnotationMirror
        // ExecutableElement:AnnotationValue pairs    
        fieldNames = {"name", "price"}, 
        fieldTypes = {String.class, Double.class},
        fieldsAnnotatedWithPojo = {"Category"}
    )
    private Game game; // VariableElement

    public Application(String id, Game game) { // ExecutableElement
        this.id = id;
        this.game = game;
    }

    public String getId() { // ExecutableElement
        return id;
    }

    public Game getGame() { // ExecutableElement
        return game;
    }
}




//@Tag( // AnnotationMirror
//    // ExecutableElement:AnnotationValue pairs
//    stringValue = "A String Value",
//    intValue = 100, 
//    enumValue = Kind.STANDARD, 
//    annotationTypeValue = @Priority(3), 
//    classValue = Application.class, 
//    classesValue = {Application.class}
//)
//public class Application { // TypeElement
//    
//    private int id; // VariableElement
//    
//    @Pojo(fieldNames = {"name"}, fieldTypes = {String.class}, fieldsAnnotatedWithPojo = {"model"})
//    private Game game;
//    
//    public void setId(int id) { // ExecutableElement
//        this.id = id;
//    }
//    
//    public int getId() { // ExecutableElement
//        return this.id;
//    }
//}