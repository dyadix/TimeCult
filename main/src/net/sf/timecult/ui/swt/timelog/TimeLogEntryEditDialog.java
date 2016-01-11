/*
 * Copyright (c) Rustam Vishnyakov, 2007-2010 (dyadix@gmail.com)
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
 * $Id: TimeLogEntryEditDialog.java,v 1.14 2010/04/26 09:14:47 dyadix Exp $
 */
package net.sf.timecult.ui.swt.timelog;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.Duration;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.TimeRecord;
import net.sf.timecult.model.Workspace;
import net.sf.timecult.ui.swt.SWTDialog;
import net.sf.timecult.ui.swt.SWTMainWindow;
import net.sf.timecult.ui.swt.calendar.CalendarDialog;
import net.sf.timecult.ui.swt.calendar.ICalendarDialogListener;
import net.sf.timecult.util.Formatter;

public class TimeLogEntryEditDialog extends SWTDialog implements ICalendarDialogListener {
    
    private TimeRecord timeRec;
    private Text startDateField;
    private Text startTimeField;
    private Text durationField;
    private Text notesField;
    private SWTMainWindow mainWindow;
    private boolean isNew = true;
    private Workspace workspace = TimeTracker.getInstance().getWorkspace();
    
    //
    // A temporary hardcoded time precision.
    // TODO: Replace with configurable value.
    //
    private final static long TIME_PRECISION_MS = 600000; // 10 minutes 
    
    
    public TimeLogEntryEditDialog(SWTMainWindow mainWindow, Task task) {
        super(mainWindow.getShell(), true);
        this.mainWindow = mainWindow;
        this.timeRec = new TimeRecord(task, getDefaultStartTime(), 0, "");
        this.timeRec = workspace.createRecord(task, getDefaultStartTime(), 0, "", true);
    }

    public TimeLogEntryEditDialog(SWTMainWindow mainWindow, TimeRecord timeRec, boolean isNew) {        
        super(mainWindow.getShell(), true);
        this.mainWindow = mainWindow;
        this.timeRec = timeRec;
        this.isNew = isNew;
    }

    @Override
    protected Composite createContentPanel(Shell shell) {
        Composite contentPanel = new Composite(shell, SWT.None);
        GridLayout grid = new GridLayout();
        grid.numColumns = 2;
        contentPanel.setLayout(grid);
        
        Label taskLabel = new Label(contentPanel, SWT.None);
        taskLabel.setText(ResourceHelper.getString("table.task")
            + ":");
        Text taskField = createTextField(contentPanel, this.timeRec.getTask().getName(), 400);
        taskField.setEditable(false);

        Label startDateLabel = new Label(contentPanel, SWT.None);
        startDateLabel.setText(ResourceHelper.getString("table.startDate")
            + ":");
        addStartDateField(contentPanel);

        Label startTimeLabel = new Label(contentPanel, SWT.None);
        startTimeLabel.setText(ResourceHelper.getString("table.startTime")
            + ":");
        /*
        startTimeField = createTextField(contentPanel, Formatter
            .toTimeString(timeRec.getStart()), 50);
        */
        createStartTimeField(contentPanel);

        Label durationLabel = new Label(contentPanel, SWT.None);
        durationLabel.setText(ResourceHelper.getString("table.duration") + ":");
        createDurationField(contentPanel);
        
        Label notesLabel = new Label(contentPanel, SWT.None);
        notesLabel.setText(ResourceHelper.getString("table.notes") + ":");
        notesField = createTextField(contentPanel, timeRec.getNotes(), 400);
        notesField.addKeyListener(getDefaultKeyListener());
        notesField.setFocus();
        return contentPanel;
    }

    @Override
    protected String getTitle() {
        if (isNew) {
            return ResourceHelper.getString("dialog.addRecord");
        }
        else {
            return ResourceHelper.getString("dialog.editRecord");
        }
    }

