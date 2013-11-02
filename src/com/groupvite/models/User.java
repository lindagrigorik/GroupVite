package com.groupvite.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6827603829256994906L;
	private List<Event> events;
	private long userId;
	private String name;

	// need to add more fields depending on what we get back from Facebook
	public User() {
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static User getCurUser() {
		User u = new User();
		u.setName("Neha");
		u.setUserId(123);
		return u;
	}

}
