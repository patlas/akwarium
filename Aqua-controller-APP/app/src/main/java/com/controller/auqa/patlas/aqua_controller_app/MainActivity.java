package com.controller.auqa.patlas.aqua_controller_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.controller.auqa.patlas.aqua_controller_app.usb.AquaUSB;
import com.controller.auqa.patlas.aqua_controller_app.usb.UsbReadRunnable;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);




//        TextView tv = (TextView) findViewById(R.id.textView);
//
//        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
//        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//        UsbDevice device = null;
//        while(deviceIterator.hasNext()){
//            device = deviceIterator.next();
//            tv.append(device.getDeviceName()+ " PID: " + device.getProductId() + " VID: "+ device.getVendorId());
//            if( device.getProductId() == 22352)
//                break;
//        }
//
//        ByteBuffer buffer = ByteBuffer.allocate(10);
//        int TIMEOUT = 0;
//        boolean forceClaim = true;
//
//
//
//        UsbInterface intf = device.getInterface(0);
//        UsbEndpoint endpoint = intf.getEndpoint(0);
//        UsbDeviceConnection connection = manager.openDevice(device);
//        UsbRequest ureq = new UsbRequest();
//        ureq.initialize(connection, endpoint);

        AquaUSB aqUsb = new AquaUSB(this);
        UsbDevice device;
        UsbDeviceConnection communication;
        Thread rxThread;

        try
        {
            device = aqUsb.findDevice(this, 1155, 22352);
            communication = aqUsb.openConnection(device, 0);
            rxThread = new Thread(new UsbReadRunnable(communication, aqUsb));
        } catch (Exception ex)
        {
            Log.e("EXCEPTION", ex.getMessage());
            return;
        }

        rxThread.start();



//
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                TextView tv = (TextView) findViewById(R.id.textView);

//                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
//                HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
//                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//                UsbDevice device = null;
//                while(deviceIterator.hasNext()){
//                    device = deviceIterator.next();
//                    tv.append(device.getDeviceName()+ " PID: " + device.getProductId() + " VID: "+ device.getVendorId());
//                    if( device.getProductId() == 22352)
//                        break;
//                }



//
//                UsbInterface intf = device.getInterface(0);
//                UsbDeviceConnection connection = manager.openDevice(device);
//                UsbEndpoint endpoint = null;
//                connection.claimInterface(device.getInterface(0), true);

//                for (int i = 0; i < intf.getEndpointCount(); i++) {
//                    if (intf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
//                        endpoint = intf.getEndpoint(i);
//                        break;
//                    }
//                }
//                UsbRequest request = new UsbRequest(); // create an URB
//                boolean initilzed = request.initialize(connection, endpoint);
//
//                if (!initilzed) {
//                    Log.e("USB CONNECTION FAILED", "Request initialization failed for reading");
//                    return;
//                }
//                int bufferMaxLength = endpoint.getMaxPacketSize();
//                ByteBuffer buffer = ByteBuffer.allocate(bufferMaxLength);
//                while (true) {
//
//
//
//
//                    if (request.queue(buffer, bufferMaxLength) == true) {
//                        if (connection.requestWait() == request) {
//                            String result = new String(buffer.array());
//                            Log.i("GELEN DATA : ", result);
//
//                        }
//                    }
//                }



//                UsbEndpoint endpoint = intf.getEndpoint(0);
//                UsbDeviceConnection connection = manager.openDevice(device);
//                UsbRequest ureq = new UsbRequest();
//                ureq.initialize(connection, endpoint);
//
//                while(true) {
//                    if (ureq.queue(buffer, 4) == true) {
//                        connection.requestWait();
//                        tv.append(buffer.toString());
//                        tv.append("tatat\n");
//                        Log.e("TAG", "RECEIVED");
//                        break;
//                    }
//                    try{
//                        Thread.sleep(1000);
//                    }catch (InterruptedException ie){}
//                    Log.e("TAG","tiktak");
//                }
//
//            }
//
//        });
//
//        t.start();
//        while(true) {
//            if (ureq.queue(buffer, 10)) {
//                connection.requestWait();
//                tv.append(buffer.toString());
//                break;
//            }
//        }

//        connection.claimInterface(intf, forceClaim);
//        connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT); //do in another thread


    }
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        // Trigger the initial hide() shortly after the activity has been
//        // created, to briefly hint to the user that UI controls
//        // are available.
//        delayedHide(100);
//    }
//
//    /**
//     * Touch listener to use for in-layout UI controls to delay hiding the
//     * system UI. This is to prevent the jarring behavior of controls going away
//     * while interacting with activity UI.
//     */
//    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//            }
//            return false;
//        }
//    };
//
//    private void toggle() {
//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
//    }
//
//    private void hide() {
//        // Hide UI first
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
//        mControlsView.setVisibility(View.GONE);
//        mVisible = false;
//
//        // Schedule a runnable to remove the status and navigation bar after a delay
//        mHideHandler.removeCallbacks(mShowPart2Runnable);
//        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
//    }
//
//    private final Runnable mHidePart2Runnable = new Runnable() {
//        @SuppressLint("InlinedApi")
//        @Override
//        public void run() {
//            // Delayed removal of status and navigation bar
//
//            // Note that some of these constants are new as of API 16 (Jelly Bean)
//            // and API 19 (KitKat). It is safe to use them, as they are inlined
//            // at compile-time and do nothing on earlier devices.
//            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        }
//    };
//
//    @SuppressLint("InlinedApi")
//    private void show() {
//        // Show the system bar
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        mVisible = true;
//
//        // Schedule a runnable to display UI elements after a delay
//        mHideHandler.removeCallbacks(mHidePart2Runnable);
//        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
//    }
//
//    private final Runnable mShowPart2Runnable = new Runnable() {
//        @Override
//        public void run() {
//            // Delayed display of UI elements
//            ActionBar actionBar = getSupportActionBar();
//            if (actionBar != null) {
//                actionBar.show();
//            }
//            mControlsView.setVisibility(View.VISIBLE);
//        }
//    };
//
//    private final Handler mHideHandler = new Handler();
//    private final Runnable mHideRunnable = new Runnable() {
//        @Override
//        public void run() {
//            hide();
//        }
//    };
//
//    /**
//     * Schedules a call to hide() in [delay] milliseconds, canceling any
//     * previously scheduled calls.
//     */
//    private void delayedHide(int delayMillis) {
//        mHideHandler.removeCallbacks(mHideRunnable);
//        mHideHandler.postDelayed(mHideRunnable, delayMillis);
//    }
}
