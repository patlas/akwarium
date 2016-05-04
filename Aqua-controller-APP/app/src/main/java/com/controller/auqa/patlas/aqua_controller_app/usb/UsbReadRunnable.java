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
    AquaUSB aquaUSB;
    ByteBuffer buffer = ByteBuffer.allocate(AquaUSB.BUFFER_SIZE);
    private LinkedBlockingQueue<ArrayList<Object>> receiver;

    private EventBus bus = EventBus.getDefault();

    public UsbReadRunnable(LinkedBlockingQueue<ArrayList<Object>> recQueue, AquaUSB aquaUsb) //TODO - add read queue
    {
        this.aquaUSB = aquaUsb;
        this.receiver = recQueue;

        try
        {
            aquaUsb.prepareRead();
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
            if (aquaUSB.readRawData(buffer, AquaUSB.BUFFER_SIZE) != null)
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
