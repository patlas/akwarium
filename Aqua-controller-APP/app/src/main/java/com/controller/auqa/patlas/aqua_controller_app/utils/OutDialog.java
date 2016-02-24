package com.controller.auqa.patlas.aqua_controller_app.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.controller.auqa.patlas.aqua_controller_app.R;

/**
 * Created by PatLas on 23.02.2016.
 */
public class OutDialog extends Dialog implements View.OnClickListener {

    public Activity parent_activity;
    public Dialog d;
    public Button save;

    int id;

    NumberPicker h1;
    NumberPicker m1;
    NumberPicker h2;
    NumberPicker m2;

    public OutDialog(Activity a, int id) {
        super(a);
        this.parent_activity = a;
        this.setTitle("OUTPUT TIME SETTINGS");
        this.id = id;
    }

    @Override
    protected void onCreate(Bundle sInstance) {
        super.onCreate(sInstance);
        setContentView(R.layout.out_time_dialog);

        h1 = (NumberPicker) findViewById(R.id.out_hour_start);
        h2 = (NumberPicker) findViewById(R.id.out_hour_stop);
        m1 = (NumberPicker) findViewById(R.id.out_min_start);
        m2 = (NumberPicker) findViewById(R.id.out_min_stop);

        h1.setMaxValue(23);
        h2.setMaxValue(23);
        h1.setMinValue(0);
        h2.setMinValue(0);

        m1.setMinValue(0);
        m2.setMinValue(0);
        m1.setMaxValue(59);
        m2.setMaxValue(59);

        m1.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });

        m2.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });

        save = (Button) findViewById(R.id.button);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int data[] = {h1.getValue(),m1.getValue(),h2.getValue(),m2.getValue()};
        UserSettings.getInstance().save("out_time", data);

        String[] str_name = {"out"+id+"_start", "out"+id+"_stop"};

        int resID1 = parent_activity.getResources().getIdentifier(str_name[0], "id", parent_activity.getPackageName());
        int resID2 = parent_activity.getResources().getIdentifier(str_name[1], "id", parent_activity.getPackageName());
        ((TextView) parent_activity.findViewById(resID1)).setText("" + data[0] + ":" + String.format("%02d", data[1]));
        ((TextView) parent_activity.findViewById(resID2)).setText("" + data[2] + ":" + String.format("%02d", data[3]));

        dismiss();
    }

}
