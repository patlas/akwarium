package com.controller.auqa.patlas.aqua_controller_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.controller.auqa.patlas.aqua_controller_app.utils.FloatPicker;
import com.controller.auqa.patlas.aqua_controller_app.utils.UserSettings;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import de.greenrobot.event.EventBus;

public class TempActivity extends AppCompatActivity
{
    private EventBus setting_bus = EventBus.getDefault();

    @Override
    public void onResume() {
        super.onResume();
        hideTopBar();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        hideTopBar();

        int val = 0;

//        DIALOG FOR TERMOSTAT SETTINGS
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View npView = inflater.inflate(R.layout.termostat_dialog, null );

        AlertDialog.Builder picker_builder = new AlertDialog.Builder(this);
        picker_builder.setTitle(R.string.termo_dialog);
        picker_builder.setView(npView);
        picker_builder.setCancelable(true);

        final NumberPicker temp_picker = (NumberPicker) npView.findViewById(R.id.termo_val);
        final NumberPicker tenth_picker = (NumberPicker) npView.findViewById(R.id.termo_val2);

        temp_picker.setMinValue(18);
        temp_picker.setMaxValue(30);
        temp_picker.setValue(22);

        tenth_picker.setMinValue(0);
        tenth_picker.setMaxValue(9);
        tenth_picker.setValue(0);

        picker_builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                float temp = temp_picker.getValue() + (float) (tenth_picker.getValue() / 10.0);
                ((TextView) findViewById(R.id.temp_term)).setText("" + temp + "°C");
                UserSettings.getInstance().save("s_temp", temp);
                hideTopBar();
                dialog.cancel();
            }
        });

        final AlertDialog ad = picker_builder.create();
        ad.getWindow().setLayout(100, 100);
///////////////////////////////////////////////////////////////////////////////////////////////////
//      DIALOG FOR PH SETTINGS
        LayoutInflater inflater_ph = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View npView_ph = inflater.inflate(R.layout.ph_dialog, null );

        AlertDialog.Builder picker_builder_ph = new AlertDialog.Builder(this);
        picker_builder_ph.setTitle(R.string.ph_dialog);
        picker_builder_ph.setView(npView_ph);
        picker_builder_ph.setCancelable(true);

        final NumberPicker ph_picker = (NumberPicker) npView_ph.findViewById(R.id.ph_val);
        final NumberPicker ph_tenth_picker = (NumberPicker) npView_ph.findViewById(R.id.ph_val2);

        ph_picker.setMinValue(5);
        ph_picker.setMaxValue(8);
        ph_picker.setValue(7);

        ph_tenth_picker.setMinValue(0);
        ph_tenth_picker.setMaxValue(9);
        ph_tenth_picker.setValue(0);

        picker_builder_ph.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                float ph = ph_picker.getValue() + (float) (ph_tenth_picker.getValue() / 10.0);
                ((TextView) findViewById(R.id.set_ph)).setText("" + ph);
                UserSettings.getInstance().save("s_ph", ph);
                hideTopBar();
                dialog.cancel();
            }
        });

        final AlertDialog ad_ph = picker_builder_ph.create();
        ad_ph.getWindow().setLayout(100, 100);
//////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
//      DIALOG FOR PH CALIBRATION
        LayoutInflater inflater_calib = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View npView_calib = inflater_calib.inflate(R.layout.calibration_dialog, null );

        AlertDialog.Builder calib_builder = new AlertDialog.Builder(this);
        calib_builder.setTitle(R.string.calib_dialog);
        calib_builder.setView(npView_calib);
        calib_builder.setCancelable(true);

        final NumberPicker calib_picker = (NumberPicker) npView_calib.findViewById(R.id.calib_picker);
        final FloatPicker fp = new FloatPicker(4.0, 10.9, 0.1);
        fp.fillStringArray();
        String picker_vales[] = fp.getStringArray();

        calib_picker.setMinValue(0);
        calib_picker.setMaxValue(picker_vales.length - 1);
        calib_picker.setValue(picker_vales.length / 2);
        calib_picker.setDisplayedValues(picker_vales);


        calib_builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                float calib_ph = Float.parseFloat(fp.getStringArray()[calib_picker.getValue()]);
                UserSettings.getInstance().save("calib_ph", calib_ph);
