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
 * $Id: CSVExporter.java,v 1.5 2010/09/02 13:39:00 dyadix Exp $
 */
package net.sf.timecult.export;

import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.*;
import net.sf.timecult.util.Formatter;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Exports time log (journal) to a CVS file using a currently set filter.
 */
public class CSVExporter {

    /**
     * Exports workspace data (time records) to the given file.
     * @param ws        The workspace to use.
     * @param fileName  The file name to export data to.
     * @throws IOException
     */
    public static void export(Workspace ws, String fileName) throws IOException {
        File file = new File(fileName);
        OutputStreamWriter writer = new FileWriter(file);
        writer.write("Date,Id,Task Name,Used Time,Notes\n");
        TimeLog timeLog = ws.getTimeLog();
        TimeRecordFilter filter = TimeTracker.getInstance().getFilter();
        TimeRecord timeRecords[] = timeLog.getTimeRecords(filter);
        for (int i = 0; i < timeRecords.length; i++) {
            TimeRecord filteredRec = TimeUtil.getFilteredTimeRec(
                filter,
                timeRecords[i]);
            Task task = filteredRec.getTask();
            writer.write(dateToString(filteredRec.getStart()));
            writer.write(',');
            writer.write(task.getId());
            writer.write(',');
            writer.write(encodeString(task.getName()));
            writer.write(',');
            writer.write(Formatter.toDurationString(filteredRec
                .getDuration().getValue(), true));
            writer.write(',');
            writer.write(encodeString(filteredRec.getNotes()));
            writer.write('\n');
        }
        writer.close();
    }

    private static String dateToString(Date date) {
        DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd");
        return dtf.format(date);
    }
    
    private static String encodeDoubleQuotes(String input) {
        return input.replace("\"", "\"\"");
    }
    
    private static String encodeString(String input) {
        if (input == null) return "\"\"";
        StringBuffer buf = new StringBuffer();
        boolean enclosedInQuotes = false;
        if (input.contains(",")) {
            buf.append('"');
            enclosedInQuotes = true;
        }
        if (input.contains("\"")) {
            buf.append(encodeDoubleQuotes(input));
        }
        else {
            buf.append(input);
        }
        if (enclosedInQuotes) {
            buf.append('"');
        }
        return buf.toString();
    }
}
