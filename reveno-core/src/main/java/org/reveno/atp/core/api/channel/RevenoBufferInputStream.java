package org.reveno.atp.core.api.channel;

import java.io.IOException;
import java.io.InputStream;

public class RevenoBufferInputStream extends InputStream {

    protected final Buffer buffer;

    @Override
    public int read() throws IOException {
        return buffer.readByte() & 0xff;
    }

    public RevenoBufferInputStream(Buffer buffer) {
        this.buffer = buffer;
    }

}
