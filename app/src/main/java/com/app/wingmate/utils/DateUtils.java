package com.app.wingmate.utils;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.format.DateUtils.FORMAT_NUMERIC_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_YEAR;
import static android.text.format.DateUtils.MINUTE_IN_MILLIS;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;

public class DateUtils {

    public static final String DATE_FORMAT_DATETIME = "MM/dd/yyyy";
    public static final String DATE_FORMAT = "MM-dd-yyyy";
    public static final String DATE_FORMAT_DATETIME_TWO = "MM/dd yyyy";
    public static final String DATE_FORMAT_MONTH = "MM/dd ";
    public static String TIME_FORMAT;
    //    public static final String DATE_TIME_FORMAT = "MM/dd/yyyy hh:mm a";
    public static String DATE_TIME_FORMAT;


    public static String ISOFormat(DateTime dateTime) {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeNoMillis();
        return fmt.print(dateTime.withZone(DateTimeZone.UTC));
    }

    public static String dailyEarningItemFormat(String dateTime) throws Exception {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
        DateTimeFormatter format = DateTimeFormat.forPattern(TIME_FORMAT);
//        DateTimeFormatter format = DateTimeFormat.forPattern("HHmm");
        DateTime dateTime1 = fmt.parseDateTime(dateTime);
        return format.print(dateTime1);
    }

    public static String standardFormat(String dateTime) throws Exception {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
        DateTimeFormatter format = DateTimeFormat.forPattern(DATE_FORMAT_DATETIME + " " + TIME_FORMAT);
        DateTime dateTime1 = fmt.parseDateTime(dateTime);
        return format.print(dateTime1);
    }


    public static String standardTimeFormat(String dateTime) throws Exception {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
        DateTimeFormatter format = DateTimeFormat.forPattern(TIME_FORMAT);
        DateTime dateTime1 = fmt.parseDateTime(dateTime);
        return format.print(dateTime1);
    }

    public static String standardDateFormat(String dateTime) throws Exception {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
        DateTimeFormatter format = DateTimeFormat.forPattern(DATE_FORMAT_DATETIME);
        DateTime dateTime1 = fmt.parseDateTime(dateTime);
        return format.print(dateTime1);
    }

    public static String getDateOnly(String dateTime) throws Exception {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
        DateTimeFormatter format = DateTimeFormat.forPattern(DATE_FORMAT);
        DateTime dateTime1 = fmt.parseDateTime(dateTime);
        return format.print(dateTime1);
    }

    public static DateTime getDateTimeObject(String dateTime) throws Exception {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
        DateTimeFormatter format = DateTimeFormat.forPattern(DATE_FORMAT_DATETIME + " " + TIME_FORMAT);
        DateTime dateTime1 = fmt.parseDateTime(dateTime);
        return dateTime1;
    }

    public static String standardFormatJustTime(String dateTime) throws Exception {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
        DateTimeFormatter format = DateTimeFormat.forPattern(TIME_FORMAT);
        DateTime dateTime1 = fmt.parseDateTime(dateTime);
        return format.print(dateTime1);
    }

    public static DateTime parseIso(String date) throws Exception {
        return ISODateTimeFormat.dateTime().parseDateTime(date);
    }

    public static String dailyHeadingFormat(DateTime dateTime) throws Exception {
        return DateUtils.parseIso(dateTime.toString()).toString(DATE_FORMAT_DATETIME);


    }

    public static String formatDate(DateTime mDateTime) throws Exception {
        return DateTimeFormat.forPattern("dd'th' MMM").print(mDateTime);
    }

    public static String formatTime(DateTime mDateTime) throws Exception {
//        return DateTimeFormat.forPattern("HHmm").print(mDateTime);
        return DateTimeFormat.forPattern(TIME_FORMAT).print(mDateTime);
    }

