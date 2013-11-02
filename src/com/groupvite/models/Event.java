package com.groupvite.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2244371851975031083L;
	
	private String eventTitle;
	
	private ArrayList<Day> days;
	private User host;
	private Day finalSelectedDay;

	public Event(){}
	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public List<Day> getDays() {
		return days;
	}

	public void setDays(ArrayList<Date> selectedDates) {
		this.days = Day.convertToDays(selectedDates);
	}

	public User getHost() {
		return host;
	}

	public void setHost(User host) {
		this.host = host;
	}

	public Day getFinalSelectedDay() {
		return finalSelectedDay;
	}

	public void setFinalSelectedDay(Day finalSelectedDay) {
		this.finalSelectedDay = finalSelectedDay;
	}

}
