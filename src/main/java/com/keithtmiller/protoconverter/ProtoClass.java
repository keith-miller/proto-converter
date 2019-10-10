package com.keithtmiller.protoconverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ProtoClass {
    /**
     * The mapped field setter name
     * @return the mapped field setter name
     */
    String setterName();

    /**
     * The mapped field proto class
     * @return the mapped field proto class
     */
    Class setterClass();
}