package com.fluffy.jsonutils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.UTF8StreamJsonParser;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;

import java.io.IOException;
import java.io.InputStream;

public class JsonParserFactory {

    public JsonParser createParser(InputStream in) throws IOException {
        return (new Factory()).createParser(in);
    }

    class Parser extends UTF8StreamJsonParser {
        Parser(IOContext ctx, int features, InputStream in, ObjectCodec codec, ByteQuadsCanonicalizer sym,
               byte[] inputBuffer, int start, int end, boolean bufferRecyclable) {
            super(ctx, features, in, codec, sym, inputBuffer, start, end, bufferRecyclable);
        }

        @Override
        protected void _closeInput() {
            // Do nothing.
        }
    }

    class Factory extends JsonFactory {
        @Override
        protected JsonParser _createParser(InputStream in, IOContext ctxt) {
            return new Parser(ctxt, _parserFeatures, in, _objectCodec,
                    _byteSymbolCanonicalizer.makeChild(_factoryFeatures), ctxt.allocReadIOBuffer(),
                    0, 0, true);
        }
    }
}
