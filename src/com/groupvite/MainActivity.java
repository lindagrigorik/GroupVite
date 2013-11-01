package com.groupvite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.groupvite.models.Event;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent i = getIntent();
		Event event = (Event) i.getSerializableExtra("event");
		// String eventTitle = i.getStringExtra("eventTitle");
		// ArrayList<Date> selectedDates = ((ArrayList<Date>)
		// i.getSerializableExtra("selectedDates"));
		Toast.makeText(getApplicationContext(),
				"event title is: " + event.getEventTitle(), Toast.LENGTH_SHORT).show();
		Toast.makeText(getApplicationContext(),
				"selected dates are: " + event.getDays(), Toast.LENGTH_SHORT).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
