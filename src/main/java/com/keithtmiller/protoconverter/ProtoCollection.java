package com.keithtmiller.protoconverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ProtoCollection {
    /**
     * The mapped field setter name
     * @return the mapped field setter name
     */
    String setter();

    /**
     * The mapped collection class
     * @return the mapped collection class
     */
    Class clazz();
}
