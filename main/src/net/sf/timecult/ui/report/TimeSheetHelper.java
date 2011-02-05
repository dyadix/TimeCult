/*
 * Copyright (c) Rustam Vishnyakov, 2008 (dyadix@gmail.com)
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
 * $Id: TimeSheetHelper.java,v 1.2 2009/09/15 18:55:06 dyadix Exp $
 */
package net.sf.timecult.ui.report;

import java.util.Calendar;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.model.Project;
import net.sf.timecult.model.Project.SortCriteria;
import net.sf.timecult.ui.swt.calendar.CalendarDialog;
import net.sf.timecult.ui.swt.calendar.ICalendarDialogListener;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Allows to select the end date for the time sheet and opens the 
 * time sheet. In other words works as a primitive time sheet wizard.
 */
public class TimeSheetHelper implements ICalendarDialogListener {

    private Shell shell;
    private Project project; 
    private String title;
    private CalendarDialog calDlg;
    
    /**
     * Construcs a new time sheet helper.
     * @param shell     The parent shell (for dialogs).
     * @param project   The project for which the time sheet is created.
     */
    public TimeSheetHelper(Shell shell, Project project) {
        this.shell = shell;
        this.project = project;
        this.title = project.getName() + " - "
            + ResourceHelper.getString("dialog.quickTimesheet");
    }
    

    /**
     * Opens a calendar dialog to choose the end date. Registers
     * itself as a listener.
     */
    public void openTimeSheet() {
        this.calDlg = new CalendarDialog(this.shell, null);
        this.calDlg.setListener(this);
        this.calDlg.open();        
    }


    /**
     * Opens the time sheet using a selected date.
     */
    public void dateSelected(Calendar data, Text dateField) {
        this.calDlg.getShell().setVisible(false);
        TimeSheetDialog timeSheet = new TimeSheetDialog(this.shell, this.title, data.getTime());
        timeSheet.addItems(project
            .getSubprojects(SortCriteria.BY_NAME));
        timeSheet.addItems(project.getTasks(SortCriteria.BY_NAME));
        timeSheet.open();       
    }
}
