package com.dare;

import com.dare.db.DareDbHelper;

import android.app.Application;

public class DareApplication extends Application {

    @Override
    public void onCreate() {
    	super.onCreate();
        new DareDbHelper(this);        
    }   
        
}