//                wysłać informacje do uC o kalibracji, wyswietlid dialog czekajacy na info z usb o koncu kalibracji

                ArrayList<String> x;
//                not poll but only check if proper one?? maby bool will be proper one
                LinkedBlockingQueue<ArrayList<String>>  receiver = (LinkedBlockingQueue<ArrayList<String>>) UserSettings.getInstance().get("ReceiverQueue");

//                x = receiver.poll();
                hideTopBar();

//                start async task
                ProgressDialog progress_dialog;
                progress_dialog = new ProgressDialog(TempActivity.this);
                progress_dialog.setTitle("Probe calibration...");
                progress_dialog.setMessage("Please wait until pH probe calibration will be done.");

                progress_dialog.show();
                CalibrationAsyncTask task = new CalibrationAsyncTask(progress_dialog);
                task.execute(receiver);
                dialog.dismiss();
            }
        });

        final AlertDialog ad_calib = calib_builder.create();
        ad_calib.getWindow().setLayout(100, 100);
///////////////////////////////////////////////////////////////////////////////////////////////////



        Switch termo_on = (Switch)findViewById(R.id.temp_termon);
        TextView termo_tv = (TextView) findViewById(R.id.temp_term);
        TextView ph_set = (TextView) findViewById(R.id.set_ph);
        Switch co2_on = (Switch) findViewById(R.id.co2_auto);
        LinearLayout ph_layout = (LinearLayout) findViewById(R.id.ph_layout);
        LinearLayout termo_layout = (LinearLayout) findViewById(R.id.termo_layout);

        termo_on.setChecked((boolean) UserSettings.getInstance().get("termo"));
        co2_on.setChecked((boolean)UserSettings.getInstance().get("auto_co2"));

        termo_tv.setText("" + UserSettings.getInstance().get("s_temp") + "°C");
        ph_set.setText("" + UserSettings.getInstance().get("s_ph"));

        termo_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    UserSettings.getInstance().save("termo", true);
                } else {
                    UserSettings.getInstance().save("termo", false);
                }
            }
        });

        co2_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    UserSettings.getInstance().save("auto_co2", true);
                } else {
                    UserSettings.getInstance().save("auto_co2", false);
                }
            }
        });

        Button calib_btn_low = (Button) findViewById(R.id.calib_btn_low);
        calib_btn_low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                // low sol calibration liquid
                TextView calib_tv = (TextView) npView_calib.findViewById(R.id.calib_info_tv);
                calib_tv.setText(getString(R.string.low_sol_info));
                ad_calib.show();
            }

        });

        Button calib_btn_high = (Button) findViewById(R.id.calib_btn_high);
        calib_btn_high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                // low sol calibration liquid
                TextView calib_tv = (TextView) npView_calib.findViewById(R.id.calib_info_tv);
                calib_tv.setText(getString(R.string.high_sol_info));
                ad_calib.show();
            }

        });

        termo_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                ad.show();
            }
        });

        ph_layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View w){
                ad_ph.show();
            }
        });
    }


    public void hideTopBar()
    {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}

class CalibrationAsyncTask extends AsyncTask<LinkedBlockingQueue<ArrayList<String>>, Void, Boolean>{

    ProgressDialog progress_dialog = null;

    public CalibrationAsyncTask(ProgressDialog pd){
        progress_dialog = pd;


    }

    protected Boolean doInBackground(LinkedBlockingQueue<ArrayList<String>> ... receiver) {

//        progress_dialog.show();
        ArrayList<String> x;
        while(true) {
         // check if read from usb that calibration done instead of sleep(2000)

            //x = receiver[0].poll();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie){}

            int a = 2;
            break;
        }
        return true;
    }


    protected void onPostExecute(Boolean ret) {
//        hide progress
        progress_dialog.dismiss();
    }
}