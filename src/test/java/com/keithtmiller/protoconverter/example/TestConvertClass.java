package com.keithtmiller.protoconverter.example;

import com.keithtmiller.protoconverter.ProtoClass;
import com.keithtmiller.protoconverter.ProtoField;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class TestConvertClass {
    @ProtoField(setter = "setEntityId", castingMethod = "toString")
    private UUID id;

    @ProtoField(setter = "setName")
    private String name;

    @ProtoField(setter = "setCount")
    private Integer count;

    @ProtoClass
    private TestConvertSubClass testConvertSubClass;
}
