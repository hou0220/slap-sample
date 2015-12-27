package com.hauchee.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Tag {
    
    enum Kind {
        PRICE, STANDARD
    }
    
    String stringValue() default "";
    int intValue() default 0;
    Kind enumValue() default Kind.PRICE;
    Priority annotationTypeValue() default @Priority(0);
    Class classValue() default Void.class;
    Class[] classesValue() default {};
}
