package com.keithtmiller.protoconverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ProtoClass {
    /**
     * The mapped field proto class
     * @return the mapped field proto class
     */
    Class value();
}