package com.groupvite;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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
import com.groupvite.fragments.EventsListFragment;
import com.groupvite.models.Event;
import com.groupvite.models.User;
import com.groupvite.util.ParseClient;

public class EventsActivity extends FragmentActivity {

	private ListView lvEvents;
	private EventsAdapter eventsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);

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
								
								FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
								EventsListFragment fragment = EventsListFragment.newInstance(user);
								ft.replace(R.id.flEvents, fragment);
								ft.commit();
								
								//FIXME: WE SHOULD CHANGE THIS GLOBAL VARIABLE USAGE!!!
								((GroupViteApp) getApplication()).setCurrentUser(user);
								
							}
						}
					}).executeAsync();
				} else {
					Log.d("GROUPVITE", "session unopened");
				}
			}
		});

	}


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
