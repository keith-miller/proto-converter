package com.keithmiller.protoconverter;

import com.keithmiller.protoconverter.example.TestConvertClass;
import com.keithmiller.protoconverter.example.TestConvertSubClass;
import com.keithmiller.prototest.TestConvertMessage;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.*;

public class ProtoConverterTest {

    @Test
    public void convertToMessage() throws Exception {
        var testSubClass = TestConvertSubClass.builder()
                .id(UUID.randomUUID())
                .build();

        var testEntity = TestConvertClass.builder()
                .id(UUID.randomUUID())
                .name(null)
                .count(5)
                .testConvertSubClass(testSubClass)
                .build();

        TestConvertMessage result = ProtoConverter.convertToMessage(testEntity, TestConvertMessage.newBuilder(), Collections.emptyMap());

        assertEquals(testEntity.getId().toString(), result.getEntityId());
        assertNotNull(result.getName());
        assertEquals((int) testEntity.getCount(), result.getCount());
        assertEquals(testSubClass.getId().toString(), result.getSubEntityId());

        // call the same method with the same class again to test the caching
        result = ProtoConverter.convertToMessage(testEntity, TestConvertMessage.newBuilder(), Collections.emptyMap());

        assertEquals(testEntity.getId().toString(), result.getEntityId());
        assertNotNull(result.getName());
        assertEquals((int) testEntity.getCount(), result.getCount());
        assertEquals(testSubClass.getId().toString(), result.getSubEntityId());
    }

    @Test
    public void customConvertToMessage() throws Exception {
        var customMap = new HashMap<String, Pair<Class<?>, Object>>();

        customMap.put("setEntityId", Pair.of(String.class, UUID.randomUUID().toString()));
        customMap.put("setName", Pair.of(String.class, "testName"));
        customMap.put("setCount", Pair.of(int.class, 5));
        customMap.put("setSubEntityId", Pair.of(String.class, UUID.randomUUID().toString()));

        TestConvertMessage result = ProtoConverter.convertToMessage(new Object(), TestConvertMessage.newBuilder(), customMap);

        assertEquals(customMap.get("setEntityId").getValue(), result.getEntityId());
        assertEquals(customMap.get("setName").getValue(), result.getName());
        assertEquals(customMap.get("setCount").getValue(), result.getCount());
        assertEquals(customMap.get("setSubEntityId").getValue(), result.getSubEntityId());
    }
}