package com.groupvite.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.groupvite.models.Event;
import com.groupvite.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class ParseClient {

    private static final ExecutorService parseExecutor = Executors.newSingleThreadExecutor();

    private static List<ParseObject> recentEvents = new ArrayList<ParseObject>();

    // currently this does nothing but eventually it should populate the user's
    // "events"
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

    public static void createEvent(final Event event) {
	if (event == null) {
	    Log.d("SUBHA", "parse client. event is null");
	    return;
	}

	parseExecutor.submit(new Runnable() {
	    @Override
	    public void run() {
		ParseObject parseEvent = new ParseObject("EventObject");
		parseEvent.put("EventTitle", event.getEventTitle());
		parseEvent.put("HostId", event.getHost().getParseId());
		Collection<String> userIds = ensureUsersExist(event.getInvitedUsers());
		parseEvent.addAll("InvitedUsers", userIds);

		// hack...
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
	});
    }

    /**
     * Ensures there is an entry for each user in Parse. If the records do not
     * exist, insert and get the id(s). If they exist, retrieve and return their
     * ids.
     * 
     * @param users
     * @return a {@link Collection} of ids from parse
     */
    protected static Collection<String> ensureUsersExist(List<User> users) {
	Collection<String> ids = new LinkedList<String>();
	Map<String, User> existingUsers;
	try {
	    existingUsers = getExistingUsers();
	} catch (ParseException e1) {
	    Log.e("GroupVite", "Error getting existing users", e1);
	    existingUsers = new HashMap<String, User>();
	}
	for (User user : users) {
	    String fbId = user.getFacebookId();
	    if (existingUsers.containsKey(fbId)) {
		ids.add(existingUsers.get(fbId).getParseId());
	    } else {
		// add user and then get its parse id, whcih is added to return
		// list
		ParseObject userObject = new ParseObject("UserObject");
		userObject.put("fbId", user.getFacebookId());
		userObject.put("name", user.getName());
		try {
		    // want to SAVE now, not in background, since we want to
		    // wait
		    // until we have the id
		    userObject.save();
		    String objectId = userObject.getObjectId();
		    ids.add(objectId);
		    Log.d("objectid", "Object id is " + objectId);
		} catch (ParseException e) {
		    Log.e("GroupVita", "Error creating user object", e);
		}
	    }
	}
	return ids;
    }

    private static Map<String, User> getExistingUsers() throws ParseException {
	ParseQuery<ParseObject> query = ParseQuery.getQuery("UserObject");
	List<ParseObject> existingUsers = query.find();
	Map<String, User> userMap = new HashMap<String, User>();
	for (ParseObject existing : existingUsers) {
	    User currentUser = new User();
	    currentUser.setFacebookId(existing.getString("fbId"));
	    currentUser.setParseId(existing.getObjectId());
	    userMap.put(currentUser.getFacebookId(), currentUser);
	}
	return userMap;
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
				// Now let's add the event to this user's hosted
				// list
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
				    // Now let's add the event to these users'
				    // invited list
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
