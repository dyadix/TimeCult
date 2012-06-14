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
 * $Id: TimeTracker.java,v 1.38 2011/01/16 06:00:21 dragulceo Exp $
 */
package net.sf.timecult;

import java.io.File;
import java.util.*;

import net.sf.timecult.conf.AppPreferences;
import net.sf.timecult.conf.ConfigurationManager;
import net.sf.timecult.export.CSVExporter;
import net.sf.timecult.io.AutosaveManager;
import net.sf.timecult.io.FileLockManager;
import net.sf.timecult.io.WorkspaceReader;
import net.sf.timecult.io.WorkspaceXMLWriter;
import net.sf.timecult.model.*;
import net.sf.timecult.model.mem.LocalIdGenerator;
import net.sf.timecult.model.mem.MemContainerFactory;
import net.sf.timecult.model.mem.MemTimeLog;
import net.sf.timecult.stopwatch.StopwatchEvent;
import net.sf.timecult.stopwatch.StopwatchListener;
import net.sf.timecult.ui.GenericUIManager;
import net.sf.timecult.ui.swt.SWTUIManager;
import net.sf.timecult.util.Formatter;


/**
 * Top-level component, acts as a controller. Contains main() method to launch
 * the application.
 * 
 * @author rvishnyakov (rvishnyakov@yahoo.com)
 */
public class TimeTracker implements WorkspaceListener {

    public final static String FILE_EXT = ".tmt";    

    public TimeTracker() {
    	//_uiManager = new SwingUIManager();
    	_uiManager = new SWTUIManager();
        _confManager = new ConfigurationManager(this);
        this.autosaveManager = new AutosaveManager();
        this.autosaveManager.addListener((SWTUIManager)_uiManager);
        _activeInstance = this;
        resetWorkspace();
        _uiManager.initUI();
        _isUIInitialized = true;
        Runtime.getRuntime().addShutdownHook(
                new Thread(new ConfigurationSaver()));        
    }


    public static TimeTracker getInstance() {
        return _activeInstance;
    }

    public void loadConfiguration() {
        try {
            _confManager.load();
        }
        catch (Exception e) {
            e.printStackTrace();
            _uiManager.showError("Configuration error: " + e.getMessage());
        }
    }
     
    
    public void startUI() {
    	_uiManager.startUI();
    }


    public void updateTask(Task task) {
        _uiManager.updateProjectTree();
        _workspace.fireWorkspaceChanged(new WorkspaceEvent(
                WorkspaceEvent.WORKSPACE_TASK_CHANGED));
    }

    public void updateProject(Project project) {
        _uiManager.updateProjectTree();
        _workspace.fireWorkspaceChanged(new WorkspaceEvent(
                WorkspaceEvent.WORKSPACE_PROJECT_CHANGED));
    }

    public TimeLog getTimeLog() {
        return _workspace.getTimeLog();
    }

    public Workspace getWorkspace() {
        return _workspace;
    }


    public static void main(String[] args) {        
        TimeTracker timeTracker = new TimeTracker();
        timeTracker.loadConfiguration();        
        if (args.length > 0) {
            File cmdlineFile = new File(args[0]);
            timeTracker.loadWorkspace(cmdlineFile);
        }
        else {
            timeTracker.resetWorkspace();
            if (timeTracker.getAppPreferences().isAutoOpenRecentFile()) {
                timeTracker.openRecentFile();
            }
        }  
        timeTracker.startUI();
    }

