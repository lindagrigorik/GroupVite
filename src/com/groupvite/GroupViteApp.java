package com.groupvite;

import java.util.Collection;

import android.app.Application;
import android.content.Context;

import com.facebook.model.GraphUser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class GroupViteApp extends Application {
	private static Context context;
    private Collection<GraphUser> selectedUsers;

	
    @Override
    public void onCreate() {
        super.onCreate();
        GroupViteApp.context = this;
        

        //Parse.initialize(this, "5znRdifSaJ7N0QhFSXwbsKQspLiTD7QAGe7SEzb3", "4NJsZ3uQ8ow2w6Oxb2LQmdgu6pDXgc0kE9WnRbfo");
        Parse.initialize(this, "9jpzUMr1kXN9qO2dAZEPCRQbdbggxt6tK1MQpYaw", "7eJJgONOyB1DUoeyKBxyZDuzdmTEGp1dMLxl3uJV"); 

        PushService.setDefaultPushCallback(this, EventsActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        
        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
        		cacheInMemory().cacheOnDisc().build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .defaultDisplayImageOptions(defaultOptions)
            .build();
        ImageLoader.getInstance().init(config);
    }
    
    public Collection<GraphUser> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(Collection<GraphUser> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }
}