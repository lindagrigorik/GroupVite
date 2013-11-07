package com.groupvite.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.parse.ParseObject;

public class Event implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2244371851975031083L;
	private String eventParseId;
	private String eventTitle;
	private ArrayList<Date> hostSelectedDates;
	private User host;
	private HashMap<User, InviteeResponse> inviteeResponseMap;
	private Date finalSelectedDay;
	private List<User> invitedUsers;
	private String messageBody;

	public String getMessageBody() {
	    return messageBody;
	}

	public void setMessageBody(String messageBody) {
	    this.messageBody = messageBody;
	}

	public Event() {
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public void setEventParseId(String eventParseId) {
		this.eventParseId = eventParseId;
	}

	public String getEventParseId() {
		return this.eventParseId;
	}

	public ArrayList<Date> getHostSelectedDates() {
		return this.hostSelectedDates;
	}

	public void setHostSelectedDates(ArrayList<Date> selectedDates) {
		this.hostSelectedDates = selectedDates;
	}

	public User getHost() {
		return this.host;
	}

	public void setHost(User host) {
		this.host = host;
	}

	public void setInvitedUsers(ArrayList<User> invitedUsers) {
		this.invitedUsers = invitedUsers;
	}

	public Date getFinalSelectedDay() {
		return finalSelectedDay;
	}

	public void setFinalSelectedDay(Date finalSelectedDay) {
		this.finalSelectedDay = finalSelectedDay;
	}

	public String toString() {
		return this.eventTitle + "hosted by: " + this.host
				+ " who selected the following dates: " + this.hostSelectedDates
				+ " invitee response map is: " + this.inviteeResponseMap;
	}

	public List<User> getInvitedUsers() {
		return this.invitedUsers;
	}

	public HashMap<User, InviteeResponse> getInviteeResponseMap() {
		return inviteeResponseMap;
	}

	public void setInviteeResponseMap(
			HashMap<User, InviteeResponse> inviteeResponseMap) {
		this.inviteeResponseMap = inviteeResponseMap;
	}

	public void initializeInviteeResponseForUser(User user) {
		if (this.getInviteeResponseMap() == null) {
			HashMap<User, InviteeResponse> inviteeResponseMap = new HashMap<User, InviteeResponse>();

			this.setInviteeResponseMap(inviteeResponseMap);
		}
		if (inviteeResponseMap.get(user) == null) {
			InviteeResponse inviteeResponse = new InviteeResponse();
			HashMap<Date, Response> resp = new HashMap<Date, Response>();
			for (int i = 0; i < hostSelectedDates.size(); i++) {
				resp.put(hostSelectedDates.get(i), Response.NO_RESPONSE);
			}
			inviteeResponse.setResponseMap(resp);

			inviteeResponseMap.put(user, inviteeResponse);
		}
	}

	public void populateInviteeResponseMap(ParseObject eventObject) {
		for (User user : this.invitedUsers) {
			initializeInviteeResponseForUser(user);
			List<Date> yesDates = new ArrayList<Date>();
			List<Object> rawDates = eventObject.getList(user.getParseId());
			if (rawDates != null && rawDates.size() > 0) {
				for (Object date : rawDates) {
					yesDates.add(new Date(Long.parseLong(date.toString())));
				}
			}
			if (eventObject.has(user.getParseId())) {
				HashMap<Date, Response> dateToResponse = this.inviteeResponseMap.get(
						user).getResponseMap();
				for (Date hostDate : this.hostSelectedDates) {
					if (yesDates.contains(hostDate)) {
						dateToResponse.put(hostDate, Response.YES);
					} else {
						dateToResponse.put(hostDate, Response.NO);
					}
				}
			}
		}
	}

}
