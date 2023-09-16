package dev.ithundxr.plutonium.utils;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;

public class EndlessByteBufInputStream extends InputStream {

    private final ByteBuf buf;

    public EndlessByteBufInputStream(ByteBuf buf) {
        this.buf = buf;
        buf.markReaderIndex();
    }

    @Override
    public int available() {
        return buf.readableBytes();
    }

    @Override
    public int read() {
        if (!buf.isReadable()) return -1;
        return buf.readByte()&0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) {
        if (!buf.isReadable()) return -1;
        len = Math.min(len, buf.readableBytes());
        buf.readBytes(b, off, len);
        return len;
    }

    @Override
    public byte[] readAllBytes() {
        byte[] bys = new byte[buf.readableBytes()];
        buf.readBytes(bys);
        return bys;
    }

    @Override
    public synchronized void mark(int readlimit) {
        buf.markReaderIndex();
    }

    @Override
    public synchronized void reset() {
        buf.resetReaderIndex();
    }

}
