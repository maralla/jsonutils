package com.fluffy.jsonutils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.JsonEOFException;

import java.util.*;

public class JsonReader {
    ByteBuffer stream;
    JsonParser parser;
    private Stack<Status> ctx = new Stack<>();

    public JsonReader() throws Exception {
        this(new byte[0]);
    }

    public JsonReader(byte[] data) throws Exception {
        stream = new ByteBuffer(data.length);
        stream.write(data);
        parser = new JsonParserFactory().createParser(stream.getInputStream());
    }

    private boolean inObject() {
        try {
            return ctx.peek() != null && ctx.peek().type == ValueType.OBJECT;
        } catch (EmptyStackException e) {
            return false;
        }
    }

    private boolean inArray() {
        try {
            return ctx.peek() != null && ctx.peek().type == ValueType.ARRAY;
        } catch (EmptyStackException e) {
            return false;
        }
    }

    private void insertObj(Object value) {
        Status s = ctx.peek();
        HashMap<String, Object> obj = (HashMap) s.obj;
        obj.put(s.currentKey, value);
    }

    private void appendObj(Object value) {
        List<Object> obj = (List) ctx.peek().obj;
        obj.add(value);
    }

    private void processValue(Object value, List<Object> values) {
        if (inObject()) {
            insertObj(value);
        } else if (inArray()) {
            appendObj(value);
        } else {
            System.out.println("===================");
            long offset = parser.getCurrentLocation().getByteOffset();
            System.out.println(offset + "|" + value + "|" + stream.getPos());
            values.add(value);
            stream.compact();
        }
    }

    public void feedData(byte[] data) {
        stream.write(data);
    }

    public List<Object> parse() throws Exception {
        List<Object> values = new ArrayList<>();
        JsonToken tok;
        try {
            while ((tok = parser.nextToken()) != null) {
                doParse(tok, values);
            }
            stream.reset();
        } catch (JsonEOFException e) {
            // Ignore EOF exception.
        }
        return values;
    }

    private void doParse(JsonToken tok, List<Object> values) throws Exception {
        switch (tok) {
            case START_OBJECT:
                ctx.push(new Status(new HashMap<String, Object>(), ValueType.OBJECT));
                break;
            case END_OBJECT: {
                processValue(ctx.pop().obj, values);
                break;
            }
            case START_ARRAY:
                ctx.push(new Status(new ArrayList<>(), ValueType.ARRAY));
                break;
            case END_ARRAY: {
                processValue(ctx.pop().obj, values);
                break;
            }
            case FIELD_NAME:
                ctx.peek().currentKey = parser.getText();
                break;
            case VALUE_STRING:
                processValue(parser.getText(), values);
                break;
            case VALUE_NUMBER_FLOAT:
                processValue(parser.getFloatValue(), values);
                break;
            case VALUE_NUMBER_INT:
                processValue(parser.getIntValue(), values);
                break;
            case VALUE_FALSE:
            case VALUE_TRUE:
                processValue(parser.getBooleanValue(), values);
                break;
            case VALUE_NULL:
                processValue(null, values);
                break;
            default:
                throw new Exception("Invalid token");
        }
    }

    enum ValueType {
        OBJECT,
        ARRAY
    }

    private class Status {
        Object obj;
        ValueType type;
        String currentKey;

        Status(Object obj, ValueType type) {
            this.obj = obj;
            this.type = type;
        }
    }
}
