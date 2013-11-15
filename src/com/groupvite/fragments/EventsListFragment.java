package com.groupvite.fragments;

import java.util.ArrayList;

import com.groupvite.CalendarActivity;
import com.groupvite.EventsActivity;
import com.groupvite.EventsAdapter;
import com.groupvite.GroupViteApp;
import com.groupvite.R;
import com.groupvite.models.Event;
import com.groupvite.models.User;
import com.groupvite.util.ParseClient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class EventsListFragment extends Fragment {

    private EventsAdapter adapter;
    private User currUser;
    
    public static EventsListFragment newInstance(User user) {
	EventsListFragment fragment = new EventsListFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	adapter = new EventsAdapter(getActivity(),
		new ArrayList<Event>());
	currUser = (User)getArguments().getSerializable("user");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
	View v = inflater.inflate(R.layout.fragment_events_list, parent, false);
	ListView lvEvents = (ListView) v.findViewById(R.id.lvEvents);
	lvEvents.setAdapter(adapter);
	this.attachOnClickEvent(lvEvents);
	this.retrieveEvents();
	return v;
    }
    
    //attach handler on event click
    private void attachOnClickEvent(ListView lvEvents){
	lvEvents.setOnItemClickListener(new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,
				long res) {
			Event e = (Event) adapter.getItemAtPosition(position);
			Intent i = new Intent(getActivity(),CalendarActivity.class);
			i.putExtra("event",e);
			i.putExtra("currentUser", currUser);

			if(e.getHost().getParseId().equalsIgnoreCase(currUser.getParseId())){
				//it's the host's own event that he/she is trying to edit
				i.putExtra("operation", "EDIT_CREATED_EVENT");
			}else{
				i.putExtra("operation", "RESPOND_TO_INVITE");
			}
			EventsListFragment.this.startActivity(i);
		}
	});
    }
    
    //add events to adapter
    protected void addToList(ArrayList<Event> events){
	if (events.size()>0) adapter.addAll(events);
    }
    
    //get user events
    protected void retrieveEvents(){
	ParseClient.populateUser(currUser);
	ParseClient.createParseUser(currUser);
	ArrayList<Event> events = ParseClient.getUserEventsList(currUser);
	this.addToList(events);

    }
    
}
