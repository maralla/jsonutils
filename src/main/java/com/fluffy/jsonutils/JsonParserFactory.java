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

    public class Parser extends UTF8StreamJsonParser {
        private int inputPtrMarker;
        private int inputEndMarker;
        private byte[] inputBufferMarker;

        Parser(IOContext ctx, int features, InputStream in, ObjectCodec codec, ByteQuadsCanonicalizer sym,
               byte[] inputBuffer) {
            super(ctx, features, in, codec, sym, inputBuffer, 0, 0, false);
        }

        @Override
        protected void _closeInput() {
            // Do nothing.
        }

        public void mark() {
            inputPtrMarker = _inputPtr;
            inputEndMarker = _inputEnd;
            inputBufferMarker = new byte[_inputEnd - _inputPtr];
            System.arraycopy(_inputBuffer, _inputPtr, inputBufferMarker, 0, _inputEnd - _inputPtr);
        }

        public void reset() {
            _inputPtr = inputPtrMarker;
            _inputEnd = inputEndMarker;
            System.arraycopy(inputBufferMarker, 0, _inputBuffer, _inputPtr, _inputEnd - _inputPtr);
        }
    }

    class Factory extends JsonFactory {
        @Override
        protected JsonParser _createParser(InputStream in, IOContext ctxt) {
            return new Parser(ctxt, _parserFeatures, in, _objectCodec,
                    _byteSymbolCanonicalizer.makeChild(_factoryFeatures), ctxt.allocReadIOBuffer());
        }
    }
}
