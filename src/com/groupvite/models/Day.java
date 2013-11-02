package com.groupvite.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Days")
public class Day extends Model implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5927378082035803886L;
	@Column(name="Date")
	private Date date;
	@Column(name="responseMap")
	private HashMap<User, Response> responses;

	public Day() {
	}

	public Day(Date date, HashMap<User, Response> responses) {
		this.date = date;
		this.responses = responses;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Map<User, Response> getResponses() {
		return responses;
	}

	public void setResponses(HashMap<User, Response> responses) {
		this.responses = responses;
	}

	public static ArrayList<Day> convertToDays(ArrayList<Date> dates) {
		ArrayList<Day> listOfDays = new ArrayList<Day>();
		for (int i = 0; i < dates.size(); i++) {
			HashMap<User, Response> responses = new HashMap<User, Response>();
			Day day = new Day(dates.get(i), responses);
			listOfDays.add(day);

		}
		return listOfDays;
	}
	
	public String toString(){
		return this.date + " responses: "+this.responses;
	}

}
