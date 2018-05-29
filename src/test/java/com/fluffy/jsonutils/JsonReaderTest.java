package com.fluffy.jsonutils;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class JsonReaderTest {
    JsonReader reader;

    static byte[] bytes(String data) {
        return data.getBytes();
    }

    @BeforeEach
    void setupReader() throws Exception {
        reader = new JsonReader();
    }

    @Test
    void testFeedData() throws Exception {
        reader.feedData(bytes("hello "));
        InputStream inputStream = reader.stream.getInputStream();

        inputStream.mark(-1);
        assertArrayEquals(reader.stream.getInputStream().readAllBytes(), bytes("hello "));
        inputStream.reset();

        reader.feedData(bytes("world"));
        assertArrayEquals(reader.stream.getInputStream().readAllBytes(), bytes("hello world"));
    }

    @Test
    void testParse() {
        reader.feedData(bytes("1234 "));
        List<Object> ret;

        ret = reader.parse();
        assertEquals(ret.get(0), 1234);

        reader.feedData(bytes("\"json string\" "));
        ret = reader.parse();
        assertEquals(ret.get(0), "json string");

        reader.feedData(bytes("\"json "));
        ret = reader.parse();
        assertTrue(ret.size() == 0);

        reader.feedData(bytes("string\""));
        ret = reader.parse();
        assertEquals(ret.get(0), "json string");

        reader.feedData(bytes("tr"));
        ret = reader.parse();
        assertTrue(ret.size() == 0);

        reader.feedData(bytes("ue"));
        ret = reader.parse();
        assertEquals(ret.get(0), true);

        reader.feedData(bytes("{}"));
        ret = reader.parse();
        HashMap v = (HashMap) ret.get(0);
        assertTrue(v.isEmpty());

        reader.feedData(bytes("{"));
        ret = reader.parse();
        assertTrue(ret.size() == 0);

        reader.feedData(bytes("}"));
        ret = reader.parse();
        v = (HashMap) ret.get(0);
        assertTrue(v.isEmpty());

        reader.feedData(bytes("{\"f1\": \"hello\"}"));
        ret = reader.parse();
        v = (HashMap) ret.get(0);
        assertEquals(v.get("f1"), "hello");
    }
}