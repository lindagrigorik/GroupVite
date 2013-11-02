package com.groupvite;

import android.content.Context;

import com.activeandroid.ActiveAndroid;

public class GroupViteApp extends com.activeandroid.app.Application {
	private static Context context;
	
    @Override
    public void onCreate() {
        super.onCreate();
        GroupViteApp.context = this;
    }
}