    public static String weeklyHeadingFormat(DateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_FORMAT_MONTH);
        return fmt.print(dateTime);
    }

    public static String weeklyWithYearHeadingFormat(DateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_FORMAT_DATETIME_TWO);
        return fmt.print(dateTime);
    }

    public static String monthlyHeadingWithYearFormat(DateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMM yyyy");
        return fmt.print(dateTime);
    }

    public static String monthlyHeadingFormat(DateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_FORMAT_MONTH);
        return fmt.print(dateTime);
    }

    public static int hoursDifference(String dateTime) throws Exception {
        DateTime dateTime1 = parseIso(dateTime);
        return DateTime.now().minus(dateTime1.getMillis()).getHourOfDay();
    }


    public static DateTime ConvertDateToDateTime(Date date) {
        SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // datef.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd' 'HH:mm:ss");
        //    DateTime dateTimeInUTC = formatter.withZoneUTC().parseDateTime(datef.format(date));
        DateTime dateTimeInUTC = formatter.parseDateTime(datef.format(date));
        return dateTimeInUTC;

    }

    public static DateTime ConvertDateToDateUTCTime(Date date) {
        SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // datef.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd' 'HH:mm:ss");
        DateTime dateTimeInUTC = formatter.withZoneUTC().parseDateTime(datef.format(date));
//        DateTime dateTimeInUTC = formatter.parseDateTime(datef.format(date));
        return dateTimeInUTC;

    }

    public static String changeDateFormateForTextView(String dateTime) {
        DateTime dtSameTime = new DateTime(dateTime);
//        String a = dtSameTime.toString("hh:mm a");
//        String b = dtSameTime.toString("MMM dd, yyyy");
        String b = dtSameTime.toString(DATE_FORMAT_DATETIME + " " + TIME_FORMAT);
        String newDate = b;
        return newDate;
    }


    public static String convertCoustomeDate(String dateTime) {
        // from 23/56/2018  to xxxxxxxx
        DateTime dtSameTime = new DateTime(dateTime);
//        String a = dtSameTime.toString("hh:mm a");
//        String b = dtSameTime.toString("MMM dd, yyyy");
        String b = dtSameTime.toString(DATE_FORMAT_DATETIME);
        String newDate = b;
        return newDate;
    }

    public static DateTime convertStringDateTimeToDateTime(String dateTime) {
        DateTime dtSameTime = DateTime.parse(dateTime);
        return dtSameTime;
    }

    //
