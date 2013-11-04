package com.groupvite;

import hirondelle.date4j.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;

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

import com.groupvite.models.Event;
import com.groupvite.models.InviteeResponse;
import com.groupvite.models.Response;
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
	private Event event;
	// final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
	final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	ArrayList<Date> hostSelectedDates = new ArrayList<Date>();
	ArrayList<Date> inviteeSelectedDates;
	Response response;

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
			// operation = "EDIT_CREATED_EVENT";
			//operation = "RESPOND_TO_INVITE";
		}
		switch (Operation.valueOf(operation)) {
		case ADD_NEW_EVENT:
			// have to create a new event
			createNewEvent();
			break;
		case RESPOND_TO_INVITE:
			// get from database, the event details
			// show list of preselected dates that you can choose to
			respondToInvite();
			break;
		case EDIT_CREATED_EVENT:
			// get from database, the event details
			// show calendar
			editCreatedEvent();
			break;
		case EDIT_RESPONDED_EVENT:
			// get from database, the event details, show calendar with preselected
			// dates
			editRespondedEvent();
			break;

		default:
			Log.e(TAG, "Some unknown type we switched on");
			break;
		}

	}

	private void editRespondedEvent() {
		// TODO Auto-generated method stub

	}

	public ArrayList<Date> getDatesBetweentMinAndMax(Date minDate, Date maxDate,
			ArrayList<Date> selectedDates) {
		ArrayList<Date> dates = new ArrayList<Date>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(minDate);

		while (calendar.getTime().before(maxDate)) {

			Date result = null;
			try {
				result = formatter.parse(formatter.format(calendar.getTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (!selectedDates.contains(result)) {
				dates.add(result);
			}
			calendar.add(Calendar.DATE, 1);
		}

		return dates;
	}

	private void respondToInvite() {
		Log.i(TAG, "In respondToInvite");
		inviteeSelectedDates = new ArrayList<Date>();
		event = stubEvent();
		ArrayList<Date> hostDates = event.getHostSelectedDates();
		Collections.sort(hostDates);
		hostSelectedDates = hostDates;
		Date minDate = hostDates.get(0);
		Date maxDate = hostDates.get(hostDates.size() - 1);
		// get disabled dates
		ArrayList<DateTime> disabledDateTimes = getDisabledDates(hostDates,
				minDate, maxDate);

		Bundle args = new Bundle();
		Calendar cal = Calendar.getInstance();
		args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
		args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
		args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
		args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
		args.putString(CaldroidFragment.MIN_DATE, formatter.format(cal.getTime()));
		args.putString(CaldroidFragment.MAX_DATE,
				formatter.format(hostDates.get(hostDates.size() - 1)));
		args.putStringArrayList(CaldroidFragment.DISABLE_DATES,
				CalendarHelper.convertToStringList(disabledDateTimes));
		args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, false);
		caldroidFragment.setArguments(args);

		if (event != null) {
			// this.alreadySelectedDates = event.getHostSelectedDates();

			for (int i = 0; i < hostDates.size(); i++) {
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

		final CaldroidListener listener = new CaldroidListener() {

			@Override
			public void onSelectDate(Date date, View view) {
				Toast.makeText(getApplicationContext(),
						"here we are selecting: " + formatter.format(date),
						Toast.LENGTH_SHORT).show();

				if (caldroidFragment != null) {
					Log.i(TAG, "whats in already selected dates: " + hostSelectedDates);
					Log.i(TAG, "what's date: " + date);
					if (inviteeSelectedDates.contains(date)) {
						// then we have to unset the selection
						caldroidFragment.setBackgroundResourceForDate(R.color.blue, date);
						caldroidFragment.setTextColorForDate(R.color.white, date);
						inviteeSelectedDates.remove(date);
					} else {
						// we have to set the selection
						caldroidFragment.setBackgroundResourceForDate(R.color.pink, date);
						caldroidFragment.setTextColorForDate(R.color.white, date);
						inviteeSelectedDates.add(date);
					}
					caldroidFragment.refreshView();
					Log.i(TAG, "already selected dates: " + hostSelectedDates);
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

				// show response

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
				HashMap<User, InviteeResponse> inviteeResponseMap = event
						.getInviteeResponseMap();
				inviteeResponseMap.put(User.getCurUser(),new InviteeResponse());
				InviteeResponse inviteeResponse = inviteeResponseMap.get(User
						.getCurUser());
				HashMap<Date, Response> responseMap = new HashMap<Date, Response>();
				for (int i = 0; i < hostSelectedDates.size(); i++) {
					if (inviteeSelectedDates.contains(hostSelectedDates.get(i))) {
						responseMap.put(hostSelectedDates.get(i), Response.YES);
					} else {
						responseMap.put(hostSelectedDates.get(i), Response.NO);
					}

				}
				inviteeResponse.setResponseMap(responseMap);

				Log.i(TAG, "Response is: " + response);
				// send this response thru parse

				Intent i = new Intent(CalendarActivity.this, ContactsActivity.class);
				i.putExtra("event", event);

				startActivity(i);

			}
		});

	}

	public ArrayList<DateTime> getDisabledDates(ArrayList<Date> hostDates,
			Date minDate, Date maxDate) {
		ArrayList<DateTime> disabledDateTimes = new ArrayList<DateTime>();
		ArrayList<Date> disabledDates = getDatesBetweentMinAndMax(minDate, maxDate,
				hostDates);
		Log.i(TAG, "host dates: " + hostDates);
		Log.i(TAG, "disabled dates: " + disabledDates);
		// convert date to datetimes

		for (Iterator iterator = disabledDates.iterator(); iterator.hasNext();) {
			Date date = (Date) iterator.next();
			disabledDateTimes.add(CalendarHelper.convertDateToDateTime(date));

		}
		return disabledDateTimes;
	}

	private void editCreatedEvent() {
		Log.i(TAG, "IN editCreatedEvent");
		Event event = stubEvent();
		add_or_update_event(event);

	}

	private Event stubEvent() {
		// create a fake event here

		Event e = new Event();
		e.setEventTitle("abc");
		e.setHost(((GroupViteApp)getApplication()).getCurrentUser());
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
		Log.i(TAG, "In createNewEvent");
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
			this.hostSelectedDates = event.getHostSelectedDates();

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
					Log.i(TAG, "whats in already selected dates: " + hostSelectedDates);
					Log.i(TAG, "what's date: " + date);
					if (hostSelectedDates.contains(date)) {
						// then we have to unset the selection
						caldroidFragment.setBackgroundResourceForDate(R.color.white, date);
						caldroidFragment.setTextColorForDate(R.color.black, date);
						hostSelectedDates.remove(date);
					} else {
						// we have to set the selection
						caldroidFragment.setBackgroundResourceForDate(R.color.blue, date);
						caldroidFragment.setTextColorForDate(R.color.white, date);
						hostSelectedDates.add(date);
					}
					caldroidFragment.refreshView();
					Log.i(TAG, "already selected dates: " + hostSelectedDates);
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
				Log.d("SUBHA.", "inside done on click method.");
				// created a new event, so save the current user as host, and event
				// details and selected dates and call new activity
				Event event = new Event();
				event.setHostSelectedDates(new ArrayList<Date>(hostSelectedDates));
				Log.i(TAG, event.getHostSelectedDates().toString());
				EditText eventTitle = (EditText) findViewById(R.id.etEventTitle);
				if (eventTitle.getText() == null
						|| eventTitle.getText().toString().isEmpty()) {
					Toast.makeText(CalendarActivity.this,
							"You need to add an event name!", Toast.LENGTH_SHORT).show();
					return;
				}
				event.setEventTitle(eventTitle.getText().toString());
				User user = ((GroupViteApp) getApplication()).getCurrentUser();

				event.setHost(user);
				event.setInviteeResponseMap(new HashMap<User, InviteeResponse>());
				event.save();
				
				// write the event to global
				Log.d("SUBHA", "saving event to global");
				((GroupViteApp) getApplication()).setCurrentEvent(event);

				// find out if event was saved

			

				if (user.getEvents() == null) {
					ArrayList<Event> events = new ArrayList<Event>();
					user.setEvents(events);
				}

				// save to sql

				user.getEvents().add(event);
			
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
