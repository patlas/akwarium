package com.controller.auqa.patlas.aqua_controller_app.usb;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbRequest;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import de.greenrobot.event.EventBus;

/**
 * Created by PatLas on 2016-01-02.
 */
public class UsbWriteRunnable implements Runnable
{

    AquaUSB aquaUSB;
    ByteBuffer buffer = ByteBuffer.allocate(AquaUSB.BUFFER_SIZE);
    private LinkedBlockingQueue<ArrayList<Object>> transmiter;
    private ArrayList<Object> txData = new ArrayList<>();


    public UsbWriteRunnable(LinkedBlockingQueue<ArrayList<Object>> transQueue, AquaUSB aquaUsb) //TODO - add read queue
    {
        this.aquaUSB = aquaUsb;
        this.transmiter = transQueue;

        try
        {
            aquaUsb.prepareWrite();
        } catch(Exception ex)
        {
            Log.e("ERROR", ex.getMessage());
        }
    }


    public void run()
    {
        while(true)
        {
            //TODO - add insertion to read queue
            if ((txData = transmiter.poll()) != null)
            {
                //prepare data
                buffer.clear();
                buffer.put(CommandParser.CmdToTLV(txData));
                //transmit data

                if(!aquaUSB.writeRawData(buffer, AquaUSB.BUFFER_SIZE))
                {
                    Log.e("ERROR", "Send data error!");
                }
                else
                {
                    Log.i("INFO", "Data send");
                }

            }
        }
    }

    public boolean WriteUSB(String commands, ArrayList<Object> args)
    {
        ArrayList<Object> txData = new ArrayList<>();

        if(commands != null)
            txData.add(commands);

        if(args != null) {
            for (Object obj : args) {
                txData.add(obj);
            }
        }

        try
        {
            transmiter.put(txData);
        } catch (InterruptedException ie) {
            Log.e("ERROR", ie.getMessage());
            return false;
        }
        Log.i("QUEUE", "Insert in transmit queue");

        return true;
    }


}
