package com.groupvite.models;

import java.io.Serializable;
import java.util.Date;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Responses")
public class Response extends Model implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5665700205008656511L;
	@Column(name = "EventTitle")
	private String eventTitle;
	@Column(name = "Invitee")
	private User invitee;
	@Column(name = "Response")
	private String response;
	@Column(name = "OneOfHostSelectedDates")
	private Date oneOfHostSelectedDates;

	public Response() {
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public User getInvitee() {
		return invitee;
	}

	public void setInvitee(User invitee) {
		this.invitee = invitee;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Date getOneOfHostSelectedDates() {
		return oneOfHostSelectedDates;
	}

	public void setOneOfHostSelectedDates(Date oneOfHostSelectedDates) {
		this.oneOfHostSelectedDates = oneOfHostSelectedDates;
	}

}
