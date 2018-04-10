/*
 * Copyright (c) Rustam Vishnyakov, 2005-2010 (dyadix@gmail.com)
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
 * $Id: SWTMainWindow.java,v 1.34 2010/09/25 21:14:27 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import net.sf.timecult.AppInfo;
import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.conf.AppPreferences;
import net.sf.timecult.model.Activity;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.Workspace;
import net.sf.timecult.ui.swt.filter.AdvancedTimeFilterView;
import net.sf.timecult.ui.swt.notifications.NotificationManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;

public class SWTMainWindow {

    private final Display display;
    private Shell _shell;
    private SWTProjectTreeView  _projTreeView;
    private SashForm            _treeTabSash;
    private SWTMainTabFolder    _mainTabFolder;
    private SWTMainMenu         _mainMenu;
    private SWTTimeLogTableView _timeLogView;
    private SWTMainToolBar      _mainToolBar;
    private SWTTotalsTableView  _totalsTableView;
    //private SashForm            _treeFilterSash;
    private SWTDetailsView      _detailsView;
    private SWTStatusLine       _statusLine;
    private IconSet             _iconSet;
    private Composite           _treeFilterContainer;
    private TrayItem            _trayItem;
    private AdvancedTimeFilterView   _filterView;
    private MenuFactory         _menuFactory;
    private NotificationManager _notificationManager;
    private FontResource        _lcdFontResource;

    public SWTMainWindow(SWTUIManager uiManager) {
        display = uiManager.getDisplay();
        _shell = new Shell(display);
        _menuFactory = new MenuFactory(this);
        _notificationManager = new NotificationManager(this);
        setup();
        addToTray();
    }


    public Shell getShell() {
        return _shell;
    }


    public void showError(String message) {
        MessageBox m = new MessageBox(_shell, SWT.ICON_ERROR | SWT.OK);
        m.setMessage(message);
        m.open();
    }


    /**
     * @return True if a user has choosen either 'YES' or 'NO' button.
     * False otherwise ('CANCEL').
     */
    public boolean confirmSave() {
        MessageBox m = new MessageBox(_shell, SWT.ICON_QUESTION | SWT.NO
            | SWT.YES | SWT.CANCEL);
        m.setMessage(ResourceHelper.getString("message.saveChanges"));
        int result = m.open();
        if (result == SWT.YES) {
            TimeTracker.getInstance().saveWorkspace(true);
        }
        return (result == SWT.YES || result == SWT.NO);
    }


    public boolean confirmExit(String message) {
        MessageBox m = new MessageBox(_shell, SWT.ICON_QUESTION | SWT.NO
            | SWT.YES);
        m.setMessage(message);
        m.setText(ResourceHelper.getString("mainwin.confirmation"));
        int result = m.open();
        return (result == SWT.YES);
    }


    private void setup() {
        this.updateTitle();
        SWTUIManager.setTimeCultWindowIcons(_shell);

        _mainMenu = new SWTMainMenu(this);
        _mainToolBar = new SWTMainToolBar(this);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        gridLayout.horizontalSpacing = 0;
        _shell.setLayout(gridLayout);

        _treeTabSash = new SashForm(_shell, SWT.NONE);
        _treeTabSash.setOrientation(SWT.HORIZONTAL);
        GridData treeTabData = new GridData(GridData.FILL_HORIZONTAL
            | GridData.FILL_VERTICAL);
        treeTabData.horizontalSpan = 3;
        _treeTabSash.setLayoutData(treeTabData);

        GridLayout treeFilterGrid = new GridLayout();
        treeFilterGrid.verticalSpacing = 2;
        treeFilterGrid.marginBottom = 0;
        treeFilterGrid.marginWidth = 2;
        treeFilterGrid.marginHeight = 0;
        _treeFilterContainer = new Composite(_treeTabSash, SWT.NONE);
        _treeFilterContainer.setLayout(treeFilterGrid);
        _treeFilterContainer.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                AppPreferences.getInstance().setTreeTabSashWeights(
                    _treeTabSash.getWeights());
            }
        });

        _projTreeView = new SWTProjectTreeView(this);
        _mainTabFolder = new SWTMainTabFolder(this);
        _filterView = new AdvancedTimeFilterView(this);

        _statusLine = new SWTStatusLine(this);
        _timeLogView = new SWTTimeLogTableView(this);
        _totalsTableView = new SWTTotalsTableView(this);
        _detailsView = new SWTDetailsView(this);

        _treeTabSash.setWeights(AppPreferences.getInstance().getTreeTabSashWeights());

        _lcdFontResource = new FontResource(_shell, "Repetition Scrolling", 20, "fonts/repet.ttf");

        //
        // On close
        //
        _shell.addListener(SWT.Close, new Listener() {
            public void handleEvent(Event evt) {
                _trayItem.dispose();
                TimeTracker.getInstance().exit();
            }
        });
        //
        // On minimize
        //
        _shell.addListener(SWT.Iconify, new Listener() {
            public void handleEvent(Event evt) {
                if(TimeTracker.getInstance().getAppPreferences().isHideWhenMinimized()) {
                    SWTMainWindow.this._shell.setVisible(false);
                }
            }
        });

    }


    public Composite getProjectTreeContainer() {
        return _treeFilterContainer;
    }


    public SashForm getMainTabFolderSash() {
        return _treeTabSash;
    }


    public SWTMainTabFolder getMainTabFolder() {
        return _mainTabFolder;
    }


    public SWTProjectTreeView getProjectTreeView() {
        return _projTreeView;
    }


    public SWTMainMenu getMainMenu() {
        return _mainMenu;
    }


    public SWTTimeLogTableView getTimeLogView() {
        return _timeLogView;
    }


    public SWTMainToolBar getMainToolBar() {
        return _mainToolBar;
    }


    public SWTTotalsTableView getTotalsTableView() {
        return _totalsTableView;
    }


    public Composite getFilterContainer() {
        return _treeFilterContainer;
    }


    public SWTDetailsView getDetailsView() {
        return _detailsView;
    }


    public SWTStatusLine getStatusLine() {
        return _statusLine;
    }


    public void updateTitle() {
        String title = getTitleString();
        this._shell.setText(title);
        if (this._trayItem != null && !this._trayItem.isDisposed()) {
            this._trayItem.setToolTipText(title);
        }
    }

    public String getTitleString() {
        StringBuilder titleBuf = new StringBuilder();
        Workspace ws = TimeTracker.getInstance().getWorkspace();
        if (ws != null) {
            titleBuf.append(ws.toString());
            titleBuf.append(" - ");
        }
        titleBuf.append(AppInfo.getAppName());
        return titleBuf.toString();
    }


    public synchronized IconSet getIconSet() {
        if (_iconSet == null) {
            _iconSet = new IconSet(this._shell.getDisplay());
        }
        return _iconSet;
    }


    public void restoreWindow() {
        _shell.setVisible(true);
        if (!_shell.isVisible()) {
            _trayItem.dispose();
            _shell.dispose();
            _shell = new Shell(display);
            setup();
            addToTray();
            _shell.open();
            _shell.setActive();
        }
        _shell.setMinimized(false);
        _shell.forceActive();
    }


    public void updateControlsFromPrefs() {
        AppPreferences appPrefs = AppPreferences.getInstance();
        this._treeTabSash.setWeights(appPrefs.getTreeTabSashWeights());
        this._mainTabFolder.selectTab(appPrefs.getSelectedTab());
    }


    private void addToTray() {
        Tray tray = _shell.getDisplay().getSystemTray();
        _trayItem = new TrayItem(tray, SWT.NONE);
        _trayItem.setImage(getIconSet().getIcon("timecult", true));
        _trayItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                restoreWindow();
            }
        });
        _trayItem.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event evt) {
                TrayMenu.getInstance(SWTMainWindow.this).open();
            }
        });
        _trayItem.setToolTipText(getTitleString());
    }


    public Menu createInProgressStartMenu(MenuItem parentItem,
        SelectionListener l) {
        Menu startMenu = new Menu(parentItem);
        Task[] tasks = TimeTracker.getInstance().getRecentTasks(7);
        for (Task task : tasks) {
            addTaskItem(startMenu, task, l);
        }
        return startMenu;
    }


    private void addTaskItem(Menu startMenu, Task task, SelectionListener l) {
        String iconName = task instanceof Activity ? "activity" : "inProgress";
        MenuItem taskMenuItem = new MenuItem(startMenu, SWT.CASCADE);
        taskMenuItem.setData(task);
        String menuText = task.toString();
        if (menuText.length() > 80) menuText = menuText.substring(0,80) + "...";
        taskMenuItem.setText(menuText);
        taskMenuItem.addSelectionListener(l);
        taskMenuItem.setImage(getIconSet().getIcon(iconName, true));
    }

    public static void centerShell(Shell shell) {
        Rectangle primaryArea = shell.getDisplay().getPrimaryMonitor()
            .getClientArea();
        Rectangle shellArea = shell.getBounds();
        shell.setLocation(
            (primaryArea.width - shellArea.width) / 2,
            (primaryArea.height - shellArea.height) / 2);
    }

    public static void centerShellRelatively(Shell parent, Shell shell) {
        Rectangle parentArea = parent.getBounds();
        Rectangle shellArea = shell.getBounds();
        Point parentCenter = new Point(parentArea.x + parentArea.width / 2, parentArea.y + parentArea.height / 2);
        Point shellOrigin = new Point(parentCenter.x - shellArea.width / 2, parentCenter.y - shellArea.height / 2);
        shell.setLocation(shellOrigin);
    }


    public AdvancedTimeFilterView getFilterView() {
        return _filterView;
    }


    public MenuFactory getMenuFactory() {
        return _menuFactory;
    }

    @SuppressWarnings("SameParameterValue")
    public File chooseTargetFile(String defaultExtension) {
        FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
        fileDialog.setFilterExtensions(new String[] { defaultExtension, "*.*" });
        fileDialog.open();
        String name = fileDialog.getFileName();
        if ((name == null) || (name.length() == 0))
            return null;
        return new File(fileDialog.getFilterPath(), name);
    }

    public void showPopupMessage(String message) {
        _notificationManager.sendMessage(message);
    }

    public Font getLcdFont() {
        return _lcdFontResource.getFont();
    }
}
