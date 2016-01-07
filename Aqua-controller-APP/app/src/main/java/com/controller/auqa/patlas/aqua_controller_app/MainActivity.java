package com.controller.auqa.patlas.aqua_controller_app;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Typeface;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private static int AUTO_HIDE_TIMEOUT = 2000;
    private boolean mVisible = true;

    private EventBus bus = EventBus.getDefault();
    public LinkedBlockingQueue<ArrayList<String>> receiver = new LinkedBlockingQueue<ArrayList<String>>();
    public LinkedBlockingQueue<ArrayList<Object>> transmiter = new LinkedBlockingQueue<ArrayList<Object>>();
    private UsbWriteRunnable usbWriteRunnable;


    public TextView tv_connectInfo = null;
    private Hashtable<String, String> ui_strings= new Hashtable<String, String>();

    @Override
    public void onResume()
    {
        super.onResume();
        hideTopBar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ui_strings.put("connected", "C\nO\nN\nN\nE\nC\nT\nE\nD");
        ui_strings.put("disconnected","D\nI\nS\nC\nO\nN\nN\nE\nC\nT\nE\nD");
        //hideNaviBar();
        hideTopBar();
        //autoHider();
        setContentView(R.layout.activity_main);

        rescaleLayout();

        tv_connectInfo = (TextView) findViewById(R.id.tv_connectInfo);


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
            rxThread.start();
            txThread.start();
        } catch (Exception ex)
        {
            Log.e("EXCEPTION", ex.getMessage());
            tv_connectInfo.setText(ui_strings.get("disconnected"));
            tv_connectInfo.setTextColor(getResources().getColor(R.color.colorDisconnected));
            //return;
        }

        RelativeLayout tempLayout = (RelativeLayout) findViewById(R.id.tempLayout);
        tempLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //hideNaviBar();
                Log.i("FSDF", "adf");
                Log.i("Navi bar size: ", ""+getStatusBarHeight());
            }
        });




        CommandList commandList = CommandList.getInstance();
        commandList.addCommand("patlas", new Command()
        {
            @Override
            public void execute(ArrayList<String> objects)
            {
                TextView tv = (TextView) findViewById(R.id.textView);
                tv.append("registered command 1\nargs: ");
                for (byte index = 1; index < objects.size(); index++)
                {
                    tv.append(objects.get(index) + ", ");
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


    public void hideNaviBar()
    {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;// | View.SYSTEM_UI_FLAG_FULLSCREEN;

        if(mVisible == false)
        {
            Log.i("HIDE", "DO NOTHING");
        }
        else
        {
            decorView.setSystemUiVisibility(uiOptions);
            mVisible = false;
            Log.i("HIDE", "HIDING");
        }
    }

    public void hideTopBar()
    {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    public void autoHider()
    {
        new Thread(new Runnable()
        {
//            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            @Override
            public void run()
            {
                if(mVisible == true)
                {
                    try
                    {
                        Thread.sleep(MainActivity.AUTO_HIDE_TIMEOUT);
                    } catch (InterruptedException ie){}

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
//                            decorView.setSystemUiVisibility(uiOptions);
                            //hideNaviBar();
                        }
                    });
                }
            }
        }).start();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void rescaleLayout()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //Log.d("ApplicationTagName", "Display width in px is " + metrics.widthPixels + "" + metrics.heightPixels);
        int objectHeight = (metrics.heightPixels-4*3)/2;

        Log.d("Height",""+objectHeight);

        float main_width = getResources().getDimension(R.dimen.icon_main_info_width);
        float setting_width = getResources().getDimension(R.dimen.icon_setting_width);
        float icon_height = getResources().getDimension(R.dimen.icon_height);
        float text_size = getResources().getDimension(R.dimen.text_size);

        float scale = objectHeight/icon_height;

        Log.d("Scale", "" + scale);

        LinearLayout main_settings = (LinearLayout) findViewById(R.id.mainLinearLayout);
        LinearLayout led_settings = (LinearLayout) findViewById(R.id.ledLinearLayout);
        LinearLayout out13_settings = (LinearLayout) findViewById(R.id.out13LinearLayout);
        LinearLayout out24_settings = (LinearLayout) findViewById(R.id.out24LinearLayout);
        LinearLayout tvLinearLayout = (LinearLayout) findViewById(R.id.tvLinearLayout);

        RelativeLayout tempLayout = (RelativeLayout) findViewById(R.id.tempLayout);
        RelativeLayout phLayout = (RelativeLayout) findViewById(R.id.phLayout);
        RelativeLayout led1Layout = (RelativeLayout) findViewById(R.id.led1Layout);
        RelativeLayout led2Layout = (RelativeLayout) findViewById(R.id.led2Layout);
        RelativeLayout out1Layout = (RelativeLayout) findViewById(R.id.out1Layout);
        RelativeLayout out2Layout = (RelativeLayout) findViewById(R.id.out2Layout);
        RelativeLayout out3Layout = (RelativeLayout) findViewById(R.id.out3Layout);
        RelativeLayout out4Layout = (RelativeLayout) findViewById(R.id.out4Layout);

        TextView tv_connectInfo = (TextView) findViewById(R.id.tv_connectInfo);

        Log.d("Setting_width", "" + setting_width);
        setting_width = setting_width * scale;
        Log.d("Setting_width", "" + setting_width);


        main_settings.setLayoutParams(new LinearLayout.LayoutParams((int) (main_width * scale), LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams((int) setting_width, LinearLayout.LayoutParams.MATCH_PARENT);
        led_settings.setLayoutParams(lp1);
        out13_settings.setLayoutParams(lp1);
        out24_settings.setLayoutParams(lp1);


        icon_height = icon_height*scale;
        tempLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(icon_height)));

        phLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(icon_height)));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(icon_height));
        led1Layout.setLayoutParams(lp);
        led2Layout.setLayoutParams(lp);
        out1Layout.setLayoutParams(lp);
        out2Layout.setLayoutParams(lp);
        out3Layout.setLayoutParams(lp);
        out4Layout.setLayoutParams(lp);

//        int status_bar_height = getStatusBarHeight();
//        int tv_size =  metrics.widthPixels - (int)(3*setting_width+main_width+5*4) + status_bar_height;
//        tvLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(tv_size, (int)(icon_height*2+8)));

        text_size = text_size*scale;
        tv_connectInfo.setTextSize(text_size);
        tv_connectInfo.setTypeface(null, Typeface.BOLD);
    }

}
