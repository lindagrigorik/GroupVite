package com.groupvite;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupvite.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ContactsAdapter extends ArrayAdapter<User> {

	public ContactsAdapter(Context context, List<User> users) {
		super(context, 0, users);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = (View) convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = (View) inflater.inflate(R.layout.profile_pic_item, null);
		}
		
		User user = getItem(position);		
		ImageView imageView = (ImageView) view.findViewById(R.id.ivProfile);
		ImageLoader.getInstance().displayImage(user.getPicUrl(), imageView);
		
		TextView nameView = (TextView) view.findViewById(R.id.tvName);
		nameView.setText(user.getName());
		
		return view;
	}
}
