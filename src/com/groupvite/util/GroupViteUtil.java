package com.groupvite.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class GroupViteUtil {
 static	final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	public static ArrayList<Date> formatDatesWithoutTimes(
			ArrayList<Date> hostDates) {
		// TODO Auto-generated method stub
		
		for (int i = 0; i < hostDates.size(); i++) {
			Date date = hostDates.get(i);
			try {
				date = formatter.parse(formatter.format(date));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hostDates.set(i, date);
					
		}
			
			
		return hostDates;
	}

}
