package com.keithtmiller.protoconverter.example;

import com.keithtmiller.protoconverter.ProtoCollection;
import com.keithtmiller.protoconverter.ProtoMap;
import com.keithtmiller.prototest.ComplexChildMessage;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder 
public class Complex {
    private String name;

    @ProtoCollection(setter = "addAllChildList", clazz = ComplexChildMessage.class)
    private List<ComplexChild> childList;

    @ProtoMap(setter = "putAllChildMap", setterKeyClass = String.class, setterValueClass = ComplexChildMessage.class)
    private Map<String, ComplexChild> childMap;
}
