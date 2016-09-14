package com.fdv.loggedoff.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

        public static final String HOUR_MINS_HS = "HH:mm'hs'";
        public static final String HOUR_MINS = "HH:mm";
        public static final String DAYMONTHYEAR = "ddMMyy";

        public static String formatDate(Date date, String format) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(date);
        }

        public static String formatDate(Date date, String format, boolean capitalize) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            String str = dateFormat.format(date);

            if (capitalize) {
                str = str.substring(0, 1).toUpperCase().concat(str.substring(1));
            }
            return str;
        }

        public static String formatHour(Date date, String format) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(date);
        }

        public static long getDifferenceDays(Date sinceDate, Date untilDate) {
            long diff = untilDate.getTime() - sinceDate.getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        }


}
