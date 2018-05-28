package com.fluffy.jsonutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ByteBuffer extends ByteArrayOutputStream {

    private ByteInputStream inputStream;

    public ByteBuffer(int size) {
        super(size);
        inputStream = new ByteInputStream(buf);
    }

    public synchronized void write(byte[] b) {
        super.write(b, 0, b.length);
        inputStream.replaceBuf(buf, count);
        System.out.println("Debug+" + inputStream.pos() + "|" + inputStream.count());
    }

    public synchronized int getPos() {
        return inputStream.pos();
    }

    public synchronized ByteArrayInputStream getInputStream() {
        return inputStream;
    }

    public synchronized void compact() {
        reset();
        int offset = getPos();
        write(buf, offset, buf.length - offset);
        inputStream.replaceBuf(buf, count, 0);
    }

    class ByteInputStream extends ByteArrayInputStream {
        ByteInputStream(byte[] buf) {
            super(buf);
        }

        void replaceBuf(byte[] buf, int size) {
            this.buf = buf;
            count = size;
        }

        void replaceBuf(byte[] buf, int size, int pos) {
            replaceBuf(buf, size);
            this.pos = pos;
        }

        int pos() {
            return pos;
        }

        int count() {
            return count;
        }
    }
}
