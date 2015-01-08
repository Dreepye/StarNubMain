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

package org.starnub.utilities.time;


import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

    public static String getPeriodFormattedFromMilliseconds(long duration, String[] separators){
        return getPeriodFormattedFromMilliseconds(duration, false, 0, separators);
    }

    public static String getPeriodFormattedFromMilliseconds(long duration, boolean capitalized){
        String[] strings = {"y ", "m ", "w ", "d ", "h ", "m ", "s"};
        if (capitalized){
            strings = getCapitalizedStrings("", strings);
        }
        return getPeriodFormattedFromMilliseconds(duration, false, 0, strings);
    }

    public static String getPeriodFormattedFromMilliseconds(long duration, boolean capitalized, String separator){
        String[] strings = {"y", "m", "w", "d", "h", "m", "s"};
        if (capitalized){
            strings = getCapitalizedStrings(separator, strings);
        }
        return getPeriodFormattedFromMilliseconds(duration, false, 0, strings);
    }

    public static String getPeriodFormattedFromMilliseconds(long duration, boolean printZeros, int digitCount, String... separators){
        Period period = new Period(duration).normalizedStandard();
        PeriodFormatterBuilder formatterBuilder = new PeriodFormatterBuilder().minimumPrintedDigits(digitCount);
        if(printZeros){
            formatterBuilder.printZeroIfSupported();
        }
        formatterBuilder
                .appendYears().appendSeparator(separators[0])
                .appendMonths().appendSeparator(separators[1])
                .appendWeeks().appendSeparator(separators[2])
                .appendDays().appendSeparator(separators[3])
                .appendHours().appendSeparator(separators[4])
                .appendMinutes().appendSeparator(separators[5])
                .appendSeconds().appendLiteral(separators[6]);
        return formatterBuilder.toFormatter().print(period);
    }

    public static String getPeriodFormattedFromMillisecondsSuffix(long duration, String... separators){
        return getPeriodFormattedFromMilliseconds(duration, false, 0, separators);
    }

    public static String getPeriodFormattedFromMillisecondsSuffix(long duration){
        String[] strings = {
                " Year, ", " Years, ",
                " Month, ", " Months, ",
                " Week, ", " Weeks, ",
                " Day, ", " Days, ",
                " Hour, ", " Hours, ",
                " Minute, ", " Minutes, ",
                " Second", " Seconds"};
        return getPeriodFormattedFromMillisecondsSuffix(duration, false, 0, strings);
    }

    public static String getPeriodFormattedFromMillisecondsSuffix(long duration, boolean printZeros, int digitCount, String... separators){
        Period period = new Period(duration).normalizedStandard();
        PeriodFormatterBuilder formatterBuilder = new PeriodFormatterBuilder().minimumPrintedDigits(digitCount);
        if(printZeros){
            formatterBuilder.printZeroIfSupported();
        }
        formatterBuilder
                .appendYears().appendSuffix(separators[0], separators[1])
                .appendMonths().appendSuffix(separators[2], separators[3])
                .appendWeeks().appendSuffix(separators[4], separators[5])
                .appendDays().appendSuffix(separators[6], separators[7])
                .appendHours().appendSuffix(separators[8], separators[9])
                .appendMinutes().appendSuffix(separators[10], separators[11])
                .appendSeconds().appendSuffix(separators[12], separators[13]);
        return formatterBuilder.toFormatter().print(period);
    }


    private static String[] getCapitalizedStrings(String separator, String ... strings) {
        int stringsLength = strings.length;
        String[] stringsUpper = new String[stringsLength];
        for (int i = 0; i < stringsLength; i++) {
            String string = strings[i];
            String sConcat = string + separator;
            String upperCase = sConcat.toUpperCase();
            stringsUpper[i] = upperCase;
        }
        return stringsUpper;
    }
}
