package com.groupvite.util;

import android.util.Log;

import com.groupvite.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseClient {
	
	// currently this does nothing but eventually it should populate the user's "events"
	public static void populateUser(User user) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("UserObject");
		query.getInBackground(user.getParseId(), new GetCallback<ParseObject>() {
		  public void done(ParseObject object, ParseException e) {
		    if (e == null) {
		      Log.d("SUBHA", "name" + object.getString("name"));
		      Log.d("SUBHA", "fbid" + object.getString("fbid"));
		    } else {
		      // something went wrong
		    	Log.d("SUBHA", "something has gone wrong");
		    	Log.d("SUBHA", e.toString());
		    }
		  }
		});
	}
}
