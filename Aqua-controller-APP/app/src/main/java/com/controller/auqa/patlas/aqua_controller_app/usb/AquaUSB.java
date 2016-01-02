package com.controller.auqa.patlas.aqua_controller_app.usb;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Created by PatLas on 2016-01-02.
 */
public class AquaUSB
{
    public static int BUFFER_SIZE = 64;
    private Context context = null;
    private UsbInterface usbInterface = null;

    public AquaUSB(Context context)
    {
        this.context = context;
    }

    public UsbDevice findDevice(Context context, int vid, int pid) throws Exception
    {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        UsbDevice device;

        while (deviceIterator.hasNext())
        {
            device = deviceIterator.next();
            if (device.getProductId() == pid && device.getVendorId() == vid)
                return device;
        }
        throw new Exception("Device with VID: "+vid+" and PID: "+pid+" not found.");
    }

    private Hashtable<String, UsbEndpoint> obtainEndpoints() throws Exception
    {

        Hashtable<String, UsbEndpoint> endpointHash = new Hashtable<>(2);

        if (usbInterface == null)
            throw new Exception("UsbInterface not obtained. First call openConnection() method.");

        for (int i = 0; i < usbInterface.getEndpointCount(); i++)
        {
            if (usbInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN)
            {
                endpointHash.put("epIN", usbInterface.getEndpoint(i));
            }
            else
            {
                endpointHash.put("epOUT", usbInterface.getEndpoint(i));
            }

            if(endpointHash.size() == 2)
                break;
        }
        return endpointHash;
    }

    public UsbDeviceConnection openConnection(UsbDevice device, int intefraceNr) throws Exception
    {
        usbInterface = device.getInterface(intefraceNr);
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        UsbDeviceConnection connection = manager.openDevice(device);
        if(connection == null)
            throw new Exception("Could not open device.");

        if(!connection.claimInterface(usbInterface, true))
            throw new Exception("Could not claim interface "+usbInterface.getId()+".");

        return connection;
    }

    public UsbRequest prepareRead(UsbDeviceConnection connection) throws Exception
    {
        UsbEndpoint endpoint;
        UsbRequest request;

        try
        {
            endpoint = obtainEndpoints().get("epIN");
            request = new UsbRequest();
        } catch(Exception ex)
        {
            throw new Exception(ex.getMessage());
        }

        boolean initilzed = request.initialize(connection, endpoint);

        if (!initilzed)
        {
            throw new Exception("Could not initialize epIN request.");
        }

        return request;
    }


    public ByteBuffer readRawData(UsbDeviceConnection connection, UsbRequest request, ByteBuffer buffer, int bufsize)
    {
            if (request.queue(buffer, bufsize) == true)
            {
                if (connection.requestWait() == request)
                {
                    //String result = new String(buffer.array());
                    Log.i("GELEN DATA : ", new String(buffer.array()));
                    return buffer;
                }
            }
        return null;
    }

}