    @Override
    protected boolean handleOk() {
        Date date;
        Date time;
        Duration duration;
        try {
            date = Formatter.parseDateString(startDateField.getText());
        }
        catch (ParseException e) {
            errorMessage(ResourceHelper.getString("message.invalidDate") + " " 
                + Formatter.toDateString(timeRec.getStart()));
            return false;
        }
        try {
            time = Formatter.parseTimeString(startTimeField.getText());
        }
        catch (ParseException e) {
            errorMessage(ResourceHelper.getString("message.invalidTime") + " "
                + Formatter.toTimeString(timeRec.getStart()));
            return false;
        }
        try {
            duration = Formatter.parseDurationString(durationField.getText());
        }
        catch (ParseException e) {
            errorMessage(ResourceHelper.getString("message.invalidDuration") + " "
                + Formatter.toDurationString(timeRec.getDuration().getValue(), true));
            return false;
        }
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);
        dateCal.add(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateCal.add(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateCal.add(Calendar.SECOND, timeCal.get(Calendar.SECOND));
        timeRec.setStart(dateCal.getTime());
        timeRec.setNotes(notesField.getText());
        timeRec.setDuration(duration.getValue());
        if (this.isNew) {
            TimeTracker.getInstance().getWorkspace().recordTime(timeRec);
        }
        else {
            TimeTracker.getInstance().getWorkspace().updateTimeRecord(timeRec);
        }
        //this.mainWindow.getTimeLogView().selectItem(this.timeRec);
        return true;
    }             
    
    
    private void addStartDateField(Composite contentPanel) {
        //
        // Create date entry panel
        //
        Composite dateEntryPanel = new Composite(contentPanel, SWT.None);
        GridLayout gl = new GridLayout();
        dateEntryPanel.setLayout(gl);
        gl.numColumns = 2;
        gl.marginWidth = 0;
        //
        // Add date entry field (text)
        //
        GridData gd = new GridData();
        gd.widthHint = 80;        
        startDateField = new Text(dateEntryPanel, SWT.BORDER);
        startDateField.setText(Formatter.toDateString(timeRec.getStart()));        
        //
        // Add date picker button
        //
        Button datePickerButton = new Button(dateEntryPanel, SWT.None);
        datePickerButton.setImage(this.mainWindow.getIconSet().getIcon("calendar", true));
        datePickerButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent evt) {
                CalendarDialog calDialog = new CalendarDialog(
                    TimeLogEntryEditDialog.this.getShell(), TimeLogEntryEditDialog.this.startDateField);
                calDialog.setListener(TimeLogEntryEditDialog.this);
                calDialog.open();
            }
        });
    }
    
    private void createDurationField(Composite contentPanel) {
        //
        // Create panel
        //
        Composite durationPanel = new Composite(contentPanel, SWT.None);
        GridLayout gl = new GridLayout();
        durationPanel.setLayout(gl);
        gl.numColumns = 3;
        gl.marginWidth = 0;
        //
        // Add duration field (text)
        //
        GridData gd = new GridData();
        gd.widthHint = 50;
        this.durationField = new Text(durationPanel, SWT.BORDER);
        this.durationField.setText(Formatter.toDurationString(timeRec
            .getDuration().getValue(), true));        
        this.durationField.setLayoutData(gd);
        //
        // Add increment button
        //
        Button incButton = new Button(durationPanel, SWT.None);
        incButton.setImage(this.mainWindow.getIconSet().getIcon("increase", true));
        incButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                changeDuration(+1);
            }
        });       
        //
        // Add decrement button
        //
        Button decButton = new Button(durationPanel, SWT.None);
        decButton.setImage(this.mainWindow.getIconSet().getIcon("decrease", true));
        decButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                changeDuration(-1);
            }
        });
    }
    
    
    private void createStartTimeField(Composite contentPanel) {
        //
        // Create panel
        //
        Composite startTimePanel = new Composite(contentPanel, SWT.None);
        GridLayout gl = new GridLayout();
        startTimePanel.setLayout(gl);
        gl.numColumns = 3;
        gl.marginWidth = 0;
        //
        // Add start time field (text)
        //
        GridData gd = new GridData();
        gd.widthHint = 50;
        this.startTimeField = new Text(startTimePanel, SWT.BORDER);
        this.startTimeField.setText(Formatter.toTimeString(this.timeRec.getStart()));
        this.startTimeField.setLayoutData(gd);
        //
        // Add increment button
        //
        Button incButton = new Button(startTimePanel, SWT.None);
        incButton.setImage(this.mainWindow.getIconSet().getIcon(
            "increase",
            true));
        incButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                changeStartTime(+1);
            }
        });
        //
        // Add decrement button
        //
        Button decButton = new Button(startTimePanel, SWT.None);
        decButton.setImage(this.mainWindow.getIconSet().getIcon(
            "decrease",
            true));
        decButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                changeStartTime(-1);
            }
        });
    }


    public void dateSelected(Calendar data, Text dateField) {
        this.startDateField.setText(Formatter.toDateString(data.getTime()));        
    }
    
    
    private void changeDuration(int n) {
        Duration duration;
        try {
            duration = Formatter.parseDurationString(durationField.getText());
        }
        catch (ParseException e) {
            duration = new Duration(0);
        }
        if (n > 0) {
            duration.incTo(TIME_PRECISION_MS * n);
        }
        else {
            duration.decTo(-TIME_PRECISION_MS * n);
        }
        this.durationField.setText(Formatter.toDurationString(duration
            .getValue(), true));
    }
    
        
    private void changeStartTime(int n) {
        long timeMs;
        Date time;
        try {
            time = Formatter.parseTimeString(startTimeField.getText());
        }
        catch (ParseException e) {
            time = Calendar.getInstance().getTime();
        }
        timeMs = time.getTime();
        if (n > 0) {
            timeMs = (timeMs / TIME_PRECISION_MS + 1) * TIME_PRECISION_MS;
        }
        else {
            long newTime = (timeMs / TIME_PRECISION_MS) * TIME_PRECISION_MS;
            if (newTime == timeMs) {
                newTime -= TIME_PRECISION_MS;
            }
            timeMs = newTime;
        }
        this.startTimeField.setText(Formatter.toTimeString(new Date(timeMs)));
    }

    
    //
    // Return a default time to be used when a new time record is added manually.
    // If there are today's records, use the latest record to find out a start
    // time for the next record. Otherwise use current time.
    //
    private Date getDefaultStartTime() {
        Date startTime = Calendar.getInstance().getTime();
        Date lastRecorded = null;
        TimeRecord timeRecords[] = TimeTracker.getInstance().getWorkspace()
            .getTimeLog().getTimeRecords(null);
        if (timeRecords != null && timeRecords.length > 0) {
            lastRecorded = timeRecords[timeRecords.length - 1].getEnd();
        }
        if (lastRecorded != null && isSameDay(lastRecorded, startTime)) {
            startTime = lastRecorded;
        }
        return startTime;
    }
    
    
    //
    // Test if the two dates represent the same day.
    //
    private boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
            && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }
    

}
