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
 * $Id: SWTUIManager.java,v 1.39 2010/12/14 12:14:22 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import net.sf.timecult.AppInfo;
import net.sf.timecult.PlatformUtil;
import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.io.AutosaveManagerListener;
import net.sf.timecult.model.*;
import net.sf.timecult.ui.GenericUIManager;
import net.sf.timecult.ui.swt.calendar.CalendarDialog;
import net.sf.timecult.ui.swt.calendar.ICalendarDialogListener;
import net.sf.timecult.ui.swt.timer.SWTTimerWindow;
import net.sf.timecult.util.Formatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.FileDialog;

import java.awt.*;
import java.io.File;
import java.util.Calendar;

public class SWTUIManager implements GenericUIManager, AutosaveManagerListener {
    private SWTMainWindow _mainWindow;
    private Display       _display;
    private Rectangle     _bounds;
    private SplashScreen  _splashScreen;

    public void initUI() {
        _display = new Display();
        if (PlatformUtil.isOSLinux()) {
            _splashScreen = new SplashScreen(_display);
        }
        _mainWindow = new SWTMainWindow(this);
    }

    public void setLookAndFeel(String name) {
        // Ignore
    }

    public String getLookAndFeel() {
        return "SWT";
    }

    public void setBounds(int left, int top, int width, int height) {
        _mainWindow.getShell().setBounds(left, top, width, height);
    }

    public Rectangle getBounds() {
        if (_bounds == null && !_mainWindow.getShell().isDisposed()) {
            org.eclipse.swt.graphics.Rectangle r = _mainWindow.getShell()
                .getBounds();
            _bounds = new Rectangle(r.x, r.y, r.width, r.height);
        }
        return _bounds;
    }

    public void showError(String message) {
        _mainWindow.showError(message);
    }

    public void setCurrentSelection(Object object) {
        _mainWindow.getProjectTreeView().setCurrentSelection(object);
    }

    public void displaySplashScreen() {
        // TODO Auto-generated method stub

    }

