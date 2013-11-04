package com.groupvite.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Event implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2244371851975031083L;
	private long eventId;
	private String eventTitle;
	private ArrayList<Date> hostSelectedDates;
	private User host;
	private HashMap<User, InviteeResponse> inviteeResponseMap;
	@Column(name = "FinalSelectedDay")
	private List<User> invitedUsers;

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
				+ " who selected the following dates: " + this.hostSelectedDates;
	}
	
	public List<String> getInvitedUsersParseIds() {
		List<String> results = new ArrayList<String>();
		for (User u : this.invitedUsers) {
			results.add(u.getParseId());
		}
		
		return results;
	}

	public HashMap<User, InviteeResponse> getInviteeResponseMap() {
		return inviteeResponseMap;
	}

	public void setInviteeResponseMap(
			HashMap<User, InviteeResponse> inviteeResponseMap) {
		this.inviteeResponseMap = inviteeResponseMap;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
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

}