    public void selectObject(Object object) {
        if (object != null) {
            if (object instanceof ProjectTreeItem) {
                _workspace.setSelection((ProjectTreeItem) object);
            }
            _uiManager.updateOnSelection(object);
        }
    }
    
    
    public void removeSelection() {
        boolean confirmed = false;
        Project parent = null;
        ProjectTreeItem removable = null;
        ProjectTreeItem selectedItem = _workspace.getSelection();
        if (selectedItem != null) {
            removable = selectedItem;
            if (selectedItem instanceof Task) {
                Task selectedTask = (Task) selectedItem;
                confirmed = _uiManager.confirmTaskDeletion(selectedTask);
                if (confirmed) {
                    parent = selectedTask.getProject();
                    _workspace.removeTask(parent, selectedTask.getId());
                }
            }
            else if (selectedItem instanceof Project) {
                Project project = (Project) selectedItem;
                confirmed = _uiManager.confirmProjectDeletion(project);
                if (confirmed) {
                    parent = project.getParent();
                    _workspace.removeChildProject(parent, project.getId());
                }
            }
        }
        if (confirmed) {
            _uiManager.updateOnRemove(removable);
        }
    }


    public void loadWorkspace(File sourceFile) {
        if (FileLockManager.isLocked(sourceFile.getAbsolutePath())) {
            _uiManager.showError("The file " + sourceFile
                + " is already open, please choose anothe file!");
            return;
        }
        File oldFile = _workspaceFile;
        if (resetWorkspace()) {
            try {
                if (sourceFile.exists()) {

                    _workspace.setEventsEnabled(false);
                    WorkspaceReader reader = new WorkspaceReader(_workspace);
                    reader.readWorkspace(sourceFile);
                    if (reader.isFileUpgradeMode()) {
                        _uiManager.displayWarning(ResourceHelper
                            .getString("warning.fileUpgrade"));
                    }
                    _workspaceFile = sourceFile;
                    FileLockManager.lock(sourceFile.getAbsolutePath());
                    addRecentlyOpenFile(sourceFile);
                    _uiManager.updateAll();
                    if (_workspace.getSelection() != null) {
                        _uiManager.setCurrentSelection(_workspace
                            .getSelection());
                    }
                    _workspace.startIdle();
                }
            }
            catch (Exception e) {
                _uiManager.showError("Cannot read " + sourceFile.getName()
                    + ":\n" + e.getLocalizedMessage());                
                if (oldFile != null && oldFile != sourceFile) {
                    loadWorkspace(oldFile);
                }
                else {
                    resetWorkspace();
                }
            }
            finally {
                _workspace.setEventsEnabled(true);
            }
        }
    }

    public void openWorkspace() {
        File fileToOpen = _uiManager.chooseFile(true);
        if (fileToOpen != null) {
            loadWorkspace(fileToOpen);
        }
    }

    public synchronized void saveWorkspace(boolean defaultFile) {
        this.saving = true;
        File targetFile = null;
        try {
            if (defaultFile) {
                targetFile = _workspaceFile;
            }
            if (targetFile == null) {
                targetFile = _uiManager.chooseFile(false);
                if (targetFile != null) {                    
                    _workspace.setFileName(targetFile.getName());
                    addRecentlyOpenFile(targetFile);
                    _uiManager.updateAll();
                }
            }
            if (targetFile != null) {
                FileLockManager.lock(targetFile.getAbsolutePath());
                WorkspaceXMLWriter writer = new WorkspaceXMLWriter(_workspace);
                writer.saveWorkspace(targetFile);                
                _workspace.fireWorkspaceChanged(new WorkspaceEvent(
                        WorkspaceEvent.WORKSPACE_SAVED));
                _workspaceFile = targetFile;
            }
        }
        catch (Exception e) {
            String fileName = "unnamed";
            if (targetFile != null) {
                fileName = targetFile.getName();
            }
            _uiManager.showError("Cannot save " + fileName + ":\n"
                    + e.getLocalizedMessage());
        }
        this.saving = false;
    }
    
    
    public boolean isSaving() {
        return this.saving;
    }
    

