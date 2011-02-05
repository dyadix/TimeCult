/*
 * Copyright (c) Rustam Vishnyakov, 2005-2009 (dyadix@gmail.com)
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * $Id: TimeRecordFilterFactory.java,v 1.4 2009/09/18 18:39:48 dyadix Exp $ 
 */
package net.sf.timecult.model;

import java.util.Calendar;
import java.util.Date;

import net.sf.timecult.util.Formatter;

/**
 * A helper class to create built-in and custom filters.
 */
public class TimeRecordFilterFactory {

    /*
     * Built-in filters' labels
     */
    public static final String ALL        = "all";
    public static final String TODAY      = "today";
    public static final String THIS_WEEK  = "thisWeek";
    public static final String THIS_MONTH = "thisMonth";
    public static final String THIS_YEAR  = "thisYear";
    
    public static String[] getFilterLabels() {
        return new String[] {ALL, TODAY, THIS_WEEK, THIS_MONTH, THIS_YEAR};
    }
    
    public static TimeRecordFilter createFilter(String label) {
        if (TODAY.equals(label)) {
            return createTodayFilter();
        }
        else if (THIS_WEEK.equals(label)) {
            return createThisWeekFilter();
        }
        else if (THIS_MONTH.equals(label)) {
            return createThisMonthFilter();
        }
        else if (THIS_YEAR.equals(label)) {
            return createThisYearFilter();
        }
        return null;
    }
    
    private static TimeRecordFilter createTodayFilter() {
        TimeRecordFilter todayFilter = new TimeRecordFilter(TODAY);
        Calendar dayStart = Calendar.getInstance();
        dayStart.set(Calendar.HOUR_OF_DAY, 0);
        dayStart.set(Calendar.MINUTE, 0);
        dayStart.set(Calendar.SECOND, 0);
        dayStart.set(Calendar.MILLISECOND, 0);
        Calendar dayEnd = (Calendar) dayStart.clone();
        dayEnd.add(Calendar.HOUR_OF_DAY, 24);
        todayFilter.setSinceDate(dayStart.getTime());
        todayFilter.setToDate(dayEnd.getTime());
        return todayFilter;
    }


    private static TimeRecordFilter createThisWeekFilter() {
        TimeRecordFilter filter = new TimeRecordFilter(THIS_WEEK);
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_WEEK, startTime.getFirstDayOfWeek());
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.DATE, 7);
        filter.setSinceDate(startTime.getTime());
        filter.setToDate(endTime.getTime());
        return filter;
    }


    private static TimeRecordFilter createThisMonthFilter() {
        TimeRecordFilter thisMonthFilter = new TimeRecordFilter(THIS_MONTH);
        Calendar monthStart = Calendar.getInstance();
        monthStart.set(Calendar.DAY_OF_MONTH, 1);
        monthStart.set(Calendar.HOUR_OF_DAY, 0);
        monthStart.set(Calendar.MINUTE, 0);
        monthStart.set(Calendar.SECOND, 0);
        monthStart.set(Calendar.MILLISECOND, 0);
        Calendar monthEnd = (Calendar) monthStart.clone();
        monthEnd.roll(Calendar.MONTH, true);
        if (monthStart.after(monthEnd)) {
            monthEnd.roll(Calendar.YEAR, true);
        }
        thisMonthFilter.setSinceDate(monthStart.getTime());
        thisMonthFilter.setToDate(monthEnd.getTime());
        return thisMonthFilter;
    }


    private static TimeRecordFilter createThisYearFilter() {
        TimeRecordFilter thisYearFilter = new TimeRecordFilter(THIS_YEAR);
        Calendar yearStart = Calendar.getInstance();
        yearStart.set(Calendar.MONTH, 0);
        yearStart.set(Calendar.DAY_OF_MONTH, 1);
        yearStart.set(Calendar.HOUR_OF_DAY, 0);
        yearStart.set(Calendar.MINUTE, 0);
        yearStart.set(Calendar.SECOND, 0);
        yearStart.set(Calendar.MILLISECOND, 0);
        Calendar yearEnd = (Calendar) yearStart.clone();
        yearEnd.roll(Calendar.YEAR, true);
        thisYearFilter.setSinceDate(yearStart.getTime());
        thisYearFilter.setToDate(yearEnd.getTime());
        return thisYearFilter;
    }
    
    
    /**
     * Create a custom filter for the given name, start date/time and end date/time. Filter's
     * end time will be set to the end of the day specified by end date/time. For example, if
     * startDate = endDate, the filter will select all the records which belong to that date.
     * In general filter label will get the following format: {name} [{startDate}..{endDate}].
     * 
     * @param name      The filter name.
     * @param startDate The start date.
     * @param endDate   The end date.
     * @return
     */
    public static TimeRecordFilter createCustomFilter(String name, Date startDate, Date endDate) {
        String label = createCustomFilterLabel(name, startDate, endDate);
        TimeRecordFilter filter = new TimeRecordFilter(label);
        filter.setSinceDate(startDate);
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(endDate);
        endOfDay.add(Calendar.HOUR_OF_DAY, 24);
        filter.setToDate(endOfDay.getTime());
        filter.setCustom(true);
        return filter;
    }
    
    
    private static String createCustomFilterLabel(String name, Date startDate, Date endDate) {
        StringBuffer filterLabel = new StringBuffer();
        if (name != null && name.length() != 0) {
            filterLabel.append(name).append(' ');
        }
        filterLabel.append('[');
        if (startDate != null) {
            filterLabel.append(Formatter.toDateString(startDate));
        }
        if (endDate != null && !endDate.equals(startDate)) {
            filterLabel.append("..");
            filterLabel.append(Formatter.toDateString(endDate));
        }
        filterLabel.append(']');
        return filterLabel.toString();
    }
}
