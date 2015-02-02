package com.zhao.temporal.Utils;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class CalendarProcess {

	//	Format time: like "2012-03-12"
	public static int daysGap(String startTimeString, String endTimeString) {
		DateTime startDate = new DateTime(startTimeString);
		DateTime endDate = new DateTime(endTimeString);		
		int gapDays = Days.daysBetween(startDate, endDate).getDays();		
		return gapDays;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(daysGap("2012-03-31", "2013-04-16"));
		System.out.println(daysGap("2013-04-16", "2012-03-31"));
		DateTime testDate1 = new DateTime("2014-W36");
		System.out.println(testDate1);
		System.out.println(daysGap("2012-03-31", "2013-04"));
		System.out.println(testDate1.isAfterNow());
		DateTime testDate2 = null;
		System.out.println(testDate2);
		DateTime testDate3 = new DateTime("2014-09-25T15:32");
		System.out.println(testDate3);
		DateTime testDate4 = new DateTime("PRESENT_REF");
		System.out.println(testDate4);
	}

}