    /**
     * Handles workspace events.
     * 
     * @see net.sf.timecult.model.WorkspaceListener#workspaceChanged(net.sf.timecult.model.WorkspaceEvent)
     */
    public void workspaceChanged(WorkspaceEvent we) {
        if (_workspace.hasBeenModified()) {
            _uiManager.setSaveEnabled(true);
        }
        else {
        	_uiManager.setSaveEnabled(false);
        }
        _uiManager.updateTimeLog(we.getSource());
        if (we.getId() == WorkspaceEvent.FILTER_CHANGED) {
            _uiManager.updateProjectTree();
        } 
        _uiManager.updateTotals();
    }

    /**
     * Creates a new empty workspace instead of existing one. Before doing
     * so asks a confirmation from a user.
     * @return Confirmation flag value (true if the user has confirmed the 
     * workspace reset).
     */
    public boolean resetWorkspace() {
        boolean confirmed = true;
        if (_workspace != null && _workspace.hasBeenModified()) {
            confirmed = _uiManager.confirmSave();
        }
        if (confirmed) {
            FileLockManager.unlockAll();
            if (_workspace != null && _workspace.isIdleEnabled()) {
                _workspace.stopIdle();
            }
            ContainerFactory cf = new MemContainerFactory();
            _workspace = new Workspace(cf);
            _workspace.setTimeLog(new MemTimeLog());
            _workspace.setIdGenerator(new LocalIdGenerator());
            _workspace.addListener(this);            
            _workspace.addIdleListener(new IdleStopwatchListener());
            _workspace.setSelection(_workspace);
            if (_isUIInitialized) {
                _uiManager.rebindWorkspaceListeners(_workspace);
                _uiManager.setSaveEnabled(false);
                _uiManager.updateAll();
            }
            _workspace.setCreationDateTime(Calendar.getInstance().getTime());
            _workspaceFile = null;
        }
        return confirmed;
    }

    
    /**
     * @return The array of recently opened files. 
     */
    public File[] getRecentlyOpenFiles() {
        File[] files = new File[_recentlyOpenFiles.size()];
        int i = 0;
        for (File f : _recentlyOpenFiles) {
            files[i] = f;
            i ++;
        }
        return files;
    }

    
    /**
     * Adds a file to the list of recently open files. If the file already
     * exists, moves it to the end of the list.
     * 
     * @param file
     *            The file to add.
     */
    public void addRecentlyOpenFile(File file) {
        for (File f : _recentlyOpenFiles) {
            if (f.getAbsolutePath().equals(file.getAbsolutePath())) {
                _recentlyOpenFiles.remove(f);
                break;
            }
        }
        _recentlyOpenFiles.add(file);
        _uiManager.updateFileMenu();
    }
    
    
    /**
     * Opens the most recently used file if any.
     */
    public void openRecentFile() {
        File recentFile = null;
        if (!_recentlyOpenFiles.isEmpty()) {
            recentFile = _recentlyOpenFiles.lastElement();
        }
        if (recentFile != null && !FileLockManager.isLocked(recentFile.getAbsolutePath())) {
            loadWorkspace(recentFile);
        }
    }

