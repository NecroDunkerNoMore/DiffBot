package org.ndnm.diffbot.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {
    private static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");
    private static final SimpleDateFormat DATE_FORMAT;
    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        DATE_FORMAT.setTimeZone(GMT_TIME_ZONE);
    }


    public static String formatGmt(Date date) {
        return DATE_FORMAT.format(date);
    }


    public static Date getTimeGmt() {
        return getGmtCalendar().getTime();
    }


    public static Calendar getGmtCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(GMT_TIME_ZONE);
        return cal;
    }
}
