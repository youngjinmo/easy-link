package com.shortenurl.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectUtilTest {

    @Test
    void stringify() {
        // given
        MockObj mockObj = new MockObj();

        // when
        String stringify = ObjectUtil.stringify(mockObj);

        // then
        String expected = "{\"value\":\"mock-value\"}";
        assertEquals(expected, stringify);
    }

    @Test
    void parseJson() {
        // given
        String mockJson = "{\"value\":\"mock-value\"}";

        // when
        MockObj parseJson = ObjectUtil.parseJson(mockJson, MockObj.class);

        // then
        assertInstanceOf(MockObj.class, parseJson);
        assertEquals("mock-value", parseJson.getValue());
    }
}

class MockObj {
    private String value = "mock-value";
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}