    public void exportToCsv() {
        try {
            File csvTarget = _uiManager.chooseTargetCsvFile();
            if (csvTarget != null) {
                CSVExporter.export(_workspace, csvTarget.getPath());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            _uiManager
                    .showError(e.getClass().getName() + ":" + e.getMessage());
        }
    }

    /**
     * Exit the application.
     */
    public void exit() {
        boolean doExit = true;
        if (_workspace.hasBeenModified()) {
            doExit = _uiManager.confirmSave();
        }
        if (doExit && _uiManager.activeTimersExist()) {
            doExit = _uiManager.confirmExit(
                ResourceHelper.getString("message.activeTimers"));
        }
        if (doExit) {
            FileLockManager.unlockAll();
            System.exit(0);
        }
        else {
            _uiManager.cancelExit();
        }
    }

    /*
    public void setFilter(TimeRecordFilter filter) {
		this._workspace.setFilter(filter);
		_uiManager.updateTimeLog(null);
        _uiManager.updateTotals();
	}
    */

    public TimeRecordFilter getFilter() {
        return this._workspace.getFilter();
    }

    public GenericUIManager getUIManager() {
        return _uiManager;
    }


    public void startTimer() {
        ProjectTreeItem selectedItem = _workspace.getSelection();
        if (selectedItem != null && selectedItem instanceof Task) {
        	_uiManager.startTimer(_workspace, (Task)selectedItem);
        }
    }

    private class ConfigurationSaver implements Runnable {

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                _confManager.save();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    
    
    private class IdleStopwatchListener implements StopwatchListener {
                
        public final static long IDLE_NOTIFICATION_INTERVAL = 300000; // 5 min
        private long nextNotifTime = IDLE_NOTIFICATION_INTERVAL;

        public void stateChanged(StopwatchEvent evt) {

            switch (evt.getID()) {
            case StopwatchEvent.STOP:
                _uiManager.clearIdleTime();
                nextNotifTime = IDLE_NOTIFICATION_INTERVAL;
                break;
            case StopwatchEvent.TICK:
                long idleTime = evt.getSource().getDuration();
                _uiManager.setIdleTime(idleTime);
                if (AppPreferences.getInstance().isIdleTimeNotification()) {
                    if (idleTime >= nextNotifTime) {
                        _uiManager.showNotification(ResourceHelper
                            .getString("workspace.idle")
                            + ":\n\n"
                            + Formatter.toDurationString(idleTime, true));
                        nextNotifTime += IDLE_NOTIFICATION_INTERVAL;
                    }
                }
                break;

            }
        }

    }  
    
    
    public void cutSelection() {
        ProjectTreeItem selectedItem = _workspace.getSelection();
        if (selectedItem != null && _clipboardItem == null) {
            _clipboardItem = selectedItem;
            _workspace.unlinkParent(selectedItem);
            _uiManager.updateOnRemove(selectedItem);
            selectedItem = null;
        }
    }
    
    public void pasteFromLocalClipboard() {
        if (_clipboardItem != null && _workspace.getSelection() != null
            && _workspace.getSelection() instanceof Project) {
            _workspace.changeParent(_clipboardItem, (Project) _workspace.getSelection());
            _uiManager.updateProjectTree();
            selectObject(_clipboardItem);
            _uiManager.setCurrentSelection(_clipboardItem);
            _clipboardItem = null;
        }
    }
    
    public boolean isLocalClipboardEmpty() {
        return _clipboardItem == null;
    }
    
    /*
    public ProjectTreeItem getSelection() {
        return _selectedItem;
    }
    */
    
    public ConfigurationManager getConfigurationManager() {
        return this._confManager;
    }
    
    
    public AppPreferences getAppPreferences() {
        return AppPreferences.getInstance();
    }

    public Task[] getRecentTasks(int maxNumber) {
        List<Task> recentTasks = new ArrayList<Task>(maxNumber);
        TimeRecord[] records = getTimeLog().getTimeRecords(null);
        for (int i = records.length - 1; i >= 0; i--) {
            Task recordedTask = records[i].getTask();
            if (!recentTasks.contains(recordedTask) && !recordedTask.isClosed() && !recordedTask.isWaiting()) {
                recentTasks.add(recordedTask);
                if (recentTasks.size() >= maxNumber) break;
            }
        }
        return recentTasks.toArray(new Task[recentTasks.size()]);
    }
    
    
    // Private attributes
    //========================================================================

    private Workspace _workspace = null;
    //private ProjectTreeItem _selectedItem = null;
    private ProjectTreeItem _clipboardItem = null;
    //private Task _selectedTask = null;
    //private Project _selectedProject = null;
    //private MainWindow _mainWindow = null;
    private GenericUIManager _uiManager = null;
    private File _workspaceFile = null;
    private static TimeTracker _activeInstance = null;
    private Vector<File> _recentlyOpenFiles = new Vector<File>();
    private ConfigurationManager _confManager = null;
    private boolean _isUIInitialized = false;
    private AutosaveManager autosaveManager;
    private boolean saving;

}
