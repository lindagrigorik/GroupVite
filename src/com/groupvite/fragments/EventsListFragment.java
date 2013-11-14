package com.groupvite.fragments;

import java.util.ArrayList;

import com.groupvite.EventsAdapter;
import com.groupvite.R;
import com.groupvite.models.Event;
import com.groupvite.models.User;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
	adapter = new EventsAdapter(getActivity(),
		new ArrayList<Event>());
	currUser = (User)getArguments().getSerializable("user");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
	View v = inflater.inflate(R.layout.fragment_events_list, parent, false);
	ListView lvEvents = (ListView) v.findViewById(R.id.lvEvents);
	lvEvents.setAdapter(adapter);
	
	return v;
    }
    
    protected void addToList(ArrayList<Event> events){
	if (events.size()>0) adapter.addAll(events);
    }
    
}
