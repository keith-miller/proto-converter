package com.keithtmiller.protoconverter;

import com.keithtmiller.protoconverter.example.Basic;
import com.keithtmiller.protoconverter.example.BasicChild;
import com.keithtmiller.prototest.BasicMessage;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.*;

public class ProtoConverterTest {

    @Test
    public void convertToMessage() throws Exception {
        var testSubEntity = BasicChild.builder()
                .id(UUID.randomUUID())
                .name("subEntity")
                .build();

        var testEntity = Basic.builder()
                .id(UUID.randomUUID())
                .name("entity")
                .count(5)
                .basicChild(testSubEntity)
                .build();

        BasicMessage result = ProtoConverter.convertToMessage(testEntity, BasicMessage.newBuilder());

        assertEquals(testEntity.getId().toString(), result.getEntityId());
        assertNotNull(result.getName());
        assertEquals((int) testEntity.getCount(), result.getCount());
        assertEquals(testSubEntity.getId().toString(), result.getChild().getEntityId());
        assertEquals(testSubEntity.getName(), result.getChild().getName());

        // call the same method with the same class again to test the caching
        result = ProtoConverter.convertToMessage(testEntity, BasicMessage.newBuilder());

        assertEquals(testEntity.getId().toString(), result.getEntityId());
        assertNotNull(result.getName());
        assertEquals((int) testEntity.getCount(), result.getCount());
        assertEquals(testSubEntity.getId().toString(),  result.getChild().getEntityId());
        assertEquals(testSubEntity.getName(), result.getChild().getName());
    }

    @Test
    public void customConvertToMessage() throws Exception {
        var customMap = new HashMap<String, Pair<Class<?>, Object>>();

        var testSubEntity = BasicChild.builder()
                .id(UUID.randomUUID())
                .name("subEntity")
                .build();

        var testEntity = Basic.builder()
                .id(UUID.randomUUID())
                .name("entity")
                .count(1)
                .basicChild(testSubEntity)
                .build();

        customMap.put("setEntityId", Pair.of(String.class, UUID.randomUUID().toString()));
        customMap.put("setName", Pair.of(String.class, "testName"));
        customMap.put("setCount", Pair.of(int.class, 5));

        BasicMessage result = ProtoConverter.convertToMessage(testEntity, BasicMessage.newBuilder(), customMap);

        assertEquals(customMap.get("setEntityId").getValue(), result.getEntityId());
        assertEquals(customMap.get("setName").getValue(), result.getName());
        assertEquals(customMap.get("setCount").getValue(), result.getCount());
        assertEquals(testSubEntity.getId().toString(), result.getChild().getEntityId());
        assertEquals(testSubEntity.getName(), result.getChild().getName());
    }
}