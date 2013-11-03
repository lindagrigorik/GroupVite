package com.groupvite.models;

import java.io.Serializable;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import com.facebook.model.GraphUser;
@Table(name ="Users")
public class User extends Model implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6827603829256994906L;
	@Column(name = "Events")
	private List<Event> events;
	@Column(name = "UserId")
	private String id;
	@Column(name = "Username")
	private String name;
	
	private String picUrl;

	// need to add more fields depending on what we get back from Facebook
	public User() {
	}
	
	public User(String id, String name) {
		this.id = id;
		this.name = name;
		this.picUrl = buildPicUrl();
	}

	public List<Event> getEvents() {
		return this.events;
//		return getMany(Event.class, "User");
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public String getUserId() {
		return id;
	}

	public void setUserId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPicUrl() {
		return this.picUrl;
	}

	public static User getCurUser() {
		User u = new User();
		u.setName("Neha");
		u.setUserId("123");
		return u;
	}
	
    private String buildPicUrl() {
    	return "http://graph.facebook.com/" + this.id + "/picture";
    }
    
    public static User fromGraphUser(GraphUser user) {
    	return new User(user.getId(), user.getName());
    }

	public String toString() {
		return "name is:" + this.getName() + " userId: " + this.getUserId()
				+ " events are: " + this.getEvents();
	}

}
