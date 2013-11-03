package com.groupvite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.groupvite.models.Event;
import com.groupvite.models.User;
import com.groupvite.util.Operation;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

@SuppressLint("SimpleDateFormat")
public class CalendarActivity extends FragmentActivity {
	protected static final String TAG = "GroupVite";
	// CalendarPickerView calendar;
	private CaldroidFragment caldroidFragment;
	EditText eventTitle;
	Event event;
	// final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
	final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	ArrayList<Date> alreadySelectedDates = new ArrayList<Date>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);

		caldroidFragment = new CaldroidFragment();

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String username = pref.getString("name", "n/a");
		Log.i(TAG, "username is what now? " + username);

		// if (savedInstanceState != null) {
		// caldroidFragment.restoreStatesFromKey(savedInstanceState,
		// "CALDROID_SAVED_STATE");
		// }
		// If activity is created from fresh
		// else {

		// * Determine whether you came here because 1) clicked "add" sign (host) 2)
		// * clicked activity to respond to host's invite (invitee) 3) clicked on
		// * event to edit activity (both host + invitee)

		Intent i = getIntent();
		String operation = i.getStringExtra("operation");
		if (operation == null) {
			Log.e(TAG, "Remember to pass in the operation!!");
			Log.i(TAG, "Defaulting to whatever we want for testing");
			 operation = "ADD_NEW_EVENT";
//			operation = "EDIT_CREATED_EVENT";
		}
		switch (Operation.valueOf(operation)) {
		case ADD_NEW_EVENT:
			// have to create a new event
			createNewEvent();
			break;
		case RESPOND_TO_INVITE:
			// get from database, the event details
			// show list of preselected dates that you can choose to
			break;
		case EDIT_CREATED_EVENT:
			// get from database, the event details
			// show calendar
			editCreatedEvent();
			break;
		case EDIT_RESPONDED_EVENT:
			// get from database, the event details, show calendar with preselected
			// dates
			break;

		default:
			Log.e(TAG, "Some unknown type we switched on");
			break;
		}

	}

	private void editCreatedEvent() {
		Log.i(TAG,"IN editCreatedEvent");
		Event event = stubEvent();
		add_or_update_event(event);
	
	}

	private Event stubEvent() {
		// create a fake event here

		Event e = new Event();
		e.setEventTitle("abc");
		e.setHost("Neha");
		ArrayList<Date> dates = new ArrayList<Date>();
		try {
			Calendar cal = Calendar.getInstance();
			dates.add(formatter.parse(formatter.format(cal.getTime())));
			cal.add(Calendar.DATE, 2);
			dates.add(formatter.parse(formatter.format(cal.getTime())));
			cal.add(Calendar.DATE, 3);
			dates.add(formatter.parse(formatter.format(cal.getTime())));
			cal.add(Calendar.DATE, 4);
			dates.add(formatter.parse(formatter.format(cal.getTime())));
		} catch (ParseException e1) {
			Log.e(TAG, "Whoops, problem parsing dates");
			e1.printStackTrace();
		}

		e.setHostSelectedDates(dates);

		return e;
	}

	private void createNewEvent() {
		Log.i(TAG,"In createNewEvent");
		add_or_update_event(null);
	}

	public void add_or_update_event(Event event) {
		Bundle args = new Bundle();
		Calendar cal = Calendar.getInstance();
		args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
		args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
		args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
		args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
		args.putString(CaldroidFragment.MIN_DATE, formatter.format(cal.getTime()));
		args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, false);
		caldroidFragment.setArguments(args);

		if (event != null) {
			this.alreadySelectedDates = event.getHostSelectedDates();

			for (int i = 0; i < event.getHostSelectedDates().size(); i++) {
				caldroidFragment.setBackgroundResourceForDate(R.color.blue, event
						.getHostSelectedDates().get(i));
				caldroidFragment.setTextColorForDate(R.color.white, event
						.getHostSelectedDates().get(i));

			}
			// setting event title
			EditText etEventTitle = (EditText) findViewById(R.id.etEventTitle);
			etEventTitle.setText(event.getEventTitle());
		}

		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.commit();

		// Setup listener
		final CaldroidListener listener = new CaldroidListener() {

			@Override
			public void onSelectDate(Date date, View view) {
				Toast.makeText(getApplicationContext(),
						"here we are selecting: " + formatter.format(date),
						Toast.LENGTH_SHORT).show();

				if (caldroidFragment != null) {
					Log.i(TAG, "whats in already selected dates: " + alreadySelectedDates);
					Log.i(TAG, "what's date: " + date);
					if (alreadySelectedDates.contains(date)) {
						// then we have to unset the selection
						caldroidFragment.setBackgroundResourceForDate(R.color.white, date);
						caldroidFragment.setTextColorForDate(R.color.black, date);
						alreadySelectedDates.remove(date);
					} else {
						// we have to set the selection
						caldroidFragment.setBackgroundResourceForDate(R.color.blue, date);
						caldroidFragment.setTextColorForDate(R.color.white, date);
						alreadySelectedDates.add(date);
					}
					caldroidFragment.refreshView();
					Log.i(TAG, "already selected dates: " + alreadySelectedDates);
				}

			}

			@Override
			public void onChangeMonth(int month, int year) {
				String text = "month: " + month + " year: " + year;
				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onLongClickDate(Date date, View view) {
				Toast.makeText(getApplicationContext(),
						"Long click " + formatter.format(date), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCaldroidViewCreated() {
				if (caldroidFragment.getLeftArrowButton() != null) {
					Toast.makeText(getApplicationContext(), "Caldroid view is created",
							Toast.LENGTH_SHORT).show();
				}
			}

		};

		// Setup Caldroid
		caldroidFragment.setCaldroidListener(listener);
		saveHostedEventDetails();
	}

	public void saveHostedEventDetails() {
		Button done = (Button) findViewById(R.id.done_button);
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// created a new event, so save the current user as host, and event
				// details and selected dates and call new activity
				Event event = new Event();
				event.setHostSelectedDates(new ArrayList<Date>(alreadySelectedDates));
				Log.i(TAG, event.getHostSelectedDates().toString());
				EditText eventTitle = (EditText) findViewById(R.id.etEventTitle);
				if (eventTitle.getText() == null
						|| eventTitle.getText().toString().isEmpty()) {
					Toast.makeText(CalendarActivity.this,
							"You need to add an event name!", Toast.LENGTH_SHORT).show();
					return;
				}
				event.setEventTitle(eventTitle.getText().toString());
				User user = User.getCurUser();

				event.setHost(user.getName());
				event.save();

				// find out if event was saved

				Event e = new Select().from(Event.class)
						.where("EventTitle = ?", event.getEventTitle()).executeSingle();

				Log.i(TAG, "what's event, you better save: " + e);

				if (user.getEvents() == null) {
					ArrayList<Event> events = new ArrayList<Event>();
					user.setEvents(events);
				}
				user.save();

				// save to sql

				user.getEvents().add(event);
				user.save();

				Intent i = new Intent(CalendarActivity.this, ContactsActivity.class);
				i.putExtra("event", event);
				startActivity(i);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calendar, menu);
		return true;
	}

	public void onFinish(View v) {
		Intent i = new Intent(this, ContactsActivity.class);
		startActivity(i);
	}
}
