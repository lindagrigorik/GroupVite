package com.groupvite;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.roomorama.caldroid.CalendarHelper;

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
		// if (savedInstanceState != null) {
		// caldroidFragment.restoreStatesFromKey(savedInstanceState,
		// "CALDROID_SAVED_STATE");
		// }
		// If activity is created from fresh
		// else {

		/*
		 * Determine whether you came here because 1) clicked "add" sign (host) 2)
		 * clicked activity to respond to host's invite (invitee) 3) clicked on
		 * event to edit activity (both host + invitee)
		 */
		Intent i = getIntent();
		String operation = i.getStringExtra("operation");
		if (operation == null) {
			Log.e(TAG, "Remember to pass in the operation!!");
			Log.i(TAG, "Defaulting to whatever we want for testing");
			operation = "ADD_NEW_EVENT";
			// operation = "EDIT_CREATED_EVENT" ;
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
			// default

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

		// String eventToEdit = getIntent().getStringExtra("eventToEdit");
		String eventToEdit = "abc";
		Log.i(TAG, "We're trying to edit an event here: " + eventToEdit);
		Event event = new Select().from(Event.class)
				.where("EventTitle = ?", eventToEdit).executeSingle();
		Log.i(TAG, "Show me event: " + event);
		// Log.i(TAG, "eventTitle")
		if (event == null) {
			Toast.makeText(CalendarActivity.this, "whoops, didn't get event",
					Toast.LENGTH_SHORT);
			return;
		}
		Bundle args = new Bundle();
		Calendar cal = Calendar.getInstance();
		args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);

		args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
		args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);

		args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
		args.putString(CaldroidFragment.MIN_DATE, formatter.format(cal.getTime()));
		args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, false);
		ArrayList<DateTime> alreadySelectedDateTimes = new ArrayList<DateTime>();
		for (int i = 0; i < alreadySelectedDates.size(); i++) {
			alreadySelectedDateTimes.add(CalendarHelper
					.convertDateToDateTime(alreadySelectedDates.get(i)));
		}

		args.putStringArrayList(CaldroidFragment.SELECTED_DATES,
				CalendarHelper.convertToStringList(alreadySelectedDateTimes));
		//

		caldroidFragment.setArguments(args);
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.commit();

		final CaldroidListener listener = new CaldroidListener() {

			@Override
			public void onSelectDate(Date date, View view) {
				Toast.makeText(getApplicationContext(),
						"here we are selecting: " + formatter.format(date),
						Toast.LENGTH_SHORT).show();

				if (caldroidFragment != null) {
					Log.i(TAG, "already selected dates: " + alreadySelectedDates);
					if (alreadySelectedDates.contains(date)) {
						// then we have to unset the selection
						caldroidFragment.setBackgroundResourceForDate(R.color.white, date);
						caldroidFragment.setTextColorForDate(R.color.black, date);
						alreadySelectedDates.remove(date);
					} else { // we have to set the selection
						caldroidFragment.setBackgroundResourceForDate(R.color.blue, date);
						caldroidFragment.setTextColorForDate(R.color.white, date);
						alreadySelectedDates.add(date);
					}
					caldroidFragment.refreshView();
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

		// createNewEvent();

	}

	private void createNewEvent() {
		Bundle args = new Bundle();
		Calendar cal = Calendar.getInstance();
		args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
		args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
		args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
		args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
		args.putString(CaldroidFragment.MIN_DATE, formatter.format(cal.getTime()));
		args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, false);

		caldroidFragment.setArguments(args);

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
		Button done = (Button) findViewById(R.id.done_button);
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// created a new event, so save the current user as host, and event
				// details and selected dates and call new activity
				Event event = new Event();
				event.setDays(new ArrayList<Date>(alreadySelectedDates));
				EditText eventTitle = (EditText) findViewById(R.id.etEventTitle);
				if (eventTitle.getText() == null
						|| eventTitle.getText().toString().isEmpty()) {
					Toast.makeText(CalendarActivity.this,
							"You need to add an event name!", Toast.LENGTH_SHORT).show();
					return;
				}
				event.setEventTitle(eventTitle.getText().toString());
				User user = User.getCurUser();

				event.setHost(user);
				if (user.getEvents() == null) {
					ArrayList<Event> events = new ArrayList<Event>();
					user.setEvents(events);
				}
				user.save();

				for (int i = 0; i < event.getDays().size(); i++) {
					event.getDays().get(i).save();
				}

				// save to sql
				event.save();
				if (user.getEvents() == null) {
					ArrayList<Event> events = new ArrayList<Event>();
					events.add(event);
					user.setEvents(events);

				} else {
					user.getEvents().add(event);
					// user.setEvents();
				}

				user.save();
				Intent i = new Intent(CalendarActivity.this, MainActivity.class);
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
