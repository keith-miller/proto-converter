package com.keithtmiller.protoconverter;

import com.keithtmiller.protoconverter.example.TestConvertClass;
import com.keithtmiller.protoconverter.example.TestConvertSubClass;
import com.keithtmiller.prototest.TestConvertMessage;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.*;

public class ProtoConverterTest {

    @Test
    public void convertToMessage() throws Exception {
        var testSubEntity = TestConvertSubClass.builder()
                .id(UUID.randomUUID())
                .name("subEntity")
                .build();

        var testEntity = TestConvertClass.builder()
                .id(UUID.randomUUID())
                .name("entity")
                .count(5)
                .testConvertSubClass(testSubEntity)
                .build();

        TestConvertMessage result = ProtoConverter.convertToMessage(testEntity, TestConvertMessage.newBuilder(), Collections.emptyMap());

        assertEquals(testEntity.getId().toString(), result.getEntityId());
        assertNotNull(result.getName());
        assertEquals((int) testEntity.getCount(), result.getCount());
        assertEquals(testSubEntity.getId().toString(), result.getTestConvertSubMessage().getEntityId());
        assertEquals(testSubEntity.getName(), result.getTestConvertSubMessage().getName());

        // call the same method with the same class again to test the caching
        result = ProtoConverter.convertToMessage(testEntity, TestConvertMessage.newBuilder(), Collections.emptyMap());

        assertEquals(testEntity.getId().toString(), result.getEntityId());
        assertNotNull(result.getName());
        assertEquals((int) testEntity.getCount(), result.getCount());
        assertEquals(testSubEntity.getId().toString(),  result.getTestConvertSubMessage().getEntityId());
        assertEquals(testSubEntity.getName(), result.getTestConvertSubMessage().getName());
    }

    @Test
    public void customConvertToMessage() throws Exception {
        var customMap = new HashMap<String, Pair<Class<?>, Object>>();

        var testSubEntity = TestConvertSubClass.builder()
                .id(UUID.randomUUID())
                .name("subEntity")
                .build();

        var testEntity = TestConvertClass.builder()
                .id(UUID.randomUUID())
                .name("entity")
                .count(1)
                .testConvertSubClass(testSubEntity)
                .build();

        customMap.put("setEntityId", Pair.of(String.class, UUID.randomUUID().toString()));
        customMap.put("setName", Pair.of(String.class, "testName"));
        customMap.put("setCount", Pair.of(int.class, 5));

        TestConvertMessage result = ProtoConverter.convertToMessage(testEntity, TestConvertMessage.newBuilder(), customMap);

        assertEquals(customMap.get("setEntityId").getValue(), result.getEntityId());
        assertEquals(customMap.get("setName").getValue(), result.getName());
        assertEquals(customMap.get("setCount").getValue(), result.getCount());
        assertEquals(testSubEntity.getId().toString(), result.getTestConvertSubMessage().getEntityId());
        assertEquals(testSubEntity.getName(), result.getTestConvertSubMessage().getName());
    }
}