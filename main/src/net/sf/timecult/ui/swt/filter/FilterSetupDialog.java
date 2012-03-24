/*
 * Copyright (c) Rustam Vishnyakov, 2009 (dyadix@gmail.com)
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
 * $Id: FilterSetupDialog.java,v 1.3 2009/11/14 13:43:32 dyadix Exp $
 */
package net.sf.timecult.ui.swt.filter;

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
import net.sf.timecult.model.TimeRecordFilter;
import net.sf.timecult.model.TimeRecordFilterFactory;
import net.sf.timecult.ui.swt.SWTDialog;
import net.sf.timecult.ui.swt.SWTMainWindow;
import net.sf.timecult.ui.swt.calendar.CalendarDialog;
import net.sf.timecult.ui.swt.calendar.ICalendarDialogListener;
import net.sf.timecult.util.Formatter;

public class FilterSetupDialog extends SWTDialog implements ICalendarDialogListener {
    
    private Text filterNameField;
    private AdvancedTimeFilterView filterView;
    private SWTMainWindow mainWindow;
    private Text startDateField;
    private Text endDateField;
    
    public FilterSetupDialog(AdvancedTimeFilterView filterView) {
        super(filterView.getMainWindow().getShell(), false);
        this.filterView = filterView;
        this.mainWindow = this.filterView.getMainWindow();
    }


    @Override
    protected Composite createContentPanel(Shell shell) {
        Composite contentPanel = new Composite(shell, SWT.None);
        GridLayout grid = new GridLayout();
        grid.numColumns = 2;
        contentPanel.setLayout(grid);
        
        Label filterLabel = new Label(contentPanel, SWT.None);
        filterLabel.setText(ResourceHelper.getString("filter.name"));
        this.filterNameField = createTextField(contentPanel, "", 100);
        
        Label startDateLabel = new Label(contentPanel, SWT.None);
        startDateLabel.setText(ResourceHelper.getString("filter.startDate"));
        this.startDateField = addDateField(contentPanel);
        
        Label endDateLabel = new Label(contentPanel, SWT.None);
        endDateLabel.setText(ResourceHelper.getString("filter.endDate"));
        this.endDateField = addDateField(contentPanel);
        
        return contentPanel;
    }


    @Override
    protected String getTitle() {
        return ResourceHelper.getString("filter.dialog.title");
    }


    @Override
    protected boolean handleOk() {
        Date startDate = null;
        Date endDate = null;
        String filterName = this.filterNameField.getText();
        try {
            startDate = Formatter.parseDateString(startDateField.getText());
        }
        catch (ParseException e) {
            errorMessage("Invalid date format!");
            return false;
        }
        try {
            endDate = Formatter.parseDateString(endDateField.getText());
        }
        catch (ParseException e) {
            errorMessage("Invalid date format!");
            return false;
        }
        TimeRecordFilter filter = TimeRecordFilterFactory.createCustomFilter(
            filterName,
            startDate,
            endDate);
        this.filterView.addCustomFilter(filter);
        return true;
    }

    private Text addDateField(Composite contentPanel) {
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
        final Text dateField = new Text(dateEntryPanel, SWT.BORDER);
        dateField.setText("");        
        //
        // Add date picker button
        //
        Button datePickerButton = new Button(dateEntryPanel, SWT.None);
        datePickerButton.setImage(this.mainWindow.getIconSet().getIcon("calendar", true));
        datePickerButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent evt) {
                CalendarDialog calDialog = new CalendarDialog(
                    FilterSetupDialog.this.getShell(), dateField);
                calDialog.setListener(FilterSetupDialog.this);
                calDialog.open();
            }
        });
        return dateField;
    }


    public void dateSelected(Calendar data, Text dateField) {
        dateField.setText(Formatter.toDateString(data.getTime()));
    }
}
