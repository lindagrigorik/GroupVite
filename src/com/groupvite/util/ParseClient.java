package com.groupvite.util;

import java.util.List;

import android.util.Log;

import com.groupvite.models.Event;
import com.groupvite.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class ParseClient {
	private static List<ParseObject> recentEvents;
	
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
	
	public static void createEvent(Event event) {
		if (event == null) {
			Log.d("SUBHA", "parse client. event is null");
			return;
		}
		ParseObject parseEvent = new ParseObject("EventObject");
		parseEvent.put("EventTitle", event.getEventTitle());
		parseEvent.put("HostId", event.getHost().getParseId());
		parseEvent.addAll("InvitedUsers", event.getInvitedUsersParseIds());
		
		//hack...
		final int index = recentEvents.size();
		recentEvents.add(parseEvent);
		
		parseEvent.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					updateEvent(index);
				} else {
					Log.d("SUBHA", "something went wrong with save event in background");
				}
			}
		});
	}
	
	private static void updateEvent(int index) {
		recentEvents.get(index).fetchInBackground(new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					// update each user invited user with the event ID
					final String eventParseId = object.getObjectId();
					List<String> invitedUsersIds = object.getList("InvitedUsers");
					String hostUserId = object.getString("HostId");
					
					// update the host user
					ParseQuery<ParseObject> hostQuery = ParseQuery.getQuery("UserObject");
					// Retrieve the object by id
					hostQuery.getInBackground(hostUserId, new GetCallback<ParseObject>() {
					  public void done(ParseObject userObject, ParseException e) {
					    if (e == null) {
					      // Now let's add the event to this user's hosted list
					      userObject.add("HostedEvent", eventParseId);
					      userObject.saveInBackground();
					    }
					  }
					});
					
					for (String invitedUserId : invitedUsersIds) {						
						// update the invited user
						ParseQuery<ParseObject> query = ParseQuery.getQuery("UserObject");
						// Retrieve the object by id
						query.getInBackground(invitedUserId, new GetCallback<ParseObject>() {
						  public void done(ParseObject userObject, ParseException e) {
						    if (e == null) {
						      // Now let's add the event to these users' invited list
						      userObject.add("InvitedEvent", eventParseId);
						      userObject.saveInBackground();
						    }
						  }
						});
					}
				} else {
					// failure
				}
			}
		});
	}
}
