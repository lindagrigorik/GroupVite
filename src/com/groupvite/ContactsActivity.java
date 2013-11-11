package com.groupvite;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.parse.ParseObject;
import com.groupvite.models.Event;
import com.groupvite.models.User;
import com.groupvite.util.ParseClient;


public class ContactsActivity extends Activity {   
	private static final int PICK_FRIENDS_ACTIVITY = 1;
	protected static final String PARSE = "Parse";
	private Button pickFriendsButton;
	private UiLifecycleHelper lifecycleHelper;
	boolean pickFriendsWhenSessionOpened;
	EditText etMessage;
	private ListView lvContacts;
	private ContactsAdapter contactsAdapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_contacts);
    	
    	lvContacts = (ListView) findViewById(R.id.lvContacts);
    	contactsAdapter = new ContactsAdapter(getApplicationContext(), new ArrayList<User>());
    	lvContacts.setAdapter(contactsAdapter);
    	
    	etMessage=(EditText)findViewById(R.id.etMessage);
    	GroupViteApp application = (GroupViteApp) getApplication();
    	
//    	if (application.getCurrentEvent().getMessageBody()!=null && !application.getCurrentEvent().getMessageBody().equals("")){
//    	    etMessage.setText(application.getCurrentEvent().getMessageBody());
//    	}
    	pickFriendsButton = (Button) findViewById(R.id.btnAddContacts);
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
        GroupViteApp application = (GroupViteApp) getApplication();
       
        Collection<GraphUser> selection = application.getSelectedUsers();
        ArrayList<User> users = new ArrayList<User>();
        
        ArrayList<User> invitedUsers = (ArrayList<User>) application.getCurrentEvent().getInvitedUsers(); 
        if (invitedUsers!= null && invitedUsers.size()>0){
            users.addAll(invitedUsers);
        }
        if (selection != null && selection.size() > 0) {
            for (GraphUser user : selection) {
                users.add(User.fromGraphUser(user));
                Log.d("PARSE", "ID : " + user.getId());
            }
        }
        
        contactsAdapter.clear();
        contactsAdapter.addAll(users);
        
        // propagate to the event
        Event e = (Event) getIntent().getSerializableExtra("event");
//        Event e = ((GroupViteApp) getApplication()).getCurrentEvent();
        if (e == null) {
        	Log.d("PARSE", " EVENT IS NULL. ");
        	return;
        }
        
        e.setInvitedUsers(users);
        ((GroupViteApp) getApplication()).setCurrentEvent(e);
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
		ParseClient.createEvent(((GroupViteApp) getApplication()).getCurrentEvent());
		
		Intent intent = new Intent(this, EventsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		return true;
    }
}
