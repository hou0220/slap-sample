package com.hauchee.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) // Target to field element only
@Retention(RetentionPolicy.SOURCE) // Source level retention policy
public @interface Pojo {
    
    /**
     * The types of the fields in this POJO class. 
     * Each type has a corresponding name specified in 'fieldNames' attribute.
     */
    Class[] fieldTypes() default {};
    
    /**
     * The names of the fields in this POJO class. 
     * Each name has a corresponding type specified in 'fieldTypes' attribute.
     */
    String[] fieldNames() default {};
    
    /**
     * The string element in this array is representing a new field which its type
     * is same as its name and it is annotated with @Pojo annotation.
     */
    String[] fieldsAnnotatedWithPojo() default {};
}