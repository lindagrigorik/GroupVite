package com.groupvite;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.parse.ParseObject;

public class ContactsActivity extends Activity {   
	private static final int PICK_FRIENDS_ACTIVITY = 1;
	private Button pickFriendsButton;
	private UiLifecycleHelper lifecycleHelper;
	boolean pickFriendsWhenSessionOpened;
	private TextView resultsTextView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_contacts);
    	
    	pickFriendsButton = (Button) findViewById(R.id.btnAddContacts);
    	resultsTextView = (TextView) findViewById(R.id.resultsTextView);
    	pickFriendsButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			onClickPickFriends();
    		}
    	});
    	
        lifecycleHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChanged(session, state, exception);
            }
        });
        lifecycleHelper.onCreate(savedInstanceState);

        ensureOpenSession();
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_FRIENDS_ACTIVITY:
                displaySelectedFriends(resultCode);
                break;
            default:
                Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
                break;
        }
    }

    private boolean ensureOpenSession() {
        if (Session.getActiveSession() == null ||
                !Session.getActiveSession().isOpened()) {
            Session.openActiveSession(this, true, new Session.StatusCallback() {
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    onSessionStateChanged(session, state, exception);
                }
            });
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.contacts, menu);	
    	return true;
    }
    
    private void onSessionStateChanged(Session session, SessionState state, Exception exception) {
        if (pickFriendsWhenSessionOpened && state.isOpened()) {
            pickFriendsWhenSessionOpened = false;

            startPickFriendsActivity();
        }
    }

    private void displaySelectedFriends(int resultCode) {
        String results = "";
        GroupViteApp application = (GroupViteApp) getApplication();

        Collection<GraphUser> selection = application.getSelectedUsers();
        if (selection != null && selection.size() > 0) {
            ArrayList<String> names = new ArrayList<String>();
            for (GraphUser user : selection) {
                names.add(user.getName());
            }
            results = TextUtils.join(", ", names);
        } else {
            results = "<No friends selected>";
        }

        resultsTextView.setText(results);
    }

    private void onClickPickFriends() {
        startPickFriendsActivity();
    }

    private void startPickFriendsActivity() {
        if (ensureOpenSession()) {
            GroupViteApp application = (GroupViteApp) getApplication();
            application.setSelectedUsers(null);

            Intent intent = new Intent(this, PickFriendsActivity.class);
            startActivityForResult(intent, PICK_FRIENDS_ACTIVITY);
        } else {
            pickFriendsWhenSessionOpened = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
	Toast.makeText(this.getBaseContext(), "SEND", Toast.LENGTH_SHORT).show();
	ParseObject contacts = new ParseObject("contacts");
	
	return false;
    }
}
