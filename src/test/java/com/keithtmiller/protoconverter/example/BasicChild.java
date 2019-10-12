package com.keithtmiller.protoconverter.example;

import com.keithtmiller.protoconverter.ProtoField;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class BasicChild {
    @ProtoField(setter = "setEntityId", castingMethod = "toString")
    private UUID id;

    private String name;
}
