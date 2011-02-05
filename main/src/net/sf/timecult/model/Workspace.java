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
 * $Id: Workspace.java,v 1.30 2011/01/19 14:22:05 dyadix Exp $
 */
package net.sf.timecult.model;

import java.util.*;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.stopwatch.Stopwatch;
import net.sf.timecult.stopwatch.StopwatchListener;


/**
 * Brings together all the model elements, performs model change actions and
 * sends corresponding notifications, say, to UI components. Workspace is a
 * single access point to the model.
 * 
 * @author rvishnyakov (dyadix@gmail.com)
 */
public class Workspace extends Project {

    /**
     * Creates new workspace with the given container factory which must be used
     * to create containers for projects and tasks.
     * 
     * @param cf
     *            The container factory to use.
     */
    public Workspace(ContainerFactory cf) {
        super(cf, null, "root", ResourceHelper.getString("workspace.newWorkspace"));
        _cf = cf;
        _idleTask = new IdleTask(this);
        this.addTask(_idleTask);
        _taskCache.put(IdleTask.ID, _idleTask);
        _idleStopwatch = new Stopwatch();
        _uuid = UUID.randomUUID();
        this._customFilters = new CustomFilterList(this);
        this._settings = new WorkspaceSettings(this);
    }

    /**
     * Sets the time log where time records are to be added.
     * 
     * @param timeLog
     *            The time log to use.
     */
    public void setTimeLog(TimeLog timeLog) {
        _timeLog = timeLog;
    }

    /**
     * @return The current time log in use.
     */
    public TimeLog getTimeLog() {
        return _timeLog;
    }

    /**
     * Adds a time record and notifies all the listeners on the change.
     * 
     * @param task
     *            The task for which the time is recorded.
     * @param start
     *            Time measure start time.
     * @param duration
     *            Measured duration.
     * @param notes
     *            Notes to measured time.
     */
    public TimeRecord createRecord(Task task, Date start, long duration, String notes, boolean roundUp) {
        Date recStart = start;
        long recDuration = duration;
        long roundUpInterval = this._settings.getRoundUpInterval();
        if (roundUpInterval != 0 && roundUp) {        
            recStart = new Date(roundUpTime(start.getTime()));
            long recEndTime = roundUpTime(start.getTime() + duration);
            recDuration = recEndTime - recStart.getTime();
        }
        TimeRecord timeRec = new TimeRecord(task, recStart, recDuration, notes);
        return timeRec;
    }
    
	public synchronized void recordTimeEx(TimeRecord timeRec, boolean withEvent) {
		_timeLog.addTimeRecord(timeRec);
		if (withEvent == true) {
			recordTimeFinish(timeRec);
		}
	}

	public void recordTimeFinish(TimeRecord timeRec) {
		fireWorkspaceChanged(new WorkspaceEvent(
				WorkspaceEvent.WORKSPACE_TIME_REC_ADDED, timeRec));
	}

	public void recordTime(TimeRecord timeRec) {
		recordTimeEx(timeRec, true);
	}

    /**
     * Removes all the records from the time log.
     */
    public void clearTimeLog() {
        _timeLog.clear();
        fireWorkspaceChanged(new WorkspaceEvent(
                WorkspaceEvent.WORKSPACE_TIME_LOG_CLEARED));
    }

    /**
     * Sets a task/project ID generator to use.
     * 
     * @param idGen
     *            The ID generator to use.
     */
    public void setIdGenerator(IdGenerator idGen) {
        _idGen = idGen;
    }

    /**
     * @return The current ID generator in use.
     */
    public IdGenerator getIdGenerator() {
        return _idGen;
    }

    /**
     * @return The root project of the workspace (the workspace itself).
     */
    public Project getRoot() {
        return this;
    }

    public Project createChildProject(Project parent, String id, String name) {
        String newId = id;
        if (newId == null && _idGen != null) {
            newId = _idGen.getNewId();
        }
        Project childProject = new Project(_cf, parent, newId, name);
        parent.addSubproject(childProject);
        fireWorkspaceChanged(new WorkspaceEvent(
                WorkspaceEvent.WORKSPACE_PROJECT_ADDED));
        return childProject;
    }

