package com.controller.auqa.patlas.aqua_controller_app.usb;

import java.nio.ByteBuffer;

/**
 * Created by PatLas on 2016-01-02.
 */

class ByteUtils {
    //private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
        buffer.putLong(0, x);
        return myArray(buffer);
    }

    public static long bytesToLong(byte[] bytes, int offset) {
        ByteBuffer buffer = ByteBuffer.allocate(8);//Long.SIZE / Byte.SIZE
        byte[] tempB = new byte[8];//Long.SIZE / Byte.SIZE

        for(int index=0; index<8;index++){//Long.SIZE / Byte.SIZE
            tempB[index] = bytes[offset+index];
        }

        buffer.put(myArray(tempB), 0, 8);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    private static byte[] myArray(ByteBuffer b){

        int size = b.capacity();
        byte[] ret = new byte[size];

        //System.out.println(size);
        for(int index=0; index<size; index++){
            ret[index]= b.get((size-1)-index) ;
        }
        return ret;
    }

    private static byte[] myArray(byte[] b){

        int size = b.length;
        byte[] ret = new byte[size];

        //System.out.println(size);
        for(int index=0; index<size; index++){
            ret[index]= b[(size-1)-index] ;
        }
        return ret;
    }
}