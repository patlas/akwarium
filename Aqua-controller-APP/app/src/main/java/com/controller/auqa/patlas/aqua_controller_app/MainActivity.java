package com.controller.auqa.patlas.aqua_controller_app;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.controller.auqa.patlas.aqua_controller_app.usb.AquaUSB;
import com.controller.auqa.patlas.aqua_controller_app.usb.UsbReadRunnable;
import com.controller.auqa.patlas.aqua_controller_app.usb.UsbWriteRunnable;
import com.controller.auqa.patlas.aqua_controller_app.utils.Command;
import com.controller.auqa.patlas.aqua_controller_app.utils.CommandList;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity
{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;
    private View mControlsView;
    private boolean mVisible;

    private EventBus bus = EventBus.getDefault();
    public LinkedBlockingQueue<ArrayList<String>> receiver = new LinkedBlockingQueue<ArrayList<String>>();
    public LinkedBlockingQueue<ArrayList<Object>> transmiter = new LinkedBlockingQueue<ArrayList<Object>>();
    private UsbWriteRunnable usbWriteRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
       // actionBar.hide();

        setContentView(R.layout.activity_main);


        bus.register(this);
        AquaUSB aqUsb = new AquaUSB(this);
        UsbDevice device;
        UsbDeviceConnection communication;


        Thread rxThread;
        Thread txThread;


        try
        {
            device = aqUsb.findDevice(this, 1155, 22352);
            communication = aqUsb.openConnection(device, 0);
            UsbReadRunnable usbReadRunnable = new UsbReadRunnable(communication, receiver, aqUsb);
            usbWriteRunnable = new UsbWriteRunnable(communication, transmiter, aqUsb);
            rxThread = new Thread(usbReadRunnable);
            txThread = new Thread(usbWriteRunnable);
        } catch (Exception ex)
        {
            Log.e("EXCEPTION", ex.getMessage());
            return;
        }



        rxThread.start();
        txThread.start();


        CommandList commandList = CommandList.getInstance();
        commandList.addCommand("patlas", new Command()
        {
            @Override
            public void execute(ArrayList<String> objects)
            {
                TextView tv = (TextView) findViewById(R.id.textView);
                tv.append("registered command 1\nargs: ");
                for (byte index = 1; index < objects.size(); index++) {
                    tv.append(objects.get(index)+", ");
                }
            }
        });

    }

    @Subscribe
    public void onEventMainThread(String event)
    {


        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ArrayList<String> x;
                x = receiver.poll();
                Log.i("EVENT", "" + x.size() + "" + x.get(0) /*+ "" + x.get(1)*/);
                CommandList cl = CommandList.getInstance();

                TextView tv = (TextView) findViewById(R.id.textView);
                tv.append("TEST\n");

                cl.executeCommand(x.get(0), x);
            }
        });

        ArrayList<Object> data = new ArrayList<Object>();
        data.add(3);
        usbWriteRunnable.WriteUSB("temperatura", data);

    }

}
