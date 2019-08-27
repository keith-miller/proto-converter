package com.keithmiller.protoconverter.example;

import com.keithmiller.protoconverter.ProtoField;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class TestConvertSubClass {
    @ProtoField(setter = "setSubEntityId", castingMethod = "toString")
    private UUID id;
}
