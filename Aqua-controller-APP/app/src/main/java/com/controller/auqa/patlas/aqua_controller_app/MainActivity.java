package com.controller.auqa.patlas.aqua_controller_app;

import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.controller.auqa.patlas.aqua_controller_app.usb.AquaUSB;
import com.controller.auqa.patlas.aqua_controller_app.usb.UsbReadRunnable;
import com.controller.auqa.patlas.aqua_controller_app.usb.UsbWriteRunnable;
import com.controller.auqa.patlas.aqua_controller_app.utils.Command;
import com.controller.auqa.patlas.aqua_controller_app.utils.CommandList;
import com.controller.auqa.patlas.aqua_controller_app.utils.UserSettings;

import java.util.ArrayList;
import java.util.Hashtable;
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
    private float set_temp = 0;
    private float set_ph = 0;

    /**
        1) EACH one min/10sec ask CPU about temp, ph, led and out status
        2) if go into out/led setting load info from CPU about time and another
     **/

    @Override
    public void onResume()
    {
        super.onResume();
        hideTopBar();

        UserSettings userSettings = UserSettings.getInstance();

        Object temp = userSettings.get("s_temp");
        Object termo = userSettings.get("termo");
        Object auto_co2 = userSettings.get("auto_co2");
        Object set_ph = userSettings.get("s_ph");

        if((boolean)termo == true) {
            TextView stemp_tv = ((TextView) findViewById(R.id.s_temp));
            stemp_tv.setTextColor(getResources().getColor(R.color.colorConnected));
            stemp_tv.setText(""+temp+"°C");
            Log.e("RESUME_sTEMP", "" + temp);
        }
        else{
            Log.e("RESUME", "NULL");
            ((TextView)findViewById(R.id.s_temp)).setTextColor(0x00);
        }

        if((boolean)auto_co2 == true) {
            TextView sph_tv = ((TextView) findViewById(R.id.set_ph));
            TextView auto = (TextView) findViewById(R.id.auto_co2);
            sph_tv.setTextColor(getResources().getColor(R.color.colorConnected));
            sph_tv.setText("" + set_ph);
            auto.setTextColor(getResources().getColor(R.color.co2_color));
            Log.e("RESUME_sPH", "" + set_ph);
        }
        else{
            Log.e("RESUME", "NULL");
            ((TextView)findViewById(R.id.set_ph)).setTextColor(0x00);
            ((TextView)findViewById(R.id.auto_co2)).setTextColor(0x00);
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        UserSettings.getInstance().save("termo", false);
        UserSettings.getInstance().save("auto_co2", false);
        UserSettings.getInstance().save("s_temp", 22.0); // TODO - nie dziala tylko float inne ok!!!!
        UserSettings.getInstance().save("s_ph", 7.0);
        UserSettings.getInstance().save("out_checkboxes", 255);

        ui_strings.put("connected", "C\nO\nN\nN\nE\nC\nT\nE\nD");
        ui_strings.put("disconnected", "D\nI\nS\nC\nO\nN\nN\nE\nC\nT\nE\nD");
        //hideNaviBar();
        hideTopBar();
        //autoHider();
        setContentView(R.layout.activity_main);

//        rescaleLayout();

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
            UserSettings.getInstance().save("UsbWriteRunnable", usbWriteRunnable);
            UserSettings.getInstance().save("ReceiverQueue", receiver);
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



        CommandList commandList = CommandList.getInstance();
        commandList.addCommand("patlas", new Command()
        {
            @Override
            public void execute(ArrayList<String> objects)
            {
                TextView tv = (TextView) findViewById(R.id.ph);
                tv.append("registered command 1\nargs: ");
                for (byte index = 1; index < objects.size(); index++)
                {
                    tv.append(objects.get(index) + ", ");
                }
            }
        });


        LinearLayout settingsLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Intent temp_act = new Intent(MainActivity.this, TempActivity.class);
                //startActivity(temp_act);
                usbWriteRunnable.WriteUSB("temperatura", null);
            }

        });

        LinearLayout powerLayout = (LinearLayout) findViewById(R.id.out13LinearLayout);
        powerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Intent out_act = new Intent(MainActivity.this, PowerActivity.class);
                startActivity(out_act);
            }

        });

    }

    @Subscribe
    public void onEventMainThread(String event)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> x;
                x = receiver.poll();
                Log.i("EVENT", "" + x.size() + "" + x.get(0) /*+ "" + x.get(1)*/);
                CommandList cl = CommandList.getInstance();

                TextView tv = (TextView) findViewById(R.id.ph);
                tv.append("TEST\n");

                cl.executeCommand(x.get(0), x);
            }
        });

        ArrayList<Object> data = new ArrayList<Object>();
        data.add(3);
        usbWriteRunnable.WriteUSB("temperatura", data);

    }

    @Subscribe
    public void onEventMainThread(UserSettings settings)
    {
        runOnUiThread(new UIrunnable(settings)
        {
            @Override
            public void run()
            {

            }
        });
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
        Log.e("ICON_HEIGHT", "" + icon_height);
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




abstract class UIrunnable implements Runnable
{
    public Object data;
    public UIrunnable(Object obj)
    {
        data = obj;
    }

}