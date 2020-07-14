package com.jge.server.utils;

import java.util.Calendar;
import java.util.TimeZone;

import com.jge.server.net.AppContext;

public class DateUtils {
	public static final TimeZone TIME_ZONE = TimeZone.getTimeZone(AppContext.getProperty("com.jge.server.net.timezone", "UTC"));
	
	public static Calendar getCalendar() {
		return Calendar.getInstance(TIME_ZONE);
	}
	
	public static long getTimeInMillis() {
		return getCalendar().getTimeInMillis();
	}
}
