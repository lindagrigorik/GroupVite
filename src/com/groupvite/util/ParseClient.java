package com.groupvite.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.groupvite.models.Event;
import com.groupvite.models.Response;
import com.groupvite.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseClient {

    private static final ExecutorService parseExecutor = Executors.newSingleThreadExecutor();

    private static List<ParseObject> recentEvents = new ArrayList<ParseObject>();
    protected static final String PARSE = "Parse";
    private static final String PARSEUSER = "UserObj";
    private static final String PARSEEVENT = "EventObj";
    
    // currently this does nothing but eventually it should populate the user's
    // "events"
    public static void populateUser(User user) {
	ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSEUSER);
	query.getInBackground(user.getParseId(), new GetCallback<ParseObject>() {
	    public void done(ParseObject object, ParseException e) {
		if (e == null) {
		    Log.d(PARSE, "name" + object.getString("name"));
		    Log.d(PARSE, "fbid" + object.getString("fbid"));
		} else {
		    // something went wrong
		    Log.d(PARSE, "something has gone wrong");
		    Log.d(PARSE, e.toString());
		}
	    }
	});
    }

    //set parse id of existing user.
    public static String createParseUser(User user){
	ArrayList<User> currUser= new ArrayList<User>();
	currUser.add(user);
	LinkedList<String> id = (LinkedList<String>) ParseClient.ensureUsersExist(currUser);
	user.setParseId(id.get(0));
	return id.get(0);
    }

    public static void createEvent(final Event event) {
	if (event == null) {
	    Log.d(PARSE, "parse client. event is null");
	    return;
	}

	parseExecutor.submit(new Runnable() {
	    @Override
	    public void run() {
		ParseObject parseEvent = new ParseObject(PARSEEVENT);
		parseEvent.put("event_title", event.getEventTitle());
		parseEvent.put("host_id", event.getHost().getParseId());
		for (Date date : event.getHostSelectedDates()) {
			parseEvent.add("host_selected_dates", date.getTime());
		}
		Collection<String> userIds = ensureUsersExist(event.getInvitedUsers());
		parseEvent.addAll("invited_users", userIds);

		// hack...
		final int index = recentEvents.size();
		recentEvents.add(parseEvent);
		
		try {
			parseEvent.save();
		} catch (ParseException e) {
			Log.d(PARSE, "Saving event failed." + e.getMessage());
		}
		updateEvent(index);
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
		ParseObject userObject = new ParseObject(PARSEUSER);
		userObject.put("fb_id", user.getFacebookId());
		userObject.put("name", user.getName());
		userObject.put("picture_url", user.getPicUrl());
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
	ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSEUSER);
	List<ParseObject> existingUsers = query.find();
	Map<String, User> userMap = new HashMap<String, User>();
	for (ParseObject existing : existingUsers) {
	    User currentUser = new User();
	    currentUser.setFacebookId(existing.getString("fb_id"));
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
		    List<String> invitedUsersIds = object.getList("invited_users");
		    String hostUserId = object.getString("host_id");

		    // update the host user
		    ParseQuery<ParseObject> hostQuery = ParseQuery.getQuery(PARSEUSER);
		    // Retrieve the object by id
		    hostQuery.getInBackground(hostUserId, new GetCallback<ParseObject>() {
			public void done(ParseObject userObject, ParseException e) {
			    if (e == null) {
				// Now let's add the event to this user's hosted
				// list
				userObject.add("Hosted_event", eventParseId);
				userObject.saveInBackground();
			    }
			}
		    });

		    for (String invitedUserId : invitedUsersIds) {
			// update the invited user
			ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSEUSER);
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

    //populate user's array list of EVENT object.
    public static ArrayList<Event> getUserEventsList(User user) {
    	ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSEUSER);
    	//Map<String, ArrayList<Event>> events = new HashMap<String, ArrayList<Event>>();
    	ArrayList<Event> events = new ArrayList<Event>();
    	ParseObject userObject;
    	try {
    		userObject = query.get(user.getParseId());
    		List<String> hostedEventIds = userObject.getList("Hosted_event");
    		if (hostedEventIds != null) {
    			for (String eventId : hostedEventIds){
    				Event event = new Event();
    				query = ParseQuery.getQuery(PARSEEVENT);
    				ParseObject eventObject = query.get(eventId);
    				event.setEventTitle(eventObject.getString("event_title"));
    				event.setHost(user);
    				List<String> inviteeIds = eventObject.getList("invited_users");
    				ArrayList<User> inviteeUsers = createUsers(inviteeIds); //create list of inviteUsers
    				event.setInvitedUsers(inviteeUsers);
    				List<Object> selectedDates = eventObject.getList("host_selected_dates");
    				if (selectedDates != null && selectedDates.size() > 0){
    					ArrayList<Date> hostSelectedDates = new ArrayList<Date>();
    					for (Object date : selectedDates){
    						hostSelectedDates.add(new Date(Long.parseLong(date.toString())));
    					}
    					event.setHostSelectedDates(hostSelectedDates);
    				}
    				event.populateInviteeResponseMap(eventObject);
    				events.add(event);
    			}
    		}
    		List<String> invitedEventIds = userObject.getList("InvitedEvent");
    		if (invitedEventIds != null) {
    			for (String invitedEventId : invitedEventIds) {
    				Event event = new Event();
    				query = ParseQuery.getQuery(PARSEEVENT);
    				ParseObject eventObject = query.get(invitedEventId);
    				event.setEventTitle(eventObject.getString("event_title"));
    				String hostParseId = eventObject.getString("host_id");

    				query = ParseQuery.getQuery(PARSEUSER);
    				ParseObject hostObject = query.get(hostParseId);
    				event.setHost(User.fromParseObject(hostObject));
    				List<String> inviteeIds = eventObject.getList("invited_users");
    				ArrayList<User> inviteeUsers = createUsers(inviteeIds); //create list of inviteUsers
    				event.setInvitedUsers(inviteeUsers);
    				List<Object> selectedDates = eventObject.getList("host_selected_dates");
    				if (selectedDates != null && selectedDates.size() > 0){
    					ArrayList<Date> hostSelectedDates = new ArrayList<Date>();
    					for (Object date : selectedDates){
    						hostSelectedDates.add(new Date(Long.parseLong(date.toString())));
    					}
    					event.setHostSelectedDates(hostSelectedDates);
    				}
    				event.setEventParseId(eventObject.getObjectId());
    				event.populateInviteeResponseMap(eventObject);
    				events.add(event);
    			}
    		}

    	} catch (ParseException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	return events;
    }

    
    //Create User object from retrieved Parse Object.
    private static ArrayList<User> createUsers(List<String> ids) throws ParseException {
	ArrayList<User> invitees = new ArrayList<User>();
	ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSEUSER);
	for (String id : ids){
	    ParseObject userObject = query.get(id); 
	    User user = new User();
	    user.setFacebookId(userObject.getString("fb_id"));
	    user.setName(userObject.getString("name"));
	    user.setPicUrl(userObject.getString("picture_url"));
	    user.setParseId(userObject.getObjectId());
	    invitees.add(user);
	}
	return invitees;
	
    }
    
    public static void syncInviteeResponse(Event event, final User user,
    		final HashMap<Date, Response> map) {
    	Log.d("SUBHA", "calling sync invitee response");
    	ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("EventObj");
    	try {
    		ParseObject eventObject = eventQuery.get(event.getEventParseId());
    		// update the invitee response for this user
			// pick out the dates the person said 'yes' to.
			List<Long> yesDates = new ArrayList<Long>();
			for (Date d : map.keySet()) {
				if (map.get(d) == Response.YES) {
					yesDates.add(d.getTime());
				}
			}
			        
			eventObject.addAll(user.getParseId(), yesDates);
			eventObject.save();
    	} catch (ParseException e) {
    		Log.d("SUBHA", "unable to get event " + e.getMessage());
    	}
    }
    
}
