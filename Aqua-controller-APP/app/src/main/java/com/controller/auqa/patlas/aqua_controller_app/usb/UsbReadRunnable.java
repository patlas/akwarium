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
public class UsbReadRunnable implements Runnable
{
    UsbDeviceConnection connection;
    UsbRequest request;
    AquaUSB aquaUSB;
    ByteBuffer buffer = ByteBuffer.allocate(AquaUSB.BUFFER_SIZE);
    private LinkedBlockingQueue<ArrayList<String>> receiver;

    private EventBus bus = EventBus.getDefault();

    public UsbReadRunnable(UsbDeviceConnection connection, LinkedBlockingQueue<ArrayList<String>> recQueue, AquaUSB aquaUsb) //TODO - add read queue
    {
        this.connection = connection;
        this.aquaUSB = aquaUsb;
        this.receiver = recQueue;

        try
        {
            request = aquaUsb.prepareRead(connection);
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
            if (aquaUSB.readRawData(connection, request, buffer, AquaUSB.BUFFER_SIZE) != null)
            {
                //data received
// uncomment
                try
                {
                    receiver.put(CommandParser.TLVtoCmd(buffer.array()));
                } catch (InterruptedException ie) {
                    Log.e("ERROR", ie.getMessage());
                }

//                byte[] a = buffer.array();
//                for( byte w : a)
//                    Log.i("RECEIVED",""+w );

                bus.post("test");
            }
        }
    }

    public String ReadUSB()
    {
        /**
         * normalnie zrobić swoja hashtable i w niej jako drugi parametr przyjmować liste objektów,
         * i każda funkcja przyjmuje liste obiektów (parie jak wskaźnik na argumenty) i przetwarza
         * argumenty na forme jaką zna
         **/
        //receiver.

    return "test";
    }

}
