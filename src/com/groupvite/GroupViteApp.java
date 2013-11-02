package com.groupvite;

import java.util.Collection;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.facebook.model.GraphUser;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class GroupViteApp extends com.activeandroid.app.Application {
	private static Context context;
    private Collection<GraphUser> selectedUsers;

	
    @Override
    public void onCreate() {
        super.onCreate();
        GroupViteApp.context = this;
        
        Parse.initialize(this, "5znRdifSaJ7N0QhFSXwbsKQspLiTD7QAGe7SEzb3", "4NJsZ3uQ8ow2w6Oxb2LQmdgu6pDXgc0kE9WnRbfo"); 
    }
    
    public Collection<GraphUser> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(Collection<GraphUser> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }
}