package com.controller.auqa.patlas.aqua_controller_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.controller.auqa.patlas.aqua_controller_app.utils.OutDialog;
import com.controller.auqa.patlas.aqua_controller_app.utils.UserSettings;

import de.greenrobot.event.EventBus;

public class PowerActivity extends AppCompatActivity
{

    @Override
    public void onResume() {
        super.onResume();
        hideTopBar();
        setDefault();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out);

        hideTopBar();

        LinearLayout out1_tim1 = (LinearLayout) findViewById(R.id.out1_time1);
        LinearLayout out1_tim2 = (LinearLayout) findViewById(R.id.out1_time2);

        LinearLayout out2_tim1 = (LinearLayout) findViewById(R.id.out2_time1);
        LinearLayout out2_tim2 = (LinearLayout) findViewById(R.id.out2_time2);

        LinearLayout out3_tim1 = (LinearLayout) findViewById(R.id.out3_time1);
        LinearLayout out3_tim2 = (LinearLayout) findViewById(R.id.out3_time2);

        LinearLayout out4_tim1 = (LinearLayout) findViewById(R.id.out4_time1);
        LinearLayout out4_tim2 = (LinearLayout) findViewById(R.id.out4_time2);

        CheckBox out11_checkbox = (CheckBox) findViewById(R.id.out11_checkbox);
        CheckBox out12_checkbox = (CheckBox) findViewById(R.id.out12_checkbox);

        CheckBox out21_checkbox = (CheckBox) findViewById(R.id.out21_checkbox);
        CheckBox out22_checkbox = (CheckBox) findViewById(R.id.out22_checkbox);

        CheckBox out31_checkbox = (CheckBox) findViewById(R.id.out31_checkbox);
        CheckBox out32_checkbox = (CheckBox) findViewById(R.id.out32_checkbox);

        CheckBox out41_checkbox = (CheckBox) findViewById(R.id.out41_checkbox);
        CheckBox out42_checkbox = (CheckBox) findViewById(R.id.out42_checkbox);

        setDefault();

        out1_tim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new OutDialog(PowerActivity.this, 11).show();
                int[] data = (int[]) UserSettings.getInstance().get("out_time");


            }
        });

        out1_tim2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OutDialog(PowerActivity.this, 12).show();
            }
        });

        out2_tim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OutDialog(PowerActivity.this, 21).show();
            }
        });

        out2_tim2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OutDialog(PowerActivity.this, 22).show();
            }
        });

        out3_tim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OutDialog(PowerActivity.this, 31).show();
            }
        });

        out3_tim2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OutDialog(PowerActivity.this, 32).show();
            }
        });

        out4_tim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OutDialog(PowerActivity.this, 41).show();
            }
        });

        out4_tim2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OutDialog(PowerActivity.this, 42).show();
            }
        });

        out11_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserSettings.getInstance().save("out11_checkbox", isChecked);
            }
        });

        out12_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserSettings.getInstance().save("out12_checkbox", isChecked);
            }
        });

        out21_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserSettings.getInstance().save("out21_checkbox",isChecked);
            }
        });

        out22_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserSettings.getInstance().save("out22_checkbox",isChecked);
            }
        });

        out31_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserSettings.getInstance().save("out31_checkbox",isChecked);
            }
        });

        out32_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserSettings.getInstance().save("out32_checkbox",isChecked);
            }
        });

        out41_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserSettings.getInstance().save("out41_checkbox",isChecked);
            }
        });

        out42_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UserSettings.getInstance().save("out42_checkbox",isChecked);
            }
        });


    }

    public void setDefault()
    {
        int[] id = {11,12,21,22,31,32,41,42};

        for(int i : id) {

            Object dat = UserSettings.getInstance().get("out_time_"+i);
            int data[] = {0, 0, 0, 0};

            if(dat != null)
                data = (int[])dat;

            String[] str_name = {"out"+id+"_start", "out"+i+"_stop"};

            String start_id = "out" + i + "_start";
            String stop_id = "out" + i + "_stop";
            int start = getResources().getIdentifier(start_id, "id", getPackageName());
            int stop = getResources().getIdentifier(stop_id, "id", getPackageName());

            ((TextView) findViewById(start)).setText("" + data[0] + ":" + String.format("%02d", data[1]));
            ((TextView) findViewById(stop)).setText("" + data[2] + ":" + String.format("%02d", data[3]));

            String check_str = "out" + i + "_checkbox";
            int checkbox = getResources().getIdentifier(check_str, "id", getPackageName());

            Object check = UserSettings.getInstance().get("out" + i + "_checkbox");
            boolean checks = false;

            if(check != null)
                checks = (boolean)check;

            ((CheckBox) findViewById(checkbox)).setChecked(checks);

        }


    }

    public void hideTopBar()
    {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
