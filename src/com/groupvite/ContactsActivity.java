package com.groupvite;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ContactsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_contacts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.contacts, menu);	
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
	Toast.makeText(this.getBaseContext(), "SEND", Toast.LENGTH_SHORT).show();
	return false;
    }
}
