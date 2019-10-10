package com.keithtmiller.protoconverter.example;

import com.keithtmiller.protoconverter.ProtoClass;
import com.keithtmiller.protoconverter.ProtoField;
import com.keithtmiller.prototest.TestConvertSubMessage;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class TestConvertClass {
    @ProtoField(setter = "setEntityId", castingMethod = "toString")
    private UUID id;

    private String name;

    private Integer count;

    @ProtoClass(setterName = "setTestConvertSubMessage", setterClass = TestConvertSubMessage.class)
    private TestConvertSubClass testConvertSubClass;
}
