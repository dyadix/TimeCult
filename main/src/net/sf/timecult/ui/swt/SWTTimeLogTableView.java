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
 * $Id: SWTTimeLogTableView.java,v 1.26 2011/01/20 15:02:22 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.conf.AppPreferences;
import net.sf.timecult.model.*;
import net.sf.timecult.model.ProjectTreeItem.ItemType;
import net.sf.timecult.ui.swt.timelog.TimeLogEntryEditDialog;
import net.sf.timecult.ui.swt.timelog.TimeLogToolBar;
import net.sf.timecult.util.Formatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class SWTTimeLogTableView {

    private final static String[] titleKeys   = { "", "table.project", "table.task", "table.startDate", "table.startTime",
        "table.duration", "table.notes"   };
    private final static int[]    align    = { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT,
        SWT.LEFT                          };

    private TimeLogToolBar toolBar;
    private Composite contentArea;
    private AppPreferences appPrefs;

    private final static int STATUS_COL_WIDTH = IconSet.ICON_SIZE + 8;
    private final Image normalRecImage;
    private final Image partialRecImage;
    private final Image idleImage;
    private final String[] titles;


    public SWTTimeLogTableView(SWTMainWindow mainWindow) {
        _mainWindow = mainWindow;
        this.appPrefs = AppPreferences.getInstance();
        titles = new String[titleKeys.length];
        titles[0] = "";
        for (int i = 1; i < titleKeys.length; i++) {
            titles[i] = ResourceHelper.getString(titleKeys[i]);
        }
        normalRecImage = this._mainWindow.getIconSet().getIcon(
            "record-normal",
            true);
        partialRecImage = this._mainWindow.getIconSet().getIcon(
            "record-partial",
            true);
        idleImage = this._mainWindow.getIconSet().getIcon(
            "idle",
            true);
        setup();
    }


    private void setup() {
        this.timeLogTab = new TabItem(_mainWindow.getMainTabFolder()
            .getTabs(), SWT.BORDER);
        this.timeLogTab.setText(ResourceHelper.getString("tab.journal"));
        GridData hd = new GridData(GridData.FILL_HORIZONTAL);

        contentArea = new Composite(_mainWindow.getMainTabFolder().getTabs(), SWT.NONE);
        contentArea.setLayoutData(hd);
        GridLayout areaLayout = new GridLayout();
        areaLayout.numColumns = 1;
        areaLayout.marginWidth = 0;
        areaLayout.marginHeight = 0;
        contentArea.setLayout(areaLayout);
        this.timeLogTab.setControl(contentArea);

        this.toolBar = new TimeLogToolBar(this);

        GridData tableLayout = new GridData(GridData.FILL_BOTH);
        tableLayout.grabExcessVerticalSpace = true;
        _table = new Table(contentArea, SWT.FULL_SELECTION | SWT.BORDER
            | SWT.MULTI);
        _table.setLayoutData(tableLayout);
        _table.setLinesVisible(true);
        _table.setHeaderVisible(true);
        _table.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent evt) {
                if (evt.stateMask == SWT.CTRL) {
                    if (evt.keyCode == SWT.INSERT || evt.keyCode == 'c' || evt.keyCode == 'C') {
                        copySelectionToClipboard();
                    }
                    else if (evt.keyCode == 'a' || evt.keyCode == 'A') {
                        SWTTimeLogTableView.this._table.selectAll();
                    }
                    else if (evt.keyCode == 'j' || evt.keyCode == 'J') {
                        joinSelected();
                    }
                }
                else if (evt.stateMask == SWT.ALT) {
                    if (evt.keyCode == SWT.CR) {
                        editSelection();
                    }
                }
                else if (evt.keyCode == SWT.DEL) {
                    removeSelected();
                }
            }


            public void keyReleased(KeyEvent evt) {
                // Do nothing

            }
        });
        _table.addSelectionListener(this.toolBar);

        createPopupMenu();

        for (int i = 0; i < titles.length; i++) {
            TableColumn column = new TableColumn(_table, SWT.NONE);
            column.setAlignment(align[i]);
            column.setResizable(true);
            if (i == 0) {
                column.setWidth(STATUS_COL_WIDTH);
            }
            else {
                column.setWidth(AppPreferences.getInstance().getTimeLogColWidth(i - 1));
            }
            column.setText(titles[i]);
            column.addControlListener(new ControlAdapter() {
                public void controlResized(ControlEvent e) {
                    TableColumn col = (TableColumn) e.getSource();
                    int colIndex = col.getParent().indexOf(col);
                    if (colIndex > 0) {
                        appPrefs.setTimeLogColWidth(
                            colIndex - 1,
                            col.getWidth());
                    }
                }
            });
        }
        _filter = TimeTracker.getInstance().getFilter();
        updateTable();
        _table.setHeaderVisible(true);
    }


    private void createPopupMenu() {
        _popup = new Menu(_table);
        _table.setMenu(_popup);
        MenuItem copyItem = new MenuItem(_popup, SWT.CASCADE);
        copyItem.setText(ResourceHelper.getString("menu.copy") + "\tCtrl+C");
        copyItem.setImage(_mainWindow.getIconSet().getIcon("copy", true));
        copyItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                SWTTimeLogTableView.this.copySelectionToClipboard();
            }
        });
        MenuItem editItem = new MenuItem(_popup, SWT.CASCADE);
        editItem.setText(ResourceHelper.getString("menu.edit") + "\tAlt+Enter");
        editItem.setImage(_mainWindow.getIconSet().getIcon("edit", true));
        editItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                SWTTimeLogTableView.this.editSelection();
            }
        });
        MenuItem deleteItem = new MenuItem(_popup, SWT.CASCADE);
        deleteItem.setText(ResourceHelper.getString("menu.delete") + "\tDel");
        deleteItem.setImage(_mainWindow.getIconSet().getIcon("delete", true));
        deleteItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                SWTTimeLogTableView.this.removeSelected();
            }
        });
        MenuItem joinItem = new MenuItem(_popup, SWT.CASCADE);
        joinItem.setText(ResourceHelper.getString("menu.join") + "\tCtrl+J");
        joinItem.setImage(_mainWindow.getIconSet().getIcon("joinRecords", true));
        joinItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                SWTTimeLogTableView.this.joinSelected();
            }
        });
        new MenuItem(_popup, SWT.SEPARATOR);
        MenuItem selectAllItem = new MenuItem(_popup, SWT.CASCADE);
        selectAllItem.setText(ResourceHelper.getString("menu.selectAll") + "\tCtrl+A");
        selectAllItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                SWTTimeLogTableView.this._table.selectAll();
            }
        });
    }

    public void updateTable() {
        AppPreferences appPrefs = AppPreferences.getInstance();
        _table.setRedraw(false);
        //
        // Keep the current selection
        //
        TimeRecord currTimeRec = null;
        if (_table.getSelectionCount() > 0) {
            currTimeRec = (TimeRecord)(_table.getSelection()[0].getData());
        }
        _table.removeAll();
        combineFilters();
        TimeTracker tracker = TimeTracker.getInstance();
        TimeRecord[] timeRecs = tracker.getWorkspace().getTimeLog()
            .getTimeRecords(_filter);
        TableItem lastItem = null;
        for (int i = 0; i < timeRecs.length; i++) {
            lastItem = new TableItem(_table, SWT.NONE);
            setData(lastItem, timeRecs[i]);
        }
        if (timeRecs.length > 0) {
            _table.setSelection(timeRecs.length - 1);
            _table.showSelection();
        }
        for (int i = 1; i < titles.length; i++) {
            _table.getColumn(i).setWidth(appPrefs.getTimeLogColWidth(i - 1));
        }
        _table.getColumn(0).setWidth(STATUS_COL_WIDTH);
        _table.getColumn(0).setResizable(false);
        _table.setRedraw(true);
        //
        // Restore the selection (if any)
        //
        if (currTimeRec != null) {
            TableItem currSelection = findItem(currTimeRec);
            if (currSelection != null) {
                _table.setSelection(currSelection);
            }
        }
    }

    private void combineFilters() {
        TimeTracker tracker = TimeTracker.getInstance();
        TimeRecordFilter timeFilter = tracker.getWorkspace().getFilter();
        TimeRecordFilter newFilter = null;
        if (timeFilter != null) {
            newFilter = (TimeRecordFilter)timeFilter.clone();
        }
        else {
            newFilter = new TimeRecordFilter();
        }
        if (_filter != null) {
            newFilter.setProject(_filter.getProject());
            newFilter.setTask(_filter.getTask());
        }
        _filter = newFilter;
    }


    /**
     * Searches a table element linked to a given time record, changes it's data
     * to reflect changed time record and selects it.
     * @param timeRec The record to find.
     */
    public void update(TimeRecord timeRec) {
        updateTable();
        TableItem foundItem = findItem(timeRec);
        if (foundItem != null) {
            setData(foundItem, timeRec);
            _table.setSelection(foundItem);
        }
    }


    public void selectItem(TimeRecord timeRec) {
        TableItem foundItem = findItem(timeRec);
        if (foundItem != null) {
            _table.setSelection(foundItem);
        }
        _table.setFocus();
    }


    public void copySelectionToClipboard() {
        TableItem[] items = _table.getSelection();
        if (items == null || items.length == 0) {
            return;
        }
        Clipboard clipboard = new Clipboard(_mainWindow.getShell().getDisplay());
        TextTransfer textTransfer = TextTransfer.getInstance();
        Transfer[] transfers = new Transfer[] { textTransfer };
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < items.length; i++) {
            TimeRecord timeRec = (TimeRecord) items[i].getData();
            TimeRecord filteredRec = TimeUtil.getFilteredTimeRec(_filter, timeRec);
            buf.append(filteredRec.getTask().getId());
            buf.append("\t");
            buf.append(filteredRec.getTask().getName());
            buf.append("\t");
            buf.append(Formatter.toDateTimeString(filteredRec.getStart(),false));
            buf.append("\t");
            buf.append(filteredRec.getDuration().toString());
            buf.append("\t");
            buf.append(timeRec.getNotes());
            if (i < items.length - 1) {
                buf.append("\n");
            }
        }
        Object[] data = new Object[] { buf.toString() };
        clipboard.setContents(data, transfers);
        clipboard.dispose();
    }


    public void editSelection() {
        TableItem[] items = _table.getSelection();
        if (items == null || items.length == 0)
            return;
        TimeRecord timeRec = (TimeRecord) items[0].getData();
        TimeLogEntryEditDialog editDialog = new TimeLogEntryEditDialog(
            _mainWindow, timeRec, false);
        editDialog.open();
    }

    public void addNewEntry() {
        ProjectTreeItem selection = TimeTracker.getInstance().getWorkspace().getSelection();
        if (selection != null && selection instanceof Task) {
            TimeLogEntryEditDialog editDialog = new TimeLogEntryEditDialog(
                _mainWindow, (Task)selection);
            editDialog.open();
        }
    }


    private void setData(TableItem item, TimeRecord timeRec) {
        TimeRecordFilter filter = TimeTracker.getInstance().getFilter();
        TimeRecord filteredRec = TimeUtil.getFilteredTimeRec(filter, timeRec);
        if (timeRec.getTask().getItemType() == ItemType.IDLE_TASK) {
            item.setImage(this.idleImage);
        } else {
            if (filteredRec != timeRec) {
                item.setImage(this.partialRecImage);
            } else {
                item.setImage(this.normalRecImage);
            }
        }
        Project proj = filteredRec.getTask().getProject();
        String projectPath = "N/A";
        if (proj != null) {
            projectPath = proj.getName();
            while (proj.getParent() != null) {
                proj = proj.getParent();
                projectPath = proj.getName() + " -> " + projectPath;
            }
        }

        item.setText(1, projectPath);
        item.setText(2, filteredRec.getTask().toString());
        item.setText(3, Formatter.toDateString(filteredRec.getStart()));
        item.setText(4, Formatter.toTimeString(filteredRec.getStart()));
        item.setText(5, filteredRec.getDuration().toString());
        String notes = timeRec.getNotes();
        if (notes == null)
            notes = "";
        item.setText(6, notes);
        item.setData(timeRec);
    }


    public SWTMainWindow getMainWindow() {
        return this._mainWindow;
    }

    public Table getTable() {
        return this._table;
    }


    public Composite getContentArea() {
        return this.contentArea;
    }

    private TableItem findItem(TimeRecord timeRec) {
        TableItem[] items = _table.getItems();
        TableItem foundItem = null;
        for (int i = 0; i < items.length && foundItem == null; i++) {
            if (items[i].getData() == timeRec) {
                foundItem = items[i];
            }
        }
        return foundItem;
    }

    public void updateOnTreeSelection(Object object) {
        TimeRecordFilter filter = TimeTracker.getInstance().getFilter();
        if (filter == null) {
            _filter = new TimeRecordFilter();
        }
        else {
            _filter = (TimeRecordFilter)filter.clone();
        }
        if (object instanceof Task) {
            _filter.setTask((Task)object);
        }
        else if (object instanceof Project) {
            _filter.setProject((Project)object);
        }
        updateTable();
        this.toolBar.updateOnTreeSelection(object);
    }

    /**
     * Joins all the currently selected records saving the first starting time.
     */
    public void joinSelected() {
        TableItem[] selectedItems = _table.getSelection();
        if (selectedItems != null && selectedItems.length > 0) {

            if (confirmJoin(selectedItems.length, Formatter.toDateTimeString(
                    ((TimeRecord) selectedItems[0].getData()).getStart(), false))) {
                TimeRecord[] recs = new TimeRecord[selectedItems.length];
                for (int i = 0; i < selectedItems.length; i++) {
                    recs[i] = (TimeRecord) (selectedItems[i].getData());
                }
                TimeTracker.getInstance().getWorkspace()
                    .joinTimeRecords(recs);
            }
        }
    }

    /**
     * Removes all the currently selected records (if any).
     */
    public void removeSelected() {
        TableItem[] selectedItems = _table.getSelection();
        if (selectedItems != null && selectedItems.length > 0) {
            if (confirmRemove(selectedItems.length)) {
                TimeRecord[] recs = new TimeRecord[selectedItems.length];
                for (int i = 0; i < selectedItems.length; i++) {
                    recs[i] = (TimeRecord) (selectedItems[i].getData());
                }
                TimeTracker.getInstance().getWorkspace()
                    .removeTimeRecords(recs);
            }
        }
    }


    public boolean confirmRemove(int nrecs) {
        String msg = ResourceHelper.getString("message.confirmRemoveRecs");
        msg = msg.replaceAll("%N%", Integer.toString(nrecs));
        return confirm(msg);
    }

    public boolean confirmJoin(int nrecs, String time) {
        String msg = ResourceHelper.getString("message.confirmJoinRecs");
        msg = msg.replaceAll("%N%", Integer.toString(nrecs));
        msg = msg.replaceAll("%T%", time);
        return confirm(msg);
    }

    public boolean confirm(String msg) {
        MessageBox m = new MessageBox(_mainWindow.getShell(), SWT.ICON_QUESTION | SWT.NO
            | SWT.YES);

        m.setMessage(msg);
        m.setText(ResourceHelper.getString("mainwin.confirmation"));
        int result = m.open();
        return (result == SWT.YES);
    }


    public void select() {
        this.timeLogTab.getParent().setSelection(this.timeLogTab);
    }

    private SWTMainWindow _mainWindow;
    private Table         _table;
    private Menu          _popup;
    private TabItem       timeLogTab;
    private TimeRecordFilter _filter;

}
