
package com.controller.auqa.patlas.aqua_controller_app.usb;

import java.nio.ByteBuffer;

/**
 *
 * @author PatLas
 */
public class TLVstruct 
{
    public static int TLV_DATA_SIZE = 55;
    public static int TLV_STRUCT_SIZE = 64;
    public static int TLV_DATA_OFFSET = 9;
    public byte type;
    public long length;
    public byte[] data = new byte[TLV_DATA_SIZE];


    //data array <= 11B
    /*private*/ public byte[] buildTLVdataHeader(boolean command, byte[] data, long length)
    {
        byte[] header = new byte[TLVstruct.TLV_STRUCT_SIZE];

        if(command == true)
            header[0] = 0;
        else
            header[0] = 1;

        byte[] len = ByteUtils.longToBytes(length);

        System.arraycopy(len, 0, header, 1, len.length);
        System.arraycopy(data, 0, header, 9, data.length);

        return header;
    }
   
}
