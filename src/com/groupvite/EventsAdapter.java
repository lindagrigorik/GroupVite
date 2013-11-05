package com.groupvite;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupvite.models.Event;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EventsAdapter extends ArrayAdapter<Event> {

	public EventsAdapter(Context context, List<Event> events) {
		super(context, 0, events);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = (View) convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = (View) inflater.inflate(R.layout.profile_pic_item, null);
		}
		
		Event event = getItem(position);		
		ImageView imageView = (ImageView) view.findViewById(R.id.ivProfile);
		ImageLoader.getInstance().displayImage(event.getHost().getPicUrl(), imageView);
		
		TextView nameView = (TextView) view.findViewById(R.id.tvName);
		nameView.setText(event.getEventTitle());
		
		return view;
	}
}