//    public static String standardTimeFormate(String mDateTime) throws Exception {
//
//        DateTime dtSameTime = new DateTime(mDateTime);
//        String a = dtSameTime.toString("hh:mm aa");
//        String b = dtSameTime.toString("EE, MMM dd, yyyy");
//        return (b + " " + "(" + a + ")");
//    }
    public static String standardTimeFormate(String dateTime) throws Exception {

//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
        DateTimeFormatter format = DateTimeFormat.forPattern(DATE_FORMAT_DATETIME + " " + TIME_FORMAT);
        DateTime dateTime1 = fmt.parseDateTime(dateTime);
        return format.print(dateTime1);
    }

    public static String getTwoDaysOldFormattedDate() {
        return getFormattedDate(AppConstants.TWO_DAYS_AGO_TIME_MILLISECONDS);
    }

    public static String getFormattedDate(long miliSeconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(miliSeconds);
        return sdf.format(calendar.getTime());
    }

    public static Calendar dateAjustment(int year, int day, boolean isfuture) {

        Calendar calendar = Calendar.getInstance(); // this would default to now
        calendar.add(Calendar.YEAR, -year);
        if (isfuture) {
            calendar.add(Calendar.DAY_OF_MONTH, +day);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -day);
        }
        return calendar;
    }

    public static String formatDateFromString(String inputFormat, String outputFormat, String inputDate) {
        Date parsed = null;
        String outputDate = "";
        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());
        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (ParseException e) {
        }
        return outputDate;
    }

    public static String timeDifference(long responseTime) {

//        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
//        ParsePosition pos = new ParsePosition(0);
        long then = responseTime;
        long now = new Date().getTime();

        long seconds = (now - then) / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = weeks / 30;

//        int days = (int) ((totalsec/ (1000*60*60*24)) % 7);
//        int weeks = (int) (totalsec/ (1000*60*60*24*7));
//        int months = weeks/30;
//        int years = months/365;

        String friendly = null;
        long num = 0;
        if (days > 0) {
            num = days;
            friendly = days + " day";
        } else if (hours > 0) {
            num = hours;
            friendly = hours + " hour";
        } else if (minutes > 0) {
            num = minutes;
            friendly = minutes + " minute";
        } else {
            num = seconds;
            friendly = seconds + " second";
        }
        if (num > 1) {
            friendly += "s";
        }
        return friendly;
    }

    public static String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(" Days ");
        sb.append(hours);
        sb.append(" Hours ");
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        return (sb.toString());
    }


    private static final int MINS_PER_DAY = 60 * 24;
    private static final long MS_PER_DAY = 1000 * 60 * MINS_PER_DAY;

    private static final int SEC = 1000;
    private static final int MIN = SEC * 60;
    private static final int HOUR = MIN * 60;
    private static final int DAY = HOUR * 24;
    private static final long WEEK = DAY * 7;
    private static final long YEAR = WEEK * 52;

    public static String getDiff(long milliseconds) {
        int seconds = (int) (milliseconds / (1000 * 60)) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60 * 60)) % 24);
        int weeks = (int) ((milliseconds / (1000 * 60 * 60 * 60 * 24)) % 7);
        int days = (int) ((milliseconds / (1000 * 60 * 60 * 60 * 24 * 7)) % 30);
        int months = (int) ((milliseconds / (1000 * 60 * 60 * 60 * 24 * 7 * 30)) % 12);
        int year = (int) ((milliseconds / (1000 * 60 * 60 * 60 * 24 * 7 * 30)) % 12);


        return "";
    }

    public static void tt() {
        Pattern p = Pattern.compile("(\\d+)\\s+(.*?)s? ago");

        Map<String, Integer> fields = new HashMap<String, Integer>() {{
            put("second", Calendar.SECOND);
            put("minute", Calendar.MINUTE);
            put("hour", Calendar.HOUR);
            put("day", DATE);
            put("week", Calendar.WEEK_OF_YEAR);
            put("month", MONTH);
            put("year", Calendar.YEAR);
        }};

        String[] tests = {
                "3 days ago",
                "1 minute ago",
                "2 years ago"
        };

        for (String test : tests) {

            Matcher m = p.matcher(test);

            if (m.matches()) {
                int amount = Integer.parseInt(m.group(1));
                String unit = m.group(2);

                Calendar cal = Calendar.getInstance();
                cal.add(fields.get(unit), -amount);
//                System.out.printf("%s: %tF, %<tT%n", test, cal);
            }
        }


//        Calendar c1, c2;
//
//
//        long ms1= c1.getTimeInMillis();
//        long ms2= c2.getTimeInMillis();
//        long totalsec = (ms2- ms1) / 1000;
//
//        int days = (int) ((totalsec/ (1000*60*60*24)) % 7);
//        int weeks = (int) (totalsec/ (1000*60*60*24*7));
//        int months = weeks/30;
//        int years = months/365;

    }

    public static CharSequence getRelativeTime(final long date) {
        long now = System.currentTimeMillis();
//        if (Math.abs(now - date.getTime()) > 60000)
        if (Math.abs(now - date) > 60000)
//            return android.text.format.DateUtils.getRelativeTimeSpanString(date.getTime(), now,
            return android.text.format.DateUtils.getRelativeTimeSpanString(date, now,
                    MINUTE_IN_MILLIS, FORMAT_SHOW_DATE | FORMAT_SHOW_YEAR
                            | FORMAT_NUMERIC_DATE);
        else
            return "Just now";
    }

    public static int getDiffYears(Date last) {
        // Creates two calendars instances
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        // Set the date for both of the calendar instance
//        cal1.set(2006, Calendar.DECEMBER, 30);
//        cal2.set(2007, Calendar.MAY, 3);
        cal2.setTime(last);

        // Get the represented date in milliseconds
        long millis1 = cal1.getTimeInMillis();
        long millis2 = cal2.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = millis2 - millis1;

        // Calculate difference in seconds
        long diffSeconds = diff / 1000;

        // Calculate difference in minutes
        long diffMinutes = diff / (60 * 1000);

        // Calculate difference in hours
        long diffHours = diff / (60 * 60 * 1000);

        // Calculate difference in days
        long diffDays = diff / (24 * 60 * 60 * 1000);

        // Calculate difference in weeks
        long diffWeeks = diff / (24 * 60 * 60 * 1000);

//        System.out.println("In milliseconds: " + diff + " milliseconds.");
//        System.out.println("In seconds: " + diffSeconds + " seconds.");
//        System.out.println("In minutes: " + diffMinutes + " minutes.");
//        System.out.println("In hours: " + diffHours + " hours.");
//        System.out.println("In days: " + diffDays + " days.");


        return 0;
    }


    public void printDifference(Date startDate, Date endDate) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

