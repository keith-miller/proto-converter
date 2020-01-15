package com.keithtmiller.protoconverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ProtoMap {
    /**
     * The mapped field setter name
     * @return the mapped field setter name
     */
    String setter();

    /**
     * The mapped map key class
     * @return the mapped map key class
     */
    Class setterKeyClass();

    /**
     * The mapped map value class
     * @return the mapped map value class
     */
    Class setterValueClass();
}
