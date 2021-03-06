package com.keithtmiller.protoconverter;

import com.google.protobuf.GeneratedMessageV3;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

public class ProtoConverter {
    private static final Map<Class<?>, Map<String, Optional<Method>>> converterCache = new LinkedHashMap<>();

    /**
     * Java object to GeneratedMessageV3 Protobuf object converter
     *
     * @param object - Java object we are converting
     * @param builder - the GeneratedMessageV3.Builder class
     * @param <BUILDER> - The generated Protobuf Builder class
     * @param <PROTO> - The generated Protobuf class
     * @return the resulting GeneratedMessageV3 Protobuf object
     * @throws ProtoConverterException - thrown is the object passed in is null
     */
    public static <BUILDER extends GeneratedMessageV3.Builder,
            PROTO extends GeneratedMessageV3> PROTO convertToMessage(Object object,
                                                                     BUILDER builder)
            throws ProtoConverterException {
        return ProtoConverter.convertToMessage(object, builder, Collections.emptyMap());
    }

    /**
     * Java object to GeneratedMessageV3 Protobuf object converter
     *
     * @param object - Java object we are converting
     * @param builder - the GeneratedMessageV3.Builder class
     * @param customMapping - Map of method names and a Pair of class and object to set
     * @param <BUILDER> - The generated Protobuf Builder class
     * @param <PROTO> - The generated Protobuf class
     * @return the resulting GeneratedMessageV3 Protobuf object
     * @throws ProtoConverterException - thrown is the object passed in is null
     */
    @SuppressWarnings("unchecked")
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
     * @throws ProtoConverterException - occurs if the field cannot be converted
     */
    private static <BUILDER extends GeneratedMessageV3.Builder> void processFields(Object object,
                                                                                   Class clazz,
                                                                                   BUILDER builder)
            throws ProtoConverterException {
        for (var field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ProtoField.class)) {
                // get the field annotation
                var fieldAnnotation = field.getAnnotation(ProtoField.class);

                try {
                    // set it to accessible
                    field.setAccessible(true);

                    // get the field value
                    var fieldValue = field.get(object);
                    if(fieldValue == null) {
                        continue;
                    }

                    if(field.getType().isAnnotationPresent(ProtoClass.class)) {
                        convertProtoClass(field, fieldValue, builder, fieldAnnotation.setter());
                    } else {
                        // get the setter method for the proto class
                        var setter = getSetter(builder, fieldAnnotation.setter());

                        // get the class of the setter param
                        var setterClass = setter.getParameterTypes()[0];

                        if (fieldAnnotation.castingMethod().length() > 0) {
                            // force casting by calling casting method
                            var getter = fieldValue.getClass().getMethod(fieldAnnotation.castingMethod());
                            var castedFieldValue = getter.invoke(fieldValue);

                            // call the setter with the field value from the object
                            setter.invoke(builder, checkValue(setterClass, castedFieldValue));
                        } else {
                            // call the setter with the field value from the object
                            setter.invoke(builder, checkValue(setterClass, fieldValue));
                        }
                    }
                } catch (Exception e) {
                    throw new ProtoConverterException(String.format("Error converting field: %s", field.getName()), e);
                }
            } else {
                // attempt the default setter lookup, ignore if not found
                var setterName = String.format("set%s", field.getName().substring(0,1).toUpperCase() + field.getName().substring(1));

                try {
                    // set it to accessible
                    field.setAccessible(true);

                    // get the field value
                    var fieldValue = field.get(object);

                    // get the setter method for the proto class
                    var setter = ProtoConverter.getSetter(builder, setterName);

                    // get the class of the setter param
                    var setterClass = setter.getParameterTypes()[0];

                    setter.invoke(builder, checkValue(setterClass, fieldValue));
                } catch (Exception ignored) { }
            }
        }
    }

    /**
     * We need to be able to handle custom mapping even with the above annotation processing for advanced mapping
     *
     * @param builder - the GeneratedMessageV3.Builder class
     * @param customMapping - Map of method names and a Pair of class and object to set
     * @throws ProtoConverterException - occurs when the custom mapping cannot be processed
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

    private static <BUILDER extends GeneratedMessageV3.Builder> Method getSetter(BUILDER builder,
                                                                                 String setterName)
            throws ProtoConverterException {
        Predicate<Method> filter = f ->  f.getName().equals(setterName);
        return getSetter(builder, setterName, filter);
    }

    private static <BUILDER extends GeneratedMessageV3.Builder> Method getSetter(BUILDER builder,
                                                                                 Class clazz,
                                                                                 String setterName)
            throws ProtoConverterException {
        Predicate<Method> filter = f ->  f.getName().equals(setterName) && f.getParameterTypes()[0].getName().equals(clazz.getName());
        return getSetter(builder, setterName, filter);
    }

    private static synchronized <BUILDER extends GeneratedMessageV3.Builder> Method getSetter(BUILDER builder,
                                                                                              String setterName,
                                                                                              Predicate<Method> filter)
            throws ProtoConverterException {
        var builderMap = ProtoConverter.converterCache.computeIfAbsent(builder.getClass(), clazz -> new LinkedHashMap<>());
        var setterOption = builderMap.computeIfAbsent(setterName, name ->
                Arrays.stream(builder.getClass().getMethods())
                        .filter(filter)
                        .findFirst());

        if(setterOption.isEmpty()) {
            throw new ProtoConverterException(
                    String.format("Method %s doesn't exist in builder %s", setterName, builder.getClass()));
        }

        return setterOption.get();
    }

    /**
     * Method to handle converting of a child proto class
     *
     * @param field - the field being converted
     * @param fieldValue - the value of the field being converted
     * @param builder - the GeneratedMessageV3.Builder class
     * @param <BUILDER> - The generated Protobuf Builder class
     * @throws NoSuchMethodException - the builder method doesn't exist
     * @throws InvocationTargetException - the builder method can't be invoked
     * @throws IllegalAccessException - the builder method isn't accessible
     * @throws ProtoConverterException - could not process the fields of the child class
     */
    @SuppressWarnings("unchecked")
    private static <BUILDER extends GeneratedMessageV3.Builder> void convertProtoClass(Field field,
                                                                                       Object fieldValue,
                                                                                       BUILDER builder,
                                                                                       String setterName)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ProtoConverterException {
        // get the sub class builder
        var destAnnotation = field.getType().getAnnotation(ProtoClass.class);

        // get the correct setter using the class of the field
        var setter = getSetter(builder, destAnnotation.value(), setterName);

        var builderMethod = destAnnotation.value().getMethod("newBuilder");
        var fieldBuilder = (BUILDER) builderMethod.invoke(null);

        // process the sub class fields
        processFields(fieldValue, field.getType(), fieldBuilder);

        setter.invoke(builder, destAnnotation.value().cast(fieldBuilder.build()));
    }
}