//        System.out.println("startDate : " + startDate);
//        System.out.println("endDate : " + endDate);
//        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weekInMilli = daysInMilli * 7;
        long monthInMilli = weekInMilli * 4;
        long yearInMilli = monthInMilli * 12;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

//        System.out.printf(
//                "%d days, %d hours, %d minutes, %d seconds%n",
//                elapsedDays,
//                elapsedHours, elapsedMinutes, elapsedSeconds);

    }


    public static void secondsToString(long seconds) {
        double numyears = Math.floor(seconds / 31536000);
        double numdays = Math.floor((seconds % 31536000) / 86400);
        double numhours = Math.floor(((seconds % 31536000) % 86400) / 3600);
        double numminutes = Math.floor((((seconds % 31536000) % 86400) % 3600) / 60);
        double numseconds = (((seconds % 31536000) % 86400) % 3600) % 60;
//        System.out.println(numyears + " years " + numdays + " days " + numhours + " hours " + numminutes + " minutes " + numseconds + " seconds");

    }


    /////// ADNAN ////////////
    public static String printDifference222(Date startDate, Date endDate) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

//        System.out.println("startDate : " + startDate);
//        System.out.println("endDate : " + endDate);
//        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weekInMilli = daysInMilli * 7;
        long monthInMilli = weekInMilli * 4;
        long yearInMilli = monthInMilli * 12;


        long elapsedYear = different / yearInMilli;
        different = different % yearInMilli;

        long elapesMonth = different / monthInMilli;
        different = different % monthInMilli;


        long elapsedWeeks = different / weekInMilli;
        different = different % weekInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

//        System.out.printf(
//                "%d year, %d month, %d weeks, %d days, %d hours, %d minutes, %d seconds%n",
//                elapsedYear, elapesMonth, elapsedWeeks, elapsedDays,
//                elapsedHours, elapsedMinutes, elapsedSeconds);


        String txt = "";
        if (elapsedYear == 1)
            txt = elapsedYear + " Year";
        else if (elapsedYear > 0)
            txt = elapsedYear + " Years";
        else if (elapesMonth == 1)
            txt = elapesMonth + " Month";
        else if (elapesMonth > 0)
            txt = elapesMonth + " Months";
        else if (elapsedWeeks == 1)
            txt = elapsedWeeks + " Week";
        else if (elapsedWeeks > 0)
            txt = elapsedWeeks + " Weeks";
        else if (elapsedDays == 1)
            txt = elapsedDays + " Day";
        else if (elapsedDays > 0)
            txt = elapsedDays + " Days";
        else {
            if (elapsedHours == 1)
                txt = elapsedHours + " Hour";
            else if (elapsedHours > 0)
                txt = elapsedHours + " Hours";
            if (elapsedMinutes == 1)
                txt = txt + " " + elapsedMinutes + " Min";
            else if (elapsedMinutes > 0)
                txt = txt + " " + elapsedMinutes + " Mins";
            else if (elapsedHours == 0 && elapsedMinutes == 0 && elapsedSeconds > 0)
                txt = elapsedSeconds + " Sec";
        }

