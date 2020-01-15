package com.keithtmiller.protoconverter;

import com.keithtmiller.protoconverter.example.Complex;
import com.keithtmiller.protoconverter.example.ComplexChild;
import com.keithtmiller.prototest.ComplexMessage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ProtoConverterComplexTest {
    @Test
    public void convertToMessage() throws Exception {
        // generate test objects
        List<ComplexChild> childList = new ArrayList<>();
        childList.add(ComplexChild.builder().name("first").build());
        childList.add(ComplexChild.builder().name("second").build());
        childList.add(ComplexChild.builder().name("third").build());

        Map<String, ComplexChild> childMap = new HashMap<>();
        childMap.put("first", childList.get(0));
        childMap.put("second", childList.get(1));
        childMap.put("third", childList.get(2));

        var complex = Complex.builder()
                .name("parent")
                .childList(childList)
                .childMap(childMap)
                .build();

        ComplexMessage result = ProtoConverter.convertToMessage(complex, ComplexMessage.newBuilder());

        // test collection
        assertEquals(complex.getName(), result.getName());
        assertEquals(complex.getChildList().size(), result.getChildListCount());
        for (var i = 0; i < complex.getChildList().size(); i++) {
            assertEquals(complex.getChildList().get(i).getName(), result.getChildList(i).getName());
        }

        // test map
        assertEquals(complex.getChildMap().size(), result.getChildMapMap().keySet().size());
        assertEquals(complex.getChildMap().get("first").getName(), result.getChildMapMap().get("first").getName());
        assertEquals(complex.getChildMap().get("second").getName(), result.getChildMapMap().get("second").getName());
        assertEquals(complex.getChildMap().get("third").getName(), result.getChildMapMap().get("third").getName());
    }
}
