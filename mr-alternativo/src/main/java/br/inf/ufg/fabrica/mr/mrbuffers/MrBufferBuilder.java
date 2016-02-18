/*
 * Copyright (c) 2015 - 2016. Instituto de Informática (UFG)
 */

package br.inf.ufg.fabrica.mr.mrbuffers;


import br.inf.ufg.fabrica.mr.Mr;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class MrBufferBuilder {

    static final Charset utf8charset = Charset.forName("UTF-8");
    ByteBuf bb;

    /**
     * Start with a buffer of size `initial_size`, then grow as required.
     *
     * @param initial_size The initial size of the internal buffer to use.
     */
    public MrBufferBuilder(int initial_size) {
        if (initial_size <= 0) initial_size = 1;
        bb = newByteBuffer(initial_size);
    }

    /**
     * Create a `ByteBuffer` with a given capacity.
     *
     * @param capacity The size of the `ByteBuffer` to allocate.
     * @return Returns the new `ByteBuffer` that was allocated.
     */
    static ByteBuf newByteBuffer(int capacity) {
        ByteBuf newbb = Unpooled.buffer(capacity);
        newbb.order(ByteOrder.LITTLE_ENDIAN);
        return newbb;
    }

    /**
     * Prepare to write an element of `size` after `additional_bytes`
     * have been written.
     *
     * @param size             This is the of the new element to write.
     * @param additional_bytes The padding size.
     */
    public void prep(int size, int additional_bytes) {
        bb.capacity(bb.capacity() + size + additional_bytes);
    }

    /**
     * Offset relative to the end of the buffer.
     *
     * @return Offset relative to the end of the buffer.
     */
    public int offset() {
        return bb.writerIndex();
    }

    /**
     * Add a `boolean` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x A `boolean` to put into the buffer.
     */
    public void addBoolean(boolean x) {
//        prep(Mr.BOOLEAN_SIZE, 0);
        putBoolean(x);
    }

    /**
     * Add a `byte` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x A `byte` to put into the buffer.
     */
    public void addByte(byte x) {
        prep(Mr.BYTE_SIZE, 0);
        putByte(x);
    }

    /**
     * Add a `short` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x A `short` to put into the buffer.
     */
    public void addShort(short x) {
        prep(Mr.SHORT_SIZE, 0);
        putShort(x);
    }

    /**
     * Add an `int` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x An `int` to put into the buffer.
     */
    public void addInt(int x) {
//        prep(Referencia.totalBytes(x), 0);
        prep(Mr.INT_SIZE, 0);
        putInt(x);
    }

    /**
     * Add a `long` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x A `long` to put into the buffer.
     */
    public void addLong(long x) {
//        prep(Referencia.totalBytes(x), 0);
        prep(Mr.LONG_SIZE, 0);
        putLong(x);
    }

    /**
     * Add a `float` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x A `float` to put into the buffer.
     */
    public void addFloat(float x) {
        prep(Mr.FLOAT_SIZE, 0);
        putFloat(x);
    }

    /**
     * Add a `double` to the buffer, properly aligned, and grows the buffer (if necessary).
     *
     * @param x A `double` to put into the buffer.
     */
    public void addDouble(double x) {
        prep(Mr.DOUBLE_SIZE, 0);
        putDouble(x);
    }

    /**
     * @param x
     */
    public void addChar(char x) {
        prep(Mr.CHAR_SIZE, 0);
        putChar(x);
    }

    /**
     * Add a `type` to the buffer.
     *
     * @param x An `int` to put into the buffer.
     */
    public int addType(int x) {
        if (x >= 0x3F) {
            throw new IllegalArgumentException("The value must be an integer of 1 byte");
        }
        prep(Mr.TYPE_SIZE, 0);
        return putType(x);
    }

    public int addRef(int parent, int index, int position) {
        prep(Mr.REF_SIZE, 0);
        int ref = parent - index + position;
        bb.writeByte(ref);
        return ref;
    }

    /**
     * Add a `boolean` to the buffer.
     *
     * @param x A `boolean` to put into the buffer.
     */
    public void putBoolean(boolean x) {
        bb.writeBoolean(x);
    }

    /**
     * Add a `byte` to the buffer.
     *
     * @param x A `byte` to put into the buffer.
     */
    public void putByte(byte x) {
        bb.writeByte(x);
    }

    /**
     * Add a `short` to the buffer.
     *
     * @param x A `short` to put into the buffer.
     */
    public void putShort(short x) {
        bb.writeShort(x);
    }

    /**
     * Add an `int` to the buffer.
     *
     * @param x An `int` to put into the buffer.
     */
    public void putInt(int x) {
//        bb.writeBytes(Referencia.intToByteArray(x));
        bb.writeInt(x);
    }

    /**
     * Add a `long` to the buffer.
     *
     * @param x A `long` to put into the buffer.
     */
    public void putLong(long x) {
//        bb.writeBytes(Referencia.longToByteArray(x));
        bb.writeLong(x);
    }

    /**
     * Add a `float` to the buffer.
     *
     * @param x A `float` to put into the buffer.
     */
    public void putFloat(float x) {
        bb.writeFloat(x);
    }

    /**
     * Add a `double` to the buffer.
     *
     * @param x A `double` to put into the buffer.
     */
    public void putDouble(double x) {
        bb.writeDouble(x);
    }

    /**
     * @param x
     */
    public void putChar(char x) {
        bb.writeChar(x);
    }

    /**
     * Add an `type` to the buffer object.
     *
     * @param x An `int` to put into the buffer.
     */
    public int putType(int x) {
        if (x >= 0x3F) {
            throw new IllegalArgumentException("The value must be an integer of 1 byte");
        }
        int index = offset();
        bb.writeByte((byte) x);
        return index;
    }

    /**
     * Encode the string `s` in the buffer using UTF-8.
     *
     * @param s The string to encode.
     * @return The offset in the buffer where the encoded string starts.
     */
    public int createString(String s) {
        byte[] utf8 = s.getBytes(utf8charset);
        return addByteArray(utf8);
    }

    public int addByteArray(byte[] arr) {
        int id = offset();
        addInt(arr.length);
        prep(arr.length, 0);
        bb.writeBytes(arr);
        return id;
    }

    public void addString(String s) {

    }

    /**
     * Get the ByteBuffer representing the MrBufferBuilder.
     */
    public ByteBuf dataBuffer() {
        return bb;
    }
}
