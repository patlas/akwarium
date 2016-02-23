package com.controller.auqa.patlas.aqua_controller_app.utils;

/**
 * Created by PatLas on 23.02.2016.
 */
public class FloatPicker {

    int size;
    String[] values;
    double min;
    double max;
    double step;

    public FloatPicker(double min, double max, double step) {
        this.min = min;
        this.max = max;
        this.step = step;
        size = (int)((max-min)/step);
        values = new String[size];
    }

    public void fillStringArray()
    {
        double value = min;
        for(int i=0; i<size; i++)
        {
            values[i] = ""+((float)(value+(i*step)));
        }
    }

    public String[] getStringArray()
    {
        return values;
    }

}
