package com.keithtmiller.protoconverter.example;

import com.keithtmiller.protoconverter.ProtoClass;
import com.keithtmiller.protoconverter.ProtoField;
import com.keithtmiller.prototest.BasicChildMessage;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@ProtoClass(BasicChildMessage.class)
public class BasicChild {
    @ProtoField(setter = "setEntityId", castingMethod = "toString")
    private UUID id;

    private String name;
}
