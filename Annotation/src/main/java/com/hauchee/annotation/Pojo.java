package com.hauchee.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Pojo {
    
    Class[] fieldTypes() default Void.class;
    String[] fieldNames() default "N/A";
    String[] pojoAnnotatedFields() default "N/A";
}
