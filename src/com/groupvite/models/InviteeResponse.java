package com.groupvite.models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;


public class InviteeResponse  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5665700205008656511L;
	private HashMap<Date, Response> responseMap;

	public InviteeResponse() {
	}

	public HashMap<Date, Response> getResponseMap() {
		return responseMap;
	}

	public void setResponseMap(HashMap<Date, Response> responseMap) {
		this.responseMap = responseMap;
	}


}
