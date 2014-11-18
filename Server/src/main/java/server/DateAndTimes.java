/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This file is part of org.starnub a Java Wrapper for Starbound.
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

package server;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Represents StarNubs Date and Time enum singleton.
 * <p>
 * This method will provide date and time calculation and formatting
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class DateAndTimes {

    private static final DateAndTimes instance = new DateAndTimes();

    private DateAndTimes(){}

    public static DateAndTimes getInstance() {
        return instance;
    }

    /**
     * Returns the current time in a formatted String
     * <p>
     * See for formating: http://joda-time.sourceforge.net/apidocs/index.html?org/joda/time/format/DateTimeFormatter.html
     * <p>
     * @param format String representing the format
     * @return String with the current time formatted
     */
    public String getFormattedTimeNow(String format){
        return DateTime.now().toString(DateTimeFormat.forPattern(format));
    }

    /**
     * Returns a String formatted date.
     *
     * @param format String representing the format
     * @param dateTime DateTime representing the time
     * @return String with the future time
     */
    public String getFormattedDate(String format, DateTime dateTime){
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

    public String getPeriodFormattedFromMilliseconds(long duration, String yearsSeparatorFormat, String monthsSeparatorFormat, String weeksSeparatorFormat, String daysSeparatorFormat, String hoursSeparatorFormat, String minutesSeparatorFormat, String secondsSeparatorFormat){
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

    public String getPeriodFormat(Period period, String secondsSeparatorFormat){
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

    public String getPeriodFormat(Period period, String minutesSeparatorFormat, String secondsSeparatorFormat){
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
    public String getPeriodFormat(Period period, String hoursSeparatorFormat, String minutesSeparatorFormat, String secondsSeparatorFormat){
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

    public String getPeriodFormat(Period period, String daysSeparatorFormat, String hoursSeparatorFormat, String minutesSeparatorFormat, String secondsSeparatorFormat){
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

    public String getPeriodFormat(Period period, String weeksSeparatorFormat, String daysSeparatorFormat, String hoursSeparatorFormat, String minutesSeparatorFormat, String secondsSeparatorFormat){
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

    public String getPeriodFormat(Period period, String monthsSeparatorFormat, String weeksSeparatorFormat, String daysSeparatorFormat, String hoursSeparatorFormat, String minutesSeparatorFormat, String secondsSeparatorFormat){
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

    public String getPeriodFormat(Period period, String yearsSeparatorFormat, String monthsSeparatorFormat, String weeksSeparatorFormat, String daysSeparatorFormat, String hoursSeparatorFormat, String minutesSeparatorFormat, String secondsSeparatorFormat){
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

    public DateTime getFutureDateTime(int minutes){
        return new DateTime().plusMinutes(minutes);
    }

    public DateTime getFutureDateTime(int hours, int minutes){
        return new DateTime().plusHours(hours).plusMinutes(minutes);
    }

    public DateTime getFutureDateTime(int days, int hours, int minutes){
        return new DateTime().plusDays(days).plusHours(hours).plusMinutes(minutes);
    }

    public DateTime getFutureDateTime(int months, int days, int hours, int minutes){
        return new DateTime().plusMonths(months).plusDays(days).plusHours(hours).plusMinutes(minutes);
    }

    public DateTime getFutureDateTime(int years, int months, int days, int hours, int minutes){
        return new DateTime().plusYears(years).plusMonths(months).plusDays(days).plusHours(hours).plusMinutes(minutes);
    }

}
