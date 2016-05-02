package com.controller.auqa.patlas.aqua_controller_app.usb;

/**
 * Created by PatLas on 2016-01-02.
 */

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * command syntax send in TLV:
 * CommandName,num_of_args,arg1,arg2,...,argN\n
 **/
public class CommandParser
{

    public static ArrayList<String> TLVtoCmd(byte[] tlv)
    {
        ArrayList<String> command = new ArrayList<>();

        long size = ByteUtils.bytesToLong(tlv,1);
        byte[] tmp = Arrays.copyOfRange(tlv, TLVstruct.TLV_DATA_OFFSET, TLVstruct.TLV_DATA_OFFSET+(int)size);
        Log.i("LENGTH", "" + tlv.length);
        String[] tab = (new String(tmp)).split(",");

        for(byte index = 0; index<tab.length - 1; index++)
        {
            command.add(tab[index]);
        }

        command.add(tab[tab.length - 1].split("\n")[0]);
        return command;
    }

    public static byte[] CmdToTLV(String command, ArrayList<Object> args)
    {
        StringBuilder strBuilder = new StringBuilder();
        TLVstruct tlv = new TLVstruct();

        strBuilder.append(command);

        for (Object obj : args)
        {
            strBuilder.append(",");
            strBuilder.append(obj.toString());
        }

        strBuilder.append("\n");
        String data = strBuilder.toString();

        return tlv.buildTLVdataHeader(true, data.getBytes(),(long)data.length());
    }


    public static byte[] CmdToTLV(ArrayList<Object> args)
    {
        StringBuilder strBuilder = new StringBuilder();
        TLVstruct tlv = new TLVstruct();

        if((byte)args.get(0) < 15)
        {
            byte[] data = new byte[args.size()];
            data[0] = (byte)(args.get(0));
            for (int i = 1; i<args.size(); i++)
            {
                data[i] = (byte)args.get(i);
            }
            return tlv.buildTLVdataHeader(data, (long)args.size()-1);
        }
        else {
            for (Object obj : args) {
                strBuilder.append(obj.toString());
                strBuilder.append(",");
            }
            strBuilder.deleteCharAt(strBuilder.length()-1);
            strBuilder.append("\n");
            String data = strBuilder.toString();

            return tlv.buildTLVdataHeader(true, data.getBytes(),(long)data.length());
        }


    }

}