//        System.out.println("===" + txt);
        return txt;
    }

    public static String getTimeDiff(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weekInMilli = daysInMilli * 7;
        long monthInMilli = weekInMilli * 4;
        long yearInMilli = monthInMilli * 12;

        long elapsedYear = different / yearInMilli;
        different = different % yearInMilli;

        long elapesMonth = different / monthInMilli;
        different = different % monthInMilli;


        long elapsedWeeks = different / weekInMilli;
        different = different % weekInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        String txt = "";
        if (elapsedYear == 1)
            txt = elapsedYear + ",Year";
        else if (elapsedYear > 0)
            txt = elapsedYear + ",Years";
        else if (elapesMonth == 1)
            txt = elapesMonth + ",Month";
        else if (elapesMonth > 0)
            txt = elapesMonth + ",Months";
        else if (elapsedWeeks == 1)
            txt = elapsedWeeks + ",Week";
        else if (elapsedWeeks > 0)
            txt = elapsedWeeks + ",Weeks";
        else if (elapsedDays == 1)
            txt = elapsedDays + ",day";
        else if (elapsedDays > 0)
            txt = elapsedDays + ",days";
        else {
            if (elapsedHours == 1)
                txt = elapsedHours + ",hour";
            else if (elapsedHours > 0)
                txt = elapsedHours + ",hours";
            else if (elapsedMinutes == 1)
                txt = elapsedMinutes + ",min";
            else if (elapsedMinutes > 0)
                txt = elapsedMinutes + ",mins";
            else if (elapsedHours == 0 && elapsedMinutes == 0 && elapsedSeconds > 0)
                txt = elapsedSeconds + ",sec";
        }
        return txt;
    }


    /////// ADNAN ////////////
    public static boolean greaterThan30Min(Date startDate, Date endDate) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

//        System.out.println("startDate : " + startDate);
//        System.out.println("endDate : " + endDate);
//        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weekInMilli = daysInMilli * 7;
        long monthInMilli = weekInMilli * 4;
        long yearInMilli = monthInMilli * 12;


        long elapsedYear = different / yearInMilli;
        different = different % yearInMilli;

        long elapesMonth = different / monthInMilli;
        different = different % monthInMilli;


        long elapsedWeeks = different / weekInMilli;
        different = different % weekInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

//        System.out.printf(
//                "%d year, %d month, %d weeks, %d days, %d hours, %d minutes, %d seconds%n",
//                elapsedYear, elapesMonth, elapsedWeeks, elapsedDays,
//                elapsedHours, elapsedMinutes, elapsedSeconds);


        String txt = "";
        if (elapsedYear == 1)
            return true;
        else if (elapsedYear > 0)
            return true;
        else if (elapesMonth == 1)
            return true;
        else if (elapesMonth > 0)
            return true;
        else if (elapsedWeeks == 1)
            return true;
        else if (elapsedWeeks > 0)
            return true;
        else if (elapsedDays == 1)
            return true;
        else if (elapsedDays > 0)
            return true;
        else {
            if (elapsedHours == 1)
                return true;
            else if (elapsedHours > 0)
                return true;
            else if (elapsedMinutes > 29)
                return true;
            else
                return false;
        }

    }

    public static boolean greaterThanXMins(Date startDate, Date endDate, int mins) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

