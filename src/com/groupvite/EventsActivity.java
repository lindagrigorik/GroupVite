package com.groupvite;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.groupvite.models.User;
import com.groupvite.util.ParseClient;


public class EventsActivity extends Activity {

 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		
		/*ParseFacebookUtils.logIn(this, new LogInCallback() {
		    @Override
                    public void done(ParseUser user, ParseException arg1) {
	                // TODO Auto-generated method stub
			if (user != null) {
			   getFacebookIdInBackground();
			}
                    }
		  });*/
		
		  
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
								
								((GroupViteApp) getApplication()).setCurrentUser(user);
								
								Toast.makeText(getApplicationContext(),
										"Welcome " + user.getName() +  "!", Toast.LENGTH_SHORT).show();
							}
						}
					}).executeAsync();
				}
			}
		});
		
		/*Intent i = getIntent();
		Event event = (Event) i.getSerializableExtra("event");
		 String eventTitle = i.getStringExtra("eventTitle");
		 ArrayList<Date> selectedDates = ((ArrayList<Date>)
		 i.getSerializableExtra("selectedDates"));
		Toast.makeText(getApplicationContext(),
				"event title is: " + event.getEventTitle(), Toast.LENGTH_SHORT).show();
		Toast.makeText(getApplicationContext(),
				"selected dates are: " + event.getDays(), Toast.LENGTH_SHORT).show();
		*/
	}
	
	/*private static void getFacebookIdInBackground() {
	    Request.executeMeRequestAsync(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
	      @Override
	      public void onCompleted(GraphUser user, Response response) {
	        if (user != null) {
	          ParseUser.getCurrentUser().put("fbId", user.getId());
	          ParseUser.getCurrentUser().saveInBackground();
	        }
	      }
	    });
	  }*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.events, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menu) {
	    Intent i = new Intent(this, CalendarActivity.class);
	    startActivity(i);
	    return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		//ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

}
