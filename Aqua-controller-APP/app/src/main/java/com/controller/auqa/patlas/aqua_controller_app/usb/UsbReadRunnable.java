package com.controller.auqa.patlas.aqua_controller_app.usb;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbRequest;
import android.util.Log;

import java.nio.ByteBuffer;

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

    private EventBus bus = EventBus.getDefault();

    public UsbReadRunnable(UsbDeviceConnection connection, AquaUSB aquaUsb) //TODO - add read queue
    {
        this.connection = connection;
        this.aquaUSB = aquaUsb;

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
                bus.post(new String(buffer.array()));
                Log.i("RECEIVED", (new String(buffer.array())));
            }
        }
    }

}