//        System.out.println("startDate : " + startDate);
//        System.out.println("endDate : " + endDate);
//        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weekInMilli = daysInMilli * 7;
        long monthInMilli = weekInMilli * 4;
        long yearInMilli = monthInMilli * 12;


        long elapsedYear = different / yearInMilli;
        different = different % yearInMilli;

        long elapesMonth = different / monthInMilli;
        different = different % monthInMilli;


        long elapsedWeeks = different / weekInMilli;
        different = different % weekInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

//        System.out.printf(
//                "%d year, %d month, %d weeks, %d days, %d hours, %d minutes, %d seconds%n",
//                elapsedYear, elapesMonth, elapsedWeeks, elapsedDays,
//                elapsedHours, elapsedMinutes, elapsedSeconds);


        String txt = "";
        if (elapsedYear == 1)
            return true;
        else if (elapsedYear > 0)
            return true;
        else if (elapesMonth == 1)
            return true;
        else if (elapesMonth > 0)
            return true;
        else if (elapsedWeeks == 1)
            return true;
        else if (elapsedWeeks > 0)
            return true;
        else if (elapsedDays == 1)
            return true;
        else if (elapsedDays > 0)
            return true;
        else {
            if (elapsedHours == 1)
                return true;
            else if (elapsedHours > 0)
                return true;
            else if (elapsedMinutes > mins)
                return true;
            else
                return false;
        }

    }

    public static boolean greaterThanXDays(Date startDate, Date endDate, int days) {

        long different = endDate.getTime() - startDate.getTime();
        System.out.println("=======different=="+different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weekInMilli = daysInMilli * 7;
        long monthInMilli = weekInMilli * 4;
        long yearInMilli = monthInMilli * 12;

        long elapsedYear = different / yearInMilli;
        different = different % yearInMilli;

        long elapesMonth = different / monthInMilli;
        different = different % monthInMilli;

        long elapsedWeeks = different / weekInMilli;
        different = different % weekInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.println("======elapsedDays==="+elapsedDays);

        if (elapsedDays > days)
            return true;
        else
            return false;
    }

    public static boolean greaterThanXSecs(Date startDate, Date endDate, int sec) {

        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;

        long elapsedSeconds = different / secondsInMilli;

        if (elapsedSeconds > sec)
            return true;
        else
            return false;


    }

    //    From String to Date
    public static Date stringToDate(String stringDate) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //From Date to String
    public static void dateToString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateTime = dateFormat.format(date);
//        System.out.println("Current Date Time : " + dateTime);
    }

    public static long daysBetween(Date startDate, Date endDate) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        if (startDate != null && endDate != null) {
            Calendar sDate = getDatePart(startDate);
            Calendar eDate = getDatePart(endDate);

            long daysBetween = 0;
//            if (!sDate.before(eDate))
//                daysBetween = -1;
            while (sDate.before(eDate)) {
                sDate.add(Calendar.DAY_OF_MONTH, 1);
                daysBetween++;
            }
            return daysBetween;
        } else {
            return 0;
        }
    }

    public static long daysBetweenNew(Date startDate, Date endDate) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        if (startDate != null && endDate != null) {
            Calendar sDate = getDatePart(startDate);
            Calendar eDate = getDatePart(endDate);

            long daysBetween = 0;
            while (sDate.before(eDate)) {
                sDate.add(Calendar.DAY_OF_MONTH, 1);
                daysBetween++;
            }
            return daysBetween;
        } else {
            return 0;
        }
    }

    public static Calendar getDatePart(Date date) {
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

        return cal;                                  // return the date part
    }

    public static Calendar getCalendarFromISO(String datestring) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault()) ;
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        try {
            Date date = dateformat.parse(datestring);
            date.setHours(date.getHours() - 1);
            calendar.setTime(date);

            String test = dateformat.format(calendar.getTime());
            Log.e("TEST_TIME", test);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar;
    }
}
