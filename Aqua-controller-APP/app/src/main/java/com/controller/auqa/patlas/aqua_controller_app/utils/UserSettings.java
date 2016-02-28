package com.controller.auqa.patlas.aqua_controller_app.utils;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

/**
 * Created by PatLas on 15.02.2016.
 */
public class UserSettings {
//    Map<String, WeakReference<Object>> data = new Hashtable<>();
//
//    public void save(String id, Object obj) {
//        data.put(id, new WeakReference<Object>(obj));
//    }
//
//    public Object get(String id) {
//        WeakReference<Object> ref = data.get(id);
//        Log.w("GET DATA", "ID: "+id+" value: "+ref.get());
//        if(ref != null)
//            return ref.get();
//        else
//            Log.w("GET DATA", "null reference!!!");
//            return null;
//    }
//
//    private static final UserSettings settings = new UserSettings();
//    public static UserSettings getInstance() { return settings;}

    HashMap<String, Object> data = new HashMap<>();
    public void save(String id, Object obj) {
        data.put(id, obj);
    }

    public Object get(String id) {
        return data.get(id);
    }

    private static final UserSettings settings = new UserSettings();
    public static UserSettings getInstance() { return settings;}
}
