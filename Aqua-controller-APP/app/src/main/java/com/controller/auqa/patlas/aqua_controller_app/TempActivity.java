package com.controller.auqa.patlas.aqua_controller_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.controller.auqa.patlas.aqua_controller_app.utils.UserSettings;

import de.greenrobot.event.EventBus;

public class TempActivity extends AppCompatActivity
{
    private EventBus setting_bus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);


        int val = 0;

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View npView = inflater.inflate(R.layout.termostat_dialog, null );

        AlertDialog.Builder picker_builder = new AlertDialog.Builder(this);
        picker_builder.setTitle(R.string.termo_dialog);
        picker_builder.setView(npView);
        picker_builder.setCancelable(true);

        NumberPicker temp_picker = (NumberPicker) npView.findViewById(R.id.termo_val);
        NumberPicker tenth_picker = (NumberPicker) npView.findViewById(R.id.termo_val2);

        temp_picker.setMinValue(18);
        temp_picker.setMaxValue(30);
        temp_picker.setValue(22);

        tenth_picker.setMinValue(0);
        tenth_picker.setMaxValue(9);
        tenth_picker.setValue(0);

        picker_builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                HARDCODED
                UserSettings.getInstance().save("temp", 8);
                dialog.cancel();
            }
        });

        AlertDialog ad = picker_builder.create();
        ad.getWindow().setLayout(100, 100);
        ad.show();
//
//        Context context = getApplicationContext();
//        int duration = Toast.LENGTH_SHORT;
//
//        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.termo_val);
//        val = numberPicker.getValue();
//
//        Toast toast = Toast.makeText(context, ""+val, duration);
//        toast.show();


    }

}