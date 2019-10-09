package com.keithtmiller.protoconverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ProtoField {
    /**
     * The mapped field setter name
     * @return the mapped field setter name
     */
    String setter();

    /**
     * The field object casting method. Empty string if not needed
     * @return the field object casting method. Empty string if not needed
     */
    String castingMethod() default "";
}
