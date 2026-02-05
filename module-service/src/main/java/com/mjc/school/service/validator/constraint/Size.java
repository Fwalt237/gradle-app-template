package com.mjc.school.service.validator.constraint;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint
public @interface Size {

    int min() default -1;

    int max() default -1;
}
