/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This utilities.file is part of org.starnub a Java Wrapper for Starbound.
*
* This above mentioned StarNub software is free software:
* you can redistribute it and/or modify it under the terms
* of the GNU General Public License as published by the Free
* Software Foundation, either version  3 of the License, or
* any later version. This above mentioned CodeHome software
* is distributed in the hope that it will be useful, but
* WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
* the GNU General Public License for more details. You should
* have received a copy of the GNU General Public License in
* this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package utilities.time;


import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents DataAndTimes instance. This will provide formatting support and time calculations
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class DateAndTimes {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final DateAndTimes instance = new DateAndTimes();

    /**
     * This constructor is private - Singleton Pattern
     */
    private DateAndTimes(){}

    /**
     *
     * @return DateAndTimes Singleton Instance
     */
    public static DateAndTimes getInstance() {
        return instance;
    }

    public static DateTime parseFutureTime(String argument) {
        Pattern p = Pattern.compile("\\d+\\D+");
        Matcher m = p.matcher(argument);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        while (m.find()) {
            String group = m.group();
            String intString = group.replaceFirst("\\D+", "");
            int i = Integer.parseInt(intString);
            if (argument.contains("y")) {
                years = i;
            } else if (argument.contains("m")) {
                months = i;
            } else if (argument.contains("w")) {
                weeks = i;
            } else if (argument.contains("d")) {
                days = i;
            } else if (argument.contains("h")) {
                hours = i;
            } else if (argument.contains("min")) {
                minutes = i;
            }
        }
        return getFutureDateTime(years, months, weeks, days, hours, minutes);
    }

    /**
     * Returns a String formatted date, Examples "MMMM dd, yyyy",  "MMMM dd, yyyy '@' HH:mm '- Server Time'", [HH:mm:ss]
     * <p>
     * @see <a href="http://www.joda.org/joda-time/key_format.html">Joda Time Formatting</a>
     * <p>
     * @param format String representing the format
     * @return String with the current time formatted
     */
    public static String getFormattedTimeNow(String format){
        return DateTime.now().toString(DateTimeFormat.forPattern(format));
    }

    /**
     * Returns a String formatted date, Examples "MMMM dd, yyyy",  "MMMM dd, yyyy '@' HH:mm '- Server Time'", [HH:mm:ss]
     * <p>
     * @see <a href="http://www.joda.org/joda-time/key_format.html">Joda Time Formatting</a>
     * <p>
     * @param format String representing the format
     * @param dateTime DateTime representing the time
     * @return String with the future time
     */
    public static String getFormattedDate(String format, DateTime dateTime){
        DateTimeFormatter fmt = DateTimeFormat.forPattern(format);
        return fmt.print(dateTime);
    }

    /**
     * Returns a period which represents the distance between two times
     *
     * @param dateTime1 DateTime representing the first date and time
     * @param dateTime2 DateTime representing the second date and time
     * @return Period representing that distance in time
     */
    public Period getPeriod(DateTime dateTime1, DateTime dateTime2){
        return new Period(dateTime1, dateTime2);
    }

    /**
     *
     * This will calculate out and return a String the length of a period in easy to read format: Y
     *
     * @param duration
     * @param yearsSeparatorFormat
     * @param monthsSeparatorFormat
     * @param weeksSeparatorFormat
     * @param daysSeparatorFormat
     * @param hoursSeparatorFormat
     * @param minutesSeparatorFormat
     * @param secondsSeparatorFormat
     * @return
     */
    public static String getPeriodFormattedFromMilliseconds(long duration, String yearsSeparatorFormat, String monthsSeparatorFormat, String weeksSeparatorFormat, String daysSeparatorFormat, String hoursSeparatorFormat, String minutesSeparatorFormat, String secondsSeparatorFormat){
        Period period = new Period(duration);
        if (period.getYears() > 0) {
            return getPeriodFormat(period, yearsSeparatorFormat, monthsSeparatorFormat, weeksSeparatorFormat, daysSeparatorFormat, hoursSeparatorFormat, minutesSeparatorFormat, secondsSeparatorFormat);
        } else if (period.getMonths() > 0) {
            return getPeriodFormat(period, monthsSeparatorFormat, weeksSeparatorFormat, daysSeparatorFormat, hoursSeparatorFormat, minutesSeparatorFormat, secondsSeparatorFormat);
        } else if (period.getWeeks() > 0) {
            return getPeriodFormat(period, weeksSeparatorFormat, daysSeparatorFormat, hoursSeparatorFormat, minutesSeparatorFormat, secondsSeparatorFormat);
        } else if (period.getDays() > 0) {
            return getPeriodFormat(period, daysSeparatorFormat, hoursSeparatorFormat, minutesSeparatorFormat, secondsSeparatorFormat);
        } else if (period.getHours() > 0) {
            return getPeriodFormat(period, hoursSeparatorFormat, minutesSeparatorFormat, secondsSeparatorFormat);
        } else if (period.getMinutes() > 0) {
            return getPeriodFormat(period, minutesSeparatorFormat, secondsSeparatorFormat);
        } else if (period.getSeconds() > 0) {
            return getPeriodFormat(period, secondsSeparatorFormat);
        } else {
            return null;
        }
    }

    /**
     *
     * @param period
     * @param secondsSeparatorFormat
     * @return
     */
    public static String getPeriodFormat(Period period, String secondsSeparatorFormat){
        if (secondsSeparatorFormat == null) {
            secondsSeparatorFormat = "-";
        }
        PeriodFormatter Formatter = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendSeconds().appendLiteral(secondsSeparatorFormat)
                .toFormatter();
        return Formatter.print(period);

    }

    /**
     *
     * @param period
     * @param minutesSeparatorFormat
     * @param secondsSeparatorFormat
     * @return
     */
    public static String getPeriodFormat(Period period, String minutesSeparatorFormat, String secondsSeparatorFormat){
        if (secondsSeparatorFormat == null) {
            secondsSeparatorFormat = "-";
        }
        PeriodFormatter Formatter = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendMinutes().appendSeparator(minutesSeparatorFormat)
                .appendSeconds().appendLiteral(secondsSeparatorFormat)
                .toFormatter();
        return Formatter.print(period);
    }

    /**
     *
     * @param period
     * @param hoursSeparatorFormat
     * @param minutesSeparatorFormat
     * @param secondsSeparatorFormat
     * @return
     */
    public static String getPeriodFormat(Period period, String hoursSeparatorFormat, String minutesSeparatorFormat, String secondsSeparatorFormat){
        if (secondsSeparatorFormat == null) {
            secondsSeparatorFormat = "-";
        }
        PeriodFormatter Formatter = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendHours().appendSeparator(hoursSeparatorFormat)
                .appendMinutes().appendSeparator(minutesSeparatorFormat)
                .appendSeconds().appendLiteral(secondsSeparatorFormat)
                .toFormatter();
        return Formatter.print(period);
    }

    /**
     *
     * @param period
     * @param daysSeparatorFormat
     * @param hoursSeparatorFormat
     * @param minutesSeparatorFormat
     * @param secondsSeparatorFormat
     * @return
     */
    public static String getPeriodFormat(Period period, String daysSeparatorFormat, String hoursSeparatorFormat, String minutesSeparatorFormat, String secondsSeparatorFormat){
        if (secondsSeparatorFormat == null) {
            secondsSeparatorFormat = "-";
        }
        PeriodFormatter Formatter = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendDays().appendSeparator(daysSeparatorFormat)
                .appendHours().appendSeparator(hoursSeparatorFormat)
                .appendMinutes().appendSeparator(minutesSeparatorFormat)
                .appendSeconds().appendLiteral(secondsSeparatorFormat)
                .toFormatter();
        return Formatter.print(period);
    }

    /**
     *
     * @param period
     * @param weeksSeparatorFormat
     * @param daysSeparatorFormat
     * @param hoursSeparatorFormat
     * @param minutesSeparatorFormat
     * @param secondsSeparatorFormat
     * @return
     */
    public static String getPeriodFormat(Period period, String weeksSeparatorFormat, String daysSeparatorFormat, String hoursSeparatorFormat, String minutesSeparatorFormat, String secondsSeparatorFormat){
        if (secondsSeparatorFormat == null) {
            secondsSeparatorFormat = "-";
        }
        PeriodFormatter Formatter = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendWeeks().appendSeparator(weeksSeparatorFormat)
                .appendDays().appendSeparator(daysSeparatorFormat)
                .appendHours().appendSeparator(hoursSeparatorFormat)
                .appendMinutes().appendSeparator(minutesSeparatorFormat)
                .appendSeconds().appendLiteral(secondsSeparatorFormat)
                .toFormatter();
        return Formatter.print(period);
    }

    /**
     *
     * @param period
     * @param monthsSeparatorFormat
     * @param weeksSeparatorFormat
     * @param daysSeparatorFormat
     * @param hoursSeparatorFormat
     * @param minutesSeparatorFormat
     * @param secondsSeparatorFormat
     * @return
     */
    public static String getPeriodFormat(Period period, String monthsSeparatorFormat, String weeksSeparatorFormat, String daysSeparatorFormat, String hoursSeparatorFormat, String minutesSeparatorFormat, String secondsSeparatorFormat){
        if (secondsSeparatorFormat == null) {
            secondsSeparatorFormat = "-";
        }
        PeriodFormatter Formatter = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendMonths().appendSeparator(monthsSeparatorFormat)
                .appendWeeks().appendSeparator(weeksSeparatorFormat)
                .appendDays().appendSeparator(daysSeparatorFormat)
                .appendHours().appendSeparator(hoursSeparatorFormat)
                .appendMinutes().appendSeparator(minutesSeparatorFormat)
                .appendSeconds().appendLiteral(secondsSeparatorFormat)
                .toFormatter();
        return Formatter.print(period);
    }

    /**
     *
     * @param period
     * @param yearsSeparatorFormat
     * @param monthsSeparatorFormat
     * @param weeksSeparatorFormat
     * @param daysSeparatorFormat
     * @param hoursSeparatorFormat
     * @param minutesSeparatorFormat
     * @param secondsSeparatorFormat
     * @return
     */
    public static String getPeriodFormat(Period period, String yearsSeparatorFormat, String monthsSeparatorFormat, String weeksSeparatorFormat, String daysSeparatorFormat, String hoursSeparatorFormat, String minutesSeparatorFormat, String secondsSeparatorFormat){
        if (secondsSeparatorFormat == null) {
            secondsSeparatorFormat = "-";
        }
        PeriodFormatter Formatter = new PeriodFormatterBuilder()
                .printZeroAlways()
                .minimumPrintedDigits(2)
                .appendYears().appendSeparator(yearsSeparatorFormat)
                .appendMonths().appendSeparator(monthsSeparatorFormat)
                .appendWeeks().appendSeparator(weeksSeparatorFormat)
                .appendDays().appendSeparator(daysSeparatorFormat)
                .appendHours().appendSeparator(hoursSeparatorFormat)
                .appendMinutes().appendSeparator(minutesSeparatorFormat)
                .appendSeconds().appendLiteral(secondsSeparatorFormat)
                .toFormatter();
        return Formatter.print(period);
    }

    /**
     *
     * @param minutes
     * @return
     */
    public static DateTime getFutureDateTime(int minutes){
        return new DateTime().plusMinutes(minutes);
    }

    /**
     *
     * @param hours
     * @param minutes
     * @return
     */
    public static DateTime getFutureDateTime(int hours, int minutes){
        return new DateTime().plusHours(hours).plusMinutes(minutes);
    }

    /**
     *
     * @param days
     * @param hours
     * @param minutes
     * @return
     */
    public static DateTime getFutureDateTime(int days, int hours, int minutes){
        return new DateTime().plusDays(days).plusHours(hours).plusMinutes(minutes);
    }

    /**
     *
     * @param weeks
     * @param days
     * @param hours
     * @param minutes
     * @return
     */
    public static DateTime getFutureDateTime(int weeks, int days, int hours, int minutes){
        return new DateTime().plusWeeks(weeks).plusDays(days).plusHours(hours).plusMinutes(minutes);
    }


    /**
     *
     * @param months
     * @param days
     * @param hours
     * @param minutes
     * @return
     */
    public static DateTime getFutureDateTime(int months, int weeks, int days, int hours, int minutes){
        return new DateTime().plusMonths(months).plusWeeks(weeks).plusDays(days).plusHours(hours).plusMinutes(minutes);
    }

    /**
     *
     * @param years
     * @param months
     * @param days
     * @param hours
     * @param minutes
     * @return
     */
    public static DateTime getFutureDateTime(int years, int months, int weeks, int days, int hours, int minutes){
        return new DateTime().plusYears(years).plusMonths(months).plusWeeks(weeks).plusDays(days).plusHours(hours).plusMinutes(minutes);
    }

}
