package com.keithtmiller.protoconverter.example;

import com.keithtmiller.protoconverter.ProtoClass;
import com.keithtmiller.prototest.ComplexChildMessage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ProtoClass(ComplexChildMessage.class)
public class ComplexChild {
    private String name;
}
