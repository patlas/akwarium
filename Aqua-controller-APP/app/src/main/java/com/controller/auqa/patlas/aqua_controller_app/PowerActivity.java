package com.controller.auqa.patlas.aqua_controller_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

    }

    public void hideTopBar()
    {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
