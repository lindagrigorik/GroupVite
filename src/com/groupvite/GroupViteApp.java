package com.groupvite;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class GroupViteApp extends com.activeandroid.app.Application {
	private static Context context;
	
    @Override
    public void onCreate() {
        super.onCreate();
        GroupViteApp.context = this;
        
        Parse.initialize(this, "5znRdifSaJ7N0QhFSXwbsKQspLiTD7QAGe7SEzb3", "4NJsZ3uQ8ow2w6Oxb2LQmdgu6pDXgc0kE9WnRbfo");
        
        PushService.setDefaultPushCallback(this, EventsActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}