package com.keithtmiller.protoconverter;

import com.google.protobuf.GeneratedMessageV3;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ProtoConverter {
    private static final Map<Class<?>, Map<String, Optional<Method>>> converterCache = new LinkedHashMap<>();

    /**
     * Java object to GeneratedMessageV3 Protobuf object converter
     *
     * @param object - Java object we are converting
     * @param builder - the GeneratedMessageV3.Builder class
     * @param customMapping - Map of method names and a Pair of class and object to set
     * @return the resulting GeneratedMessageV3 Protobuf object
     * @throws ProtoConverterException
     */
    public static <BUILDER extends GeneratedMessageV3.Builder,
            PROTO extends GeneratedMessageV3> PROTO convertToMessage(Object object,
                                                                     BUILDER builder,
                                                                     Map<String, Pair<Class<?>, Object>> customMapping)
            throws ProtoConverterException {
        if(object == null) {
            throw new ProtoConverterException("Object to convert cannot be null!");
        }

        var clazz = object.getClass();

        processFields(object, clazz, builder);
        processCustomMapping(builder, customMapping);

        return (PROTO) builder.build();
    }

    /**
     *  Recursion method to handle sub objects that have fields we want to convert
     *
     * @param object - Java object we are converting
     * @param clazz - Class of the Java object we are converting
     * @param builder - the GeneratedMessageV3.Builder class
     * @throws ProtoConverterException
     */
    private static <BUILDER extends GeneratedMessageV3.Builder> void processFields(Object object,
                                                                                   Class clazz,
                                                                                   BUILDER builder) throws ProtoConverterException {
        for (var field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ProtoField.class)) {
                // get the field annotation
                var fieldAnnotation = field.getAnnotation(ProtoField.class);

                try {
                    // set it to accessible
                    field.setAccessible(true);

                    // get the field value
                    var fieldValue = field.get(object);

                    // get the setter method for the proto class
                    var setter = ProtoConverter.getSetter(builder, fieldAnnotation.setter());

                    // get the class of the setter param
                    var setterClass = setter.getParameterTypes()[0];

                    if(fieldAnnotation.castingMethod().length() > 0) {
                        // force casting by calling casting method
                        var getter = fieldValue.getClass().getMethod(fieldAnnotation.castingMethod());
                        var castedFieldValue = getter.invoke(fieldValue);

                        // call the setter with the field value from the object
                        setter.invoke(builder, checkValue(setterClass, castedFieldValue));
                    } else {
                        // call the setter with the field value from the object
                        setter.invoke(builder, checkValue(setterClass, fieldValue));
                    }
                } catch (Exception e) {
                    throw new ProtoConverterException(String.format("Error converting field: %s", field.getName()), e);
                }
            } else if (field.isAnnotationPresent(ProtoClass.class)) {
                try {
                    // set it to accessible
                    field.setAccessible(true);

                    // get the field value
                    var fieldValue = field.get(object);

                    // Entities can have child entities that are null
                    if(fieldValue != null) {
                        processFields(fieldValue, fieldValue.getClass(), builder);
                    }
                } catch (IllegalAccessException | NullPointerException e) {
                    throw new ProtoConverterException(String.format("Error accessing ProtoEntity: %s", field.getName()), e);
                }
            }
        }
    }

    /**
     * We need to be able to handle custom mapping even with the above annotation processing (e.g. card sub type, color)
     *
     * @param builder - the GeneratedMessageV3.Builder class
     * @param customMapping - Map of method names and a Pair of class and object to set
     * @throws ProtoConverterException
     */
    private static <BUILDER extends GeneratedMessageV3.Builder> void processCustomMapping(BUILDER builder,
                                                                                          Map<String, Pair<Class<?>, Object>> customMapping)
            throws ProtoConverterException {
        for (var methodName :customMapping.keySet()) {
            var pair = customMapping.get(methodName);

            try {
                var setter = builder.getClass().getMethod(methodName, pair.getKey());
                setter.invoke(builder, pair.getValue());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new ProtoConverterException(String.format("Error handling custom mapping: %s", methodName), e);
            }
        }
    }

    /**
     * Method to check values being inserted into the Protobuf object. Needed to handle nulls.
     *
     * @param clazz - the class of the setter param
     * @param value - the value from the field being converted
     * @return Protobuf safe object
     */
    private static Object checkValue(Class<?> clazz, Object value) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        if(value == null) {
            return clazz.getConstructor().newInstance();
        } else {
            return value;
        }
    }

    private static synchronized <BUILDER extends GeneratedMessageV3.Builder> Method getSetter(BUILDER builder,
                                                                                              String setterName) throws ProtoConverterException {
        var builderMap = ProtoConverter.converterCache.computeIfAbsent(builder.getClass(), clazz -> new LinkedHashMap<>());
        var setterOption = builderMap.computeIfAbsent(setterName, name ->
                Arrays.stream(builder.getClass().getMethods())
                        .filter(f-> f.getName().equals(setterName))
                        .findFirst());

        if(setterOption.isEmpty()) {
            throw new ProtoConverterException(
                    String.format("Method %s doesn't exist in builder %s", setterName, builder.getClass()));
        }

        return setterOption.get();
    }
}
