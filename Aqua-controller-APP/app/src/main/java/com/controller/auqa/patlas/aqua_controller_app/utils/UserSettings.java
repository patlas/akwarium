package com.controller.auqa.patlas.aqua_controller_app.utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by PatLas on 15.02.2016.
 */
public class UserSettings {
    Map<String, WeakReference<Object>> data = new HashMap<String, WeakReference<Object>>();

    public void save(String id, Object obj) {
        data.put(id, new WeakReference<Object>(obj));
    }

    public Object get(String id) {
        WeakReference<Object> ref = data.get(id);
        if(ref != null)
            return ref.get();
        else
            return null;
    }

    private static final UserSettings settings = new UserSettings();
    public static UserSettings getInstance() { return settings;}

}
