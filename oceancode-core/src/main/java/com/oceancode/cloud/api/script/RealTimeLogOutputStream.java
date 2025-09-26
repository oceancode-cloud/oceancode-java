package com.oceancode.cloud.api.script;


import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

public class RealTimeLogOutputStream extends OutputStream {
    private int index = 0;
    private byte[] bytes;
    private Consumer<String> lineCallback;

    public RealTimeLogOutputStream(Consumer<String> lineCallback) {
        bytes = new byte[1024];
        this.lineCallback = lineCallback;
    }


    @Override
    public void write(int b) throws IOException {
        if (index >= bytes.length) {
            int len = bytes.length + 200;
            byte[] temp = new byte[len];
            System.arraycopy(bytes, 0, temp, 0, index);
            bytes = temp;
        }
        bytes[index++] = (byte) b;
        if (b == 10) {
            byte[] array = new byte[index];
            System.arraycopy(bytes, 0, array, 0, index);
            index = 0;
            lineCallback.accept(new String(array));
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (index != 0) {
            byte[] array = new byte[index];
            System.arraycopy(bytes, 0, array, 0, index);
            lineCallback.accept(new String(array));
        }
        index = 0;
        bytes = null;
    }
}
