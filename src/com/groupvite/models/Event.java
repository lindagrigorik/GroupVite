package com.groupvite.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Events")
public class Event extends Model implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2244371851975031083L;
	@Column(name = "EventTitle")
	private String eventTitle;
	@Column(name = "HostSelectedDates")
	private ArrayList<Date> hostSelectedDates;
	@Column(name = "Host")
	private String host;
	@Column(name = "FinalSelectedDay")
	private Date finalSelectedDay;

	public Event() {
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public ArrayList<Date> getHostSelectedDates() {
		return this.hostSelectedDates;
	}

	public void setHostSelectedDates(ArrayList<Date> selectedDates) {
		this.hostSelectedDates = selectedDates;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Date getFinalSelectedDay() {
		return finalSelectedDay;
	}

	public void setFinalSelectedDay(Date finalSelectedDay) {
		this.finalSelectedDay = finalSelectedDay;
	}

	public String toString() {
		return this.eventTitle + "hosted by: " + this.host
				+ " who selected the following dates: " + this.hostSelectedDates;
	}

}