    /**
     * Creates a new task if only it's ID is different from idle task ID.
     * 
     * @param parent
     *            The task parent (project)
     * @param id
     *            The task ID.
     * @param name
     *            The task name.
     * @return Either newly created task in the workspace or idle task if the
     *         task id is "idle".
     */
    public Task createTask(Project parent, String id, String name) {
        if (IdleTask.ID.equals(id) && parent == this) {
            _idleTask.setName(name);
            return _idleTask;
        }
        else {
            Task task = new Task(parent, id, name);
            registerTask(parent, task);
            return task;
        }
    }
    
    
    public Activity createActivity(Project parent, String id, String name) {
        Activity activity = new Activity(parent, id, name);
        registerTask(parent, activity);
        return activity;
    }
    
    
    public ProjectTreeItem createItem(Project parent, ItemType type, String id, String name) {
        ProjectTreeItem treeItem = null;
        switch(type) {
        case PROJECT:
            treeItem = createChildProject(parent, id, name);
            break;
        case TASK:
            treeItem = createTask(parent, id, name);
            break;
        case ACTIVITY:
            treeItem = createActivity(parent, id, name);
            break;
        }
        return treeItem;
    }
    
    
    /**
     * Sets an automatically generated task ID if it has not been specified
     * already, adds a task to the given parent and to the task cache. Notifies
     * listeners of the added task.
     * 
     * @param parent
     *            The parent to add the task to.
     * @param task
     *            The task to add (register).
     */
    private void registerTask(Project parent, Task task) {
        if (task.getId() == null && _idGen != null) {
            task.setId(_idGen.getNewId());
        }
        parent.addTask(task);
        _taskCache.put(task.getId(), task);
        fireWorkspaceChanged(new WorkspaceEvent(
            WorkspaceEvent.WORKSPACE_TASK_ADDED));
    }

    public Task findTask(String taskId) {
        return (Task) _taskCache.get(taskId);
    }

    public void removeTask(Project parent, String taskId) {
        TimeRecordFilter taskFilter = new TimeRecordFilter();
        taskFilter.setTask(findTask(taskId));
        _timeLog.removeRecords(taskFilter);
        _taskCache.remove(taskId);
        parent.removeTask(taskId);
        fireWorkspaceChanged(new WorkspaceEvent(
                WorkspaceEvent.WORKSPACE_TASK_REMOVED));
    }

    public void removeChildProject(Project parent, String projectId) {
        if (parent != null) {
            parent.removeSubproject(projectId, this._timeLog);
            fireWorkspaceChanged(new WorkspaceEvent(
                    WorkspaceEvent.WORKSPACE_PROJECT_REMOVED));
        }
    }


    public String toString() {
        return getName() + " [" + getFileName() + "]";
    }

    public void addListener(WorkspaceListener wl) {
        _listeners.add(wl);
    }

    public boolean hasBeenModified() {
        return _hasBeenModified;
    }

    public void setEventsEnabled(boolean eventsEnabled) {
        _eventsEnabled = eventsEnabled;
    }

    public void fireWorkspaceChanged(WorkspaceEvent we) {
        if (_eventsEnabled) {
            _hasBeenModified = (we.getId() != WorkspaceEvent.WORKSPACE_SAVED);
            for (WorkspaceListener wl : _listeners) {
                wl.workspaceChanged(we);
            }
        }
    }

    public void addIdleListener(StopwatchListener l) {
        _idleStopwatch.addStopwatchListener(l);
    }

    public void enableIdle() {
        _idleTask.setEnabled(true);
        startIdle();
    }

    public void disableIdle() {
        stopIdle();
        _idleTask.setEnabled(false);
    }

    public void startIdle() {
        if (_idleTask.isEnabled()) {
            Stopwatch currIdleStopwatch = _idleStopwatch;
            _idleStopwatch = new Stopwatch();
            if (currIdleStopwatch != null) {
                currIdleStopwatch.stop();
                currIdleStopwatch.copyListenersTo(_idleStopwatch);
            }
            _idleStopwatch.start();
        }
    }

    public void stopIdle() {
        if (_idleTask.isEnabled()) {
            _idleStopwatch.stop();
            TimeRecord timeRec =
            createRecord(_idleTask, _idleStopwatch.getStartTime(), _idleStopwatch
                    .getDuration(), _idleTask.getDefaultNote(), true);
            if (timeRec.getDuration().getValue() != 0) {
                recordTime(timeRec);
            }
        }
    }

    public boolean isIdleEnabled() {
        return _idleTask.isEnabled();
    }
    
    
    public Task[] getTasksByStatus(int status) {
        Vector<Task> tasks = new Vector<Task>();
        addSubprojectTasksByStatus(tasks, this.getRoot(), status);
        return tasks.toArray(new Task[0]);
    }
    
    private void addSubprojectTasksByStatus(Vector<Task> tasks, Project project, int status) {
        Task[] allTasks = project.getTasks(SortCriteria.DEFAULT);
        for (int i = 0; i < allTasks.length; i++) {
            if (allTasks[i].getStatus() == status) {
                tasks.add(allTasks[i]);
            }
        }
        Project[] subprojects = project.getSubprojects(SortCriteria.DEFAULT);
        for (int i = 0; i < subprojects.length; i++) {
            addSubprojectTasksByStatus(tasks, subprojects[i], status);
        }
    }
    
