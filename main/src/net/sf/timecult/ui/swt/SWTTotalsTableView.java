/*
 * Copyright (c) Rustam Vishnyakov, 2007 (dyadix@gmail.com)
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
 * $Id: SWTTotalsTableView.java,v 1.9 2009/11/21 13:58:38 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.conf.AppPreferences;
import net.sf.timecult.model.*;
import net.sf.timecult.model.TotalsCalculator;
import net.sf.timecult.util.Formatter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

public class SWTTotalsTableView {

    private static final String[] titles = {
            "table.object",
            "table.object",
            "table.totals.new",
            "table.totals.closed",
            "table.totals.open",
            "table.duration"};
    private static final int[]    align  = {SWT.RIGHT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT};

    private AppPreferences appPrefs;

    public SWTTotalsTableView(SWTMainWindow mainWindow) {
        _mainWindow = mainWindow;
        this.appPrefs = AppPreferences.getInstance();
        setup();
    }

    private void setup() {
        TabItem totalsTab = new TabItem(_mainWindow.getMainTabFolder()
                .getTabs(), SWT.NONE);
        totalsTab.setText(ResourceHelper.getString("tab.summary"));
        _table = new Table(_mainWindow.getMainTabFolder().getTabs(),
                SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
        totalsTab.setControl(_table);
        _table.setLinesVisible(true);
        _table.setHeaderVisible(true);
        _table.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent evt) {
                if (evt.stateMask == SWT.CTRL) {
                    if (evt.keyCode == SWT.INSERT || evt.keyCode == 'c' || evt.keyCode == 'C') {
                        copySelectionToClipboard();
                    } else if (evt.keyCode == 'a' || evt.keyCode == 'A') {
                        _table.selectAll();
                    }
                }
            }

            public void keyReleased(KeyEvent evt) {
                // TODO Auto-generated method stub

            }
        });
        createPopupMenu();
        for (int i = 0; i < titles.length; i++) {
            TableColumn column = new TableColumn(_table, SWT.NONE, i);
            column.setAlignment(align[i]);
            column.setText(ResourceHelper.getString(titles[i]));
            column.setWidth(this.appPrefs.getTotalsColWidth(i));
            column.addControlListener(new ControlAdapter() {
                public void controlResized(ControlEvent e) {
                    TableColumn col = (TableColumn) e.getSource();
                    appPrefs.setTotalsColWidth(
                            col.getParent().indexOf(col),
                            col.getWidth());
                }
            });
        }
        _table.getColumn(0).setResizable(false);
        updateTable();
    }

    public void updateTable() {
		_table.removeAll();
		if (_mainWindow.getProjectTreeView().getTree().getSelectionCount() > 0) {
			TreeItem selectedItem = _mainWindow.getProjectTreeView().getTree()
					.getSelection()[0];
			if (selectedItem != null) {
				_timeLog = TimeTracker.getInstance().getWorkspace()
						.getTimeLog();
                if (selectedItem.getData() instanceof Project) {
                    _table.getColumn(1).setText(ResourceHelper.getString(titles[1]));
                    addChildNodes(selectedItem);
                }
                /*
                else if (selectedItem.getData() instanceof Task) {
                    _table.getColumn(1).setText(ResourceHelper.getString("table.startDate"));
                    addRecords((Task)selectedItem.getData());
                }
                */
				if (selectedItem.getData() != null
						&& selectedItem.getData() instanceof TotalsCalculator) {
					addTableItem((TotalsCalculator) selectedItem.getData(), true);
				}
			}
		}
        for (int i = 0; i < titles.length; i++) {
            _table.getColumn(i).setWidth(this.appPrefs.getTotalsColWidth(i));
        }
    }


    private void addRecords(Task task) {
        TimeRecordFilter filter = null;
        if (TimeTracker.getInstance().getFilter() != null) {
            filter = (TimeRecordFilter) TimeTracker.getInstance().getFilter()
                .clone();
        }
        else {
            filter = new TimeRecordFilter();
        }
        filter.setTask(task);
        filter.setGroupByDate(true);
        TimeRecord[] records = _timeLog.getTimeRecords(filter);
        for (int i = 0; i < records.length; i++) {
            TableItem item = new TableItem(_table, SWT.NONE);
            item.setText(1, Formatter.toDateString(records[i].getStart()));
            item.setText(2, records[i].getDuration().toString());
        }
    }


	private void addChildNodes(TreeItem selectedItem) {
        int nChildren = selectedItem.getItemCount();
        for (int i = 0; i < nChildren; i++) {
            TreeItem child = selectedItem.getItem(i);
            Object data = child.getData();
            if (data != null && data instanceof TotalsCalculator) {
                addTableItem((TotalsCalculator)data, false);
            }
        }
    }

	public void updateOnSelection(Object selection) {
		updateTable();
	}

	private void addTableItem(TotalsCalculator data, boolean highlight) {
		StringBuffer buf = new StringBuffer();
		TableItem item = new TableItem(_table, SWT.NONE);
        Totals totals = data.getTotals(_timeLog, TimeTracker.getInstance().getFilter());
		if (highlight) {
            buf.append(ResourceHelper.getString("message.total") + " (");
        }
		buf.append(data.toString());
		if (highlight) {
			buf.append(")");
		}
        int col = 0;
		item.setText(++col, buf.toString());
        item.setText(++col, totals.getNewItems() > 0 ? Integer.toString(totals.getNewItems()) : "");
        item.setText(++col, totals.getClosedItems() > 0 ? Integer.toString(totals.getClosedItems()) : "");
        item.setText(++col, totals.getOpenItems() > 0 ? Integer.toString(totals.getOpenItems()) : "");
        item.setText(++col, totals.getDuration().toString());
		if (highlight) {
			FontData[] f = item.getFont().getFontData();
            for (FontData aF : f) {
                aF.setStyle(SWT.BOLD);
            }
            Font fn = new Font(_mainWindow.getShell().getDisplay(), f);
            item.setFont(fn);
		}
        item.setData(data);
	}

    public void copySelectionToClipboard() {
        TableItem[] items = _table.getSelection();
        Clipboard clipboard = new Clipboard(_mainWindow.getShell().getDisplay());
        TextTransfer textTransfer = TextTransfer.getInstance();
        Transfer[] transfers = new Transfer[] { textTransfer };
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < items.length; i++) {
            Object dataObject = items[i].getData();
            if (dataObject instanceof TotalsCalculator && dataObject instanceof ProjectTreeItem) {
                ProjectTreeItem pti = (ProjectTreeItem) dataObject;
                buf.append(pti.getId());
                buf.append("\t");
                buf.append(pti.getName());
                buf.append("\t");
                TotalsCalculator totalsCalculator = (TotalsCalculator) dataObject;
                buf.append(totalsCalculator.getTotals(_timeLog,
                        TimeTracker.getInstance().getFilter()).getDuration().toString());
                if (i < items.length - 1) {
                    buf.append("\n");
                }
            }
        }
        if (buf.length() != 0) {
            Object[] data = new Object[] { buf.toString() };
            clipboard.setContents(data, transfers);
            clipboard.dispose();
        }
    }

    private void createPopupMenu() {
        _popup = new Menu(_table);
        _table.setMenu(_popup);
        MenuItem copyItem = new MenuItem(_popup, SWT.CASCADE);
        copyItem.setText(ResourceHelper.getString("menu.copy") + "\tCtrl+C");
        copyItem.setImage(_mainWindow.getIconSet().getIcon("copy", true));
        copyItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                copySelectionToClipboard();
            }
        });
        new MenuItem(_popup, SWT.SEPARATOR);
        MenuItem selectAllItem = new MenuItem(_popup, SWT.CASCADE);
        selectAllItem.setText("Select All" + "\tCtrl+A"); //TODO: LOCALIZE
        selectAllItem.setImage(_mainWindow.getIconSet().getIcon("copy", true));
        selectAllItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                _table.selectAll();
            }
        });
    }

	private SWTMainWindow _mainWindow;
    private Table         _table;
    private TimeLog       _timeLog;
    private Menu          _popup;
}

