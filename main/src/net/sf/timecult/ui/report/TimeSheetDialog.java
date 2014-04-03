/*
 * Copyright (c) Rustam Vishnyakov, 2007-2008 (dyadix@gmail.com)
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
 * $Id: TimeSheetDialog.java,v 1.4 2008/04/21 19:05:09 dyadix Exp $
 */
package net.sf.timecult.ui.report;

import java.text.SimpleDateFormat;
//import java.util.Calendar;
import java.util.Date;

import net.sf.timecult.model.TotalsCalculator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.Duration;
import net.sf.timecult.ui.swt.SWTDialog;

public class TimeSheetDialog extends SWTDialog {
    
    private TimeSheet timeSheet;
    private String title;
    private Shell shell;

    public TimeSheetDialog(Shell parent, String title, Date endDate) {
        super(parent, false);
        //Date endDate = Calendar.getInstance().getTime();
        this.timeSheet = new TimeSheet(TimeTracker.getInstance().getWorkspace()
            .getTimeLog(), endDate, 7);
        this.title = title;
        this.shell = parent.getShell();
    }
    
    public void addItems(TotalsCalculator[] items) {
        for (int i = 0; i < items.length; i++) {
            if (this.timeSheet.getTimeUsed(items[i]).getValue() != 0) {
                this.timeSheet.addItem(items[i]);
            }
        }
    }

    @Override
    protected Composite createContentPanel(Shell shell) {
        shell.setSize(800,600);
        Composite pane = new Composite(shell, SWT.None);
        GridLayout grid = new GridLayout();
        grid.numColumns = 1;
        pane.setLayout(grid);
        
        GridData tableLayoutData = new GridData(GridData.FILL_HORIZONTAL
            | GridData.VERTICAL_ALIGN_FILL);
        tableLayoutData.heightHint = 200;        
        Table table = new Table(pane, SWT.FULL_SELECTION | SWT.BORDER
            | SWT.MULTI);
        table.setLayoutData(tableLayoutData);
        table.setHeaderVisible(true);
        //table.setLinesVisible(true);
        Date dates[] = this.timeSheet.getDates();
        TableColumn itemCol = new TableColumn(table, SWT.NONE);
        itemCol.setText(ResourceHelper.getString("message.item"));
        itemCol.setWidth(200);
        for (int i = 0; i < dates.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("E, dd MMM");
            column.setText(dateFormatter.format(dates[i]));
            column.setWidth(80);
            column.setAlignment(SWT.RIGHT);
        }
        TableColumn totalCol = new TableColumn(table, SWT.NONE);
        totalCol.setText(ResourceHelper.getString("message.total"));
        totalCol.setWidth(80);
        totalCol.setAlignment(SWT.RIGHT);
        addTableData(shell, table);
        return pane;
    }

    @Override
    protected String getTitle() {
        return this.title;
    }

    /*
     * Just exit
     */
    @Override
    protected boolean handleOk() {
        return true;
    }
    
    private void addTableData(Shell shell, Table table) {
        TotalsCalculator items[] = timeSheet.getItems();
        Date dates[] = this.timeSheet.getDates();
        long totals[] = new long[dates.length];
        boolean odd = true;
        for (int i = 0; i < items.length; i++) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, items[i].toString());
            for (int j = 0; j < dates.length; j++) {
                Duration totalDuration = this.timeSheet.getTimeUsed(i, dates[j]);
                totals[j] += totalDuration.getValue();
                item.setText(j + 1, totalDuration.toString());
            }
            item.setText(dates.length + 1, this.timeSheet.getTimeUsed(i).toString());
            Font f = makeBoldFont(shell, item.getFont().getFontData());
            item.setFont(dates.length + 1, f);
            if(!odd) {
                item.setBackground(new Color(this.shell.getDisplay(), 235, 235, 210));
            }
            odd = !odd;
        }
        TableItem totalsItem = new TableItem(table, SWT.NONE);
        totalsItem.setText(0, ResourceHelper.getString("message.total"));
        totalsItem.setFont(makeBoldFont(shell, totalsItem.getFont().getFontData()));
        long grandTotal = 0;
        for (int j = 0; j < dates.length; j++) {
            totalsItem.setText(j + 1, new Duration(totals[j]).toString());
            grandTotal += totals[j];
        }
        totalsItem.setText(dates.length + 1, new Duration(grandTotal).toString());
    }
    
    private Font makeBoldFont(Shell shell, FontData[] f) {
        for(int i = 0; i < f.length; i ++) {
            f[i].setStyle(SWT.BOLD);
        }
        Font fn = new Font(shell.getDisplay(), f);
        return fn;
    }

    @Override
    protected void createButtons(Composite buttonPanel) {
        GridData buttonLayout = new GridData(GridData.FILL);
        buttonLayout.widthHint = 80;
        Button closeButton = new Button(buttonPanel, SWT.FLAT );
        closeButton.setLayoutData(buttonLayout);
        closeButton.setText(ResourceHelper.getString("button.close"));
        closeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                getShell().setVisible(false);
            }
        });
    }
    
    

}