    public void startTimer(Workspace workspace, Task task, long initTime) {
        if (!task.isFlagged()) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }
        _mainWindow.getProjectTreeView().updateTreeItemStyle(task);
        SWTTimerWindow timerWindow = SWTTimerWindow.newInstance(_mainWindow, workspace, task, initTime);
        timerWindow.launchTimer();
    }

    public void updateProjectTree() {
        _mainWindow.getProjectTreeView().update();
    }

    public void updateOnSelection(Object object) {
        _mainWindow.getProjectTreeView().getPopupMenu().updateOnSelection(object);
        _mainWindow.getMainToolBar().updateOnSelection(object);
        _mainWindow.getTotalsTableView().updateOnSelection(object);
        _mainWindow.getDetailsView().updateOnSelection(object);
        _mainWindow.getStatusLine().setSelection(object);
        _mainWindow.getMainMenu().updateOnSelection(object);
        _mainWindow.getTimeLogView().updateOnTreeSelection(object);
    }

    public void updateOnRemove(Object object) {
        _mainWindow.getProjectTreeView().updateOnRemove(object);
    }

    public void updateAll() {
        updateProjectTree();
        updateTimeLog(null);
        updateTotals();
        _mainWindow.updateTitle();
        TrayMenu.dispose();
        //_mainWindow.getMainMenu().updateFlagged();
        TimeRecordFilter filter = TimeTracker.getInstance().getWorkspace().getFilter();
        _mainWindow.getFilterView().updateFilterList();
        _mainWindow.getFilterView().setFilterSelection(filter);
    }

    public File chooseFile(boolean forOpen) {
        FileDialog fileDialog = forOpen ?
            new FileDialog(_mainWindow.getShell(), SWT.OPEN) :
            new FileDialog(_mainWindow.getShell(), SWT.SAVE);
        fileDialog.setFilterExtensions(new String[]{"*.tmt", "*.*"});
        fileDialog.open();
        String name = fileDialog.getFileName();

        if ((name == null) || (name.length() == 0))
            return null;

        return new File(fileDialog.getFilterPath(), name);
    }

    public File chooseTargetCsvFile() {
        return _mainWindow.chooseTargetFile("*.csv");
    }


    public void setSaveEnabled(boolean enabled) {
        _mainWindow.getMainToolBar().setSaveEnabled(enabled);
        _mainWindow.getMainMenu().setSaveItemEnabled(enabled);
    }

    public void updateFileMenu() {
        _mainWindow.getMainMenu().updateFileMenu();

    }

    public void updateTimeLog(Object source) {
        _mainWindow.getTimeLogView().updateTable();
        if (source != null && source instanceof TimeRecord) {
            _mainWindow.getTimeLogView().select();
            _mainWindow.getTimeLogView().selectItem((TimeRecord) source);
            _mainWindow.getProjectTreeView().setCurrentSelection(((TimeRecord) source).getTask());
        }
    }

    public void updateTotals() {
        _mainWindow.getTotalsTableView().updateTable();
    }

    public boolean confirmTaskDeletion(Task task) {
        MessageBox m = new MessageBox(_mainWindow.getShell(), SWT.ICON_QUESTION
            | SWT.NO | SWT.YES);
        m.setMessage(ResourceHelper.getString("message.removeTask") + " '"
            + task.getName() + "'?");
        int result = m.open();
        /*
        if (result == SWT.YES) {
            TimeTracker.getInstance().saveWorkspace(true);
        }
        */
        return (result == SWT.YES);
    }


    public boolean confirmProjectDeletion(Project project) {
        MessageBox m = new MessageBox(_mainWindow.getShell(), SWT.ICON_QUESTION
            | SWT.NO | SWT.YES);
        m.setMessage(ResourceHelper.getString("message.removeProject") + " '"
            + project.getName() + "'?");
        int result = m.open();
        /*
        if (result == SWT.YES) {
            TimeTracker.getInstance().saveWorkspace(true);
        }
        */
        return (result == SWT.YES);

    }

    public boolean confirmExit(String message) {
        return _mainWindow.confirmExit(message);
    }

    public boolean confirmSave() {
        return _mainWindow.confirmSave();
    }

    public void cancelExit() {
        //
        // We are just restarting the UI
        //
        startUI();
    }

    public boolean activeTimersExist() {
        return SWTTimerWindow.activeTimersExist();
    }

    public void startUI() {
        if (_splashScreen != null) _splashScreen.open();
        //
        //To catch up with loaded configuration
        //
        updateAll();
        _mainWindow.updateControlsFromPrefs();

        _mainWindow.getShell().open();
        while (!_mainWindow.getShell().isDisposed()) {
            //
            // Keep bounds before disposing the display to allow
            // configuration manager to save them.
            //
            org.eclipse.swt.graphics.Rectangle r = _mainWindow.getShell()
                .getBounds();
            _bounds = new Rectangle(r.x, r.y, r.width, r.height);

            try {
                if (!_display.readAndDispatch())
                    _display.sleep();
            } catch (Exception e) {
                _mainWindow.showError(createErrorMessage(e));
                _display.dispose();
                TimeTracker.getInstance().close(1);
            }
        }
        _display.dispose();
    }

    private String createErrorMessage(Exception e) {
        e.printStackTrace();
        StringBuilder buf = new StringBuilder();
        buf.append("Fatal application error: ");
        if (e instanceof NullPointerException) {
            buf.append("NPE");
        } else {
            buf.append(e.getMessage());
        }
        buf.append('\n');
        buf.append("Details:\n");
        int i = 0;
        for (StackTraceElement ste : e.getStackTrace()) {
            if (i < 1) {
                buf.append("\tVersion:\t").append(AppInfo.getVersionString()).append('\n');
                buf.append("\tClass:\t").append(ste.getClassName()).append('\n');
                buf.append("\tLine:\t").append(ste.getLineNumber()).append('\n');
                buf.append("\tFile:\t").append(ste.getFileName()).append('\n');
                i++;
            } else {
                break;
            }
        }
        buf.append("The application will now exit.\n");
        return buf.toString();
    }

    public void setIdleTime(long duration) {
        _mainWindow.getStatusLine().setIdleTime(duration);
    }

    public void clearIdleTime() {
        _mainWindow.getStatusLine().clearIdleTime();
    }

    public IconSet getIconSet() {
        return _mainWindow.getIconSet();
    }

    public void displayWarning(String message) {
        MessageBox m = new MessageBox(_mainWindow.getShell(), SWT.ICON_WARNING | SWT.OK);
        m.setMessage(message);
        m.setText(ResourceHelper.getString("dialog.warning"));
        m.open();
    }

    public void rebindWorkspaceListeners(Workspace workspace) {
        workspace.addListener(_mainWindow.getProjectTreeView().getPopupMenu());
        workspace.addListener(_mainWindow.getStatusLine());
    }


    /**
     * From AutosaveManagerListener.
     */
    public void doSave() {
        TimeTracker tt = TimeTracker.getInstance();
        if (tt.getWorkspace() != null && !tt.isSaving()) {
            if (tt.getWorkspace().hasBeenModified()) {
                if (!_display.isDisposed()) {
                    _display.asyncExec(new Runnable() {
                        public void run() {
                            TimeTracker.getInstance().saveWorkspace(true);
                        }
                    });
                }
            }
        }
    }

    public Display getDisplay() {
        return this._display;
    }

    public void showNotification(String message) {
        _mainWindow.showPopupMessage(message);
    }

    public static void setTimeCultWindowIcons(Shell shell) {
        Image iconImage_16x16 = new Image(shell.getDisplay(), ResourceHelper.openStream("images/timecult_icon.png"));
        Image iconImage_32x32 = new Image(shell.getDisplay(), ResourceHelper.openStream("images/timecult_icon_32x32.png"));
        shell.setImages(new Image[]{iconImage_16x16, iconImage_32x32});
    }

    public static Text addDateField(final SWTDialog dialog, Composite contentPanel) {
        IconSet iconSet = ((SWTUIManager) TimeTracker.getInstance().getUIManager()).getIconSet();
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
        gd.widthHint = 100;
        final Text dateField = new Text(dateEntryPanel, SWT.BORDER);
        dateField.setLayoutData(gd);
        dateField.setText("");
        //
        // Add date picker button
        //
        Button datePickerButton = new Button(dateEntryPanel, SWT.None);
        datePickerButton.setImage(iconSet.getIcon("calendar", true));
        datePickerButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent evt) {
                CalendarDialog calDialog = new CalendarDialog(dialog.getShell(), dateField);
                calDialog.setListener(new ICalendarDialogListener() {
                    @Override
                    public void dateSelected(Calendar data, Text dateField) {
                        dateField.setText(Formatter.toDateString(data.getTime()));
                    }
                });
                calDialog.open();
            }
        });
        return dateField;
    }

}
