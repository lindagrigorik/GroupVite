package com.groupvite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.groupvite.models.Event;
import com.groupvite.util.Operation;

public class CalendarActivity extends Activity {
	protected static final String TAG = "GroupVite";
//	CalendarPickerView calendar;
	EditText eventTitle;
	Event event;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);

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
		}
		switch (Operation.valueOf(operation)) {
		case ADD_NEW_EVENT:
			// have to create a new event
//			createNewEvent();
			break;
		case RESPOND_TO_INVITE:
			// get from database, the event details
			// show list of preselected dates that you can choose to
			break;
		case EDIT_CREATED_EVENT:
			// get from database, the event details
			// show calendar
			break;
		case EDIT_RESPONDED_EVENT:
			// get from database, the event details, show calendar with preselected
			// dates
			break;

		default:
			Log.e(TAG, "Some unknown type we switched on");
			break;
		}
		// if (operation.equalsIgnor)

	}
/*
	private void createNewEvent() {
		// TODO Auto-generated method stub
		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);

		final Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.YEAR, -1);
		calendar = (CalendarPickerView) findViewById(R.id.calendar_view);

		Calendar today = Calendar.getInstance();
		ArrayList<Date> dates = new ArrayList<Date>();
		dates.add(today.getTime());

		calendar.init(new Date(), nextYear.getTime()) //
				.inMode(SelectionMode.MULTIPLE) //
				.withSelectedDates(dates);

		Button done = (Button) findViewById(R.id.done_button);
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<Date> selectedDates = (ArrayList<Date>) calendar
						.getSelectedDates();
				Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
				eventTitle = (EditText) findViewById(R.id.etEventTitle);
				event = new Event();

				Log.i(TAG, "eventtitle was: " + eventTitle.getText());
				if (eventTitle.getText() == null
						|| eventTitle.getText().toString().isEmpty()
						|| eventTitle.getText().toString() == "") {
					Toast.makeText(CalendarActivity.this, "You didn't enter title name!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				event.setEventTitle(eventTitle.getText().toString());
				event.setDays(selectedDates);

				intent.putExtra("event", event);

				for (int i = 0; i < selectedDates.size(); i++) {
					Log.i(TAG, selectedDates.get(i).toString());
				}
				startActivity(intent);

			}
		});

	}
*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calendar, menu);
		return true;
	}

	public void onFinish(View v){
	    Intent i = new Intent(this, ContactsActivity.class);
	    startActivity(i);
	}
}
