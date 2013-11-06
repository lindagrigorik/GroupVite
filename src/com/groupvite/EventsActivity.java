package com.groupvite;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.groupvite.models.Event;
import com.groupvite.models.User;
import com.groupvite.util.ParseClient;

public class EventsActivity extends Activity {

	private ListView lvEvents;
	private EventsAdapter eventsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);

		/*
		 * ParseFacebookUtils.logIn(this, new LogInCallback() {
		 * 
		 * @Override public void done(ParseUser user, ParseException arg1) { // TODO
		 * Auto-generated method stub if (user != null) {
		 * getFacebookIdInBackground(); } } });
		 */

		lvEvents = (ListView) findViewById(R.id.lvEvents);
		eventsAdapter = new EventsAdapter(getApplicationContext(),
				new ArrayList<Event>());
		lvEvents.setAdapter(eventsAdapter);
		lvEvents.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long res) {
				Event e = (Event) adapter.getItemAtPosition(position);
				User currentUser = ((GroupViteApp)getApplication()).getCurrentUser();
				Toast.makeText(EventsActivity.this,
						"Selecting this item here: " + e,
						Toast.LENGTH_SHORT).show();
				Intent i = new Intent(EventsActivity.this,CalendarActivity.class);
				i.putExtra("event",e);
				i.putExtra("currentUser", currentUser);

				if(e.getHost().getParseId().equalsIgnoreCase(currentUser.getParseId())){
					//it's the host's own event that he/she is trying to edit
					i.putExtra("operation", "EDIT_CREATED_EVENT");
				}else{
					i.putExtra("operation", "RESPOND_TO_INVITE");
				}
				
				startActivity(i);
			}
		});

		// start Facebook login
		Session.openActiveSession(this, true, new Session.StatusCallback() {
			// callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					Request.newMeRequest(session, new Request.GraphUserCallback() {
						// callback after Graph API response with user object
						@Override
						public void onCompleted(GraphUser graphUser, Response response) {
							if (graphUser != null) {
								User user = User.fromGraphUser(graphUser);
								ParseClient.populateUser(user);
								ParseClient.createParseUser(user);
								ArrayList<Event> events = ParseClient.getUserEventsList(user);
								((GroupViteApp) getApplication()).setCurrentUser(user);

								if (events != null ) eventsAdapter.addAll(events);

								Toast.makeText(getApplicationContext(),
										"Welcome " + user.getName() + "!", Toast.LENGTH_SHORT)
										.show();
							}
						}
					}).executeAsync();
				} else {
					Log.d("SUBHA", "session unopened");
				}
			}
		});

		/*
		 * Intent i = getIntent(); Event event = (Event)
		 * i.getSerializableExtra("event"); String eventTitle =
		 * i.getStringExtra("eventTitle"); ArrayList<Date> selectedDates =
		 * ((ArrayList<Date>) i.getSerializableExtra("selectedDates"));
		 * Toast.makeText(getApplicationContext(), "event title is: " +
		 * event.getEventTitle(), Toast.LENGTH_SHORT).show();
		 * Toast.makeText(getApplicationContext(), "selected dates are: " +
		 * event.getDays(), Toast.LENGTH_SHORT).show();
		 */
	}

	/*
	 * private static void getFacebookIdInBackground() {
	 * Request.executeMeRequestAsync(ParseFacebookUtils.getSession(), new
	 * Request.GraphUserCallback() {
	 * 
	 * @Override public void onCompleted(GraphUser user, Response response) { if
	 * (user != null) { ParseUser.getCurrentUser().put("fbId", user.getId());
	 * ParseUser.getCurrentUser().saveInBackground(); } } }); }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.events, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menu) {
		Intent i = new Intent(this, CalendarActivity.class);
		i.putExtra("operation", "ADD_NEW_EVENT");
		User curUser = ((GroupViteApp)getApplication()).getCurrentUser();
		i.putExtra("currentUser", curUser);
		startActivity(i);
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode,
				data);
		// ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

}
