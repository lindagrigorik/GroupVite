package com.groupvite;

import java.util.Collection;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.model.GraphUser;
import com.groupvite.models.Event;
import com.groupvite.models.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

public class GroupViteApp extends Application {
	private static Context context;
    private Collection<GraphUser> selectedUsers;
    private User currentUser;
    private Event currentEvent;
	
    @Override
    public void onCreate() {
        super.onCreate();
        GroupViteApp.context = this;
        
        Parse.initialize(this, "XjzbnK5X5eBJAFJIm0sXFjM0lPDRJnKAWUQxFpC2", "qppHxLDmGFfF1k4vAsRWbCeJOoJR4ifZvLQ14Ysn"); 
        
        PushService.setDefaultPushCallback(this, EventsActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        
        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
        		cacheInMemory().cacheOnDisc().build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .defaultDisplayImageOptions(defaultOptions)
            .build();
        ImageLoader.getInstance().init(config);
        //this.createTestUsers();
    }
    
    public void createTestUsers(){
	ParseObject UserObject = new ParseObject("UserObj");
	UserObject.put("fb_id", "122611373");
	UserObject.put("name", "Linda Yang");
	try {
	    UserObject.save();
        } catch (ParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
        }
	
	UserObject = new ParseObject("UserObj");
	UserObject.put("fb_id", "821699189");
	UserObject.put("name", "Neha Karajgikar");
	try {
	    UserObject.save();
        } catch (ParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
        }
	
	UserObject = new ParseObject("UserObj");
	UserObject.put("fb_id", "712153");
	UserObject.put("name", "Subha Gollakota");
	try {
	    UserObject.save();
        } catch (ParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
        }
    }
    
    public Collection<GraphUser> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(Collection<GraphUser> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }
    
    public User getCurrentUser() {
    	return this.currentUser;
    }
    
    public Event getCurrentEvent() {
    	return this.currentEvent;
    }
    
    public void setCurrentEvent(Event e) {
    	Log.d("PARSE", "saving current event as :" + e);
    	this.currentEvent = e;
    }
    
    public void setCurrentUser(User user) {
    	this.currentUser = user;
    }
}