    public void changeParent(ProjectTreeItem item, Project newParent) {
        if (item instanceof Project) {
            newParent.addSubproject((Project) item);
        }
        else if (item instanceof Task) {
            newParent.addTask((Task) item);
        }
        item.setParent(newParent);
        fireWorkspaceChanged(new WorkspaceEvent(
            WorkspaceEvent.WORKSPACE_PROJECT_CHANGED));
    }
    
    public void unlinkParent(ProjectTreeItem item) {
        item.getParent().unlinkItem(item);
        fireWorkspaceChanged(new WorkspaceEvent(
            WorkspaceEvent.WORKSPACE_PROJECT_CHANGED));
    }
    
    public void setFileName(String fileName) {
        _fileName = fileName;
    }
    
    public String getFileName() {
        if (_fileName == null) {
            return ResourceHelper.getString("workspace.unsaved");
        }
        else {
            return _fileName;
        }
    }
    
    public void updateTimeRecord(TimeRecord timeRec) {
        fireWorkspaceChanged(new WorkspaceEvent(
            WorkspaceEvent.TIME_REC_CHANGED));
    }
    
    public void joinTimeRecords(TimeRecord timeRecs[]) {
        _timeLog.joinRecords(timeRecs);
        fireWorkspaceChanged(new WorkspaceEvent(
            WorkspaceEvent.TIME_RECS_JOINED));
    }
    
    public void removeTimeRecords(TimeRecord timeRecs[]) {
        _timeLog.removeRecords(timeRecs);
        fireWorkspaceChanged(new WorkspaceEvent(
            WorkspaceEvent.TIME_RECS_REMOVED));
    }
    
    
    public String getUUIDString() {
        return _uuid.toString();
    }
    
    public void setUUIDString(String uuidString) {
        this._uuid = UUID.fromString(uuidString);
    }
    
    
    public void setSelection(ProjectTreeItem selection) {
        this._selection = selection;
    }
    
    public ProjectTreeItem getSelection() {
        return this._selection;
    }
    
    public boolean isSelected(ProjectTreeItem item) {
        if (this._selection != null && this._selection == item) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public void setFilter(TimeRecordFilter filter) {
        this._timeFilter = filter;
        this.fireWorkspaceChanged(new WorkspaceEvent(WorkspaceEvent.FILTER_CHANGED, filter));
    }
    
    public TimeRecordFilter getFilter() {
        return this._timeFilter;
    }
    
    @Override
    public ItemType getItemType() {
        return ItemType.WORKSPACE;
    }
    
    
    public CustomFilterList getCustomFilters() {
        return this._customFilters;
    }
    
    
    public WorkspaceSettings getSettings() {
        return this._settings;
    }
    
    
    @Override
    public boolean isDeletable() {
        return false;
    }
    
    public long roundUpTime(long time) {
        long roundUpInterval = _settings.getRoundUpInterval();
        if (roundUpInterval == 0) {
            return time;
        }
        return Math.round(time/(float)roundUpInterval) * roundUpInterval;
    }

    public ProjectTreeItem[] findItems(String text, boolean caseSensitive, SortCriteria sortCriteria) {
        List<ProjectTreeItem> items = new ArrayList<ProjectTreeItem>();
        if (text != null && text.length() > 0) {
            scanItem(getRoot(), items, caseSensitive ? text : text.toLowerCase(), sortCriteria);
        }
        return items.toArray(new ProjectTreeItem[items.size()]);
    }

    private void scanItem(ProjectTreeItem root, List<ProjectTreeItem> items, String text, SortCriteria sortCriteria) {
        if (root.getName().toLowerCase().contains(text)) {
            items.add(root);
        }
        if (root instanceof Project) {
            Project project = (Project)root;
            for (Task task : project.getTasks(sortCriteria)) {
                scanItem(task, items, text, sortCriteria);
            }
            for (Project subProject : project.getSubprojects(sortCriteria)) {
                scanItem(subProject, items, text, sortCriteria);
            }
        }
    }


    private Stopwatch        _idleStopwatch   = null;
    private ContainerFactory _cf              = null;
    private IdGenerator      _idGen           = null;
    private Vector<WorkspaceListener> _listeners = new Vector<WorkspaceListener>();
    private Hashtable<String,Task>  _taskCache       = new Hashtable<String,Task>();
    private boolean          _hasBeenModified = false;
    private TimeLog          _timeLog         = null;
    private boolean          _eventsEnabled   = true;
    private IdleTask         _idleTask        = null;
    private String           _fileName;
    private UUID             _uuid;
    private ProjectTreeItem  _selection;
    private TimeRecordFilter _timeFilter;
    private CustomFilterList _customFilters;
    private WorkspaceSettings _settings;
}
