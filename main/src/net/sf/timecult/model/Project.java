/*
 * Copyright (c) Rustam Vishnyakov, 2005-2009 (dyadix@gmail.com)
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
 * $Id: Project.java,v 1.8 2009/11/21 14:00:29 dyadix Exp $
 */
package net.sf.timecult.model;

import java.util.TreeMap;

/**
 * A project consists of tasks. It's not a separately measurable unit. Overall time
 * used for it is calculated as a sum of times spent for each task.
 * @author rvishnyakov (dyadix@gmail.com)
 */
public class Project extends ProjectTreeItem implements Totals, DescriptionHolder {
    
    public enum SortCriteria { DEFAULT, BY_ID, BY_NAME };

    public Project(ContainerFactory containerFactory, Project parent,
            String id, String name) {
        super(id, name, parent);
        _taskContainer = containerFactory.createTaskContainer();
        _projectContainer = containerFactory.createProjectContainer();
    }

    
    /**
     * Sorts the project tasks according to given criterea and returns an array
     * of sorted tasks. If DEFAULT is given, the tasks are returned in the same
     * order they are kept in container.
     * @param sortCriteria  As defined by SourceCriteria.
     * @return The array of sorted tasks.
     */
    public Task[] getTasks(SortCriteria sortCriteria) {
        Task[] tasks = _taskContainer.getTasks();
        if (sortCriteria == SortCriteria.DEFAULT) {
            return tasks;
        }
        TreeMap<String, Task> sortedMap = new TreeMap<String, Task>();
        for (int i = 0; i < tasks.length; i++) {
            switch (sortCriteria) {
            case BY_NAME:
                // Make the name unique by adding a special character
                // followed by task ID.
                String nameId = tasks[i].getName() + "\u0001" + tasks[i].getId();
                sortedMap.put(nameId, tasks[i]);
                break;
            case BY_ID:
                sortedMap.put(tasks[i].getId(), tasks[i]);
            }
        }
        return sortedMap.values().toArray(new Task[0]);
    }

    /**
     * Adds a task to the project.
     * @param task
     */
    public void addTask(Task task) {
        _taskContainer.addTask(task);
    }

    /**
     * Removes a task from the project.
     * @param taskId	The id of the task to be removed.
     */
    public void removeTask(String taskId) {
        _taskContainer.removeTask(taskId);
    }

    /**
     * @param sortCriteria Order in which the subprojects should be returned. 
     * @return Array of child projects.
     */
    public Project[] getSubprojects(SortCriteria sortCriteria) {
        Project[] subprojects = _projectContainer.getProjects();
        if (sortCriteria == SortCriteria.DEFAULT) {
            return subprojects;
        }
        TreeMap<String, Project> sortedMap = new TreeMap<String, Project>();
        for (int i = 0; i < subprojects.length; i++) {
            switch (sortCriteria) {
            case BY_NAME:
                sortedMap.put(subprojects[i].getName(), subprojects[i]);
                break;
            case BY_ID:
                sortedMap.put(subprojects[i].getId(), subprojects[i]);
            }
        }
        return sortedMap.values().toArray(new Project[0]);
    }

    /**
     * Adds a subproject to the project.
     * @param subproject The subproject to add.
     */
    public void addSubproject(Project subproject) {
        _projectContainer.addProject(subproject);
    }

    /**
     * Remove a specified subproject and its child projects (recursively).
     * Remove also any tasks in them and associated time records.
     * @param subprojectId  The subproject Id.
     * @param timeLog       The time log to remove time records from.
     */
    public void removeSubproject(String subprojectId, TimeLog timeLog) {
        Project subproject = _projectContainer.getProjectById(subprojectId);
        if (subproject != null) {
            subproject.removeAllTasks(timeLog);
            subproject.removeAllSubprojects(timeLog);
            _projectContainer.removeProject(subprojectId);
        }
    }

    private void removeAllSubprojects(TimeLog timeLog) {
        Project[] subprojects = _projectContainer.getProjects();
        for (int i = 0; i < subprojects.length; i++) {
            subprojects[i].removeAllTasks(timeLog);
            subprojects[i].removeAllSubprojects(timeLog);
        }
        _projectContainer.removeAll();
    }

    private void removeAllTasks(TimeLog timeLog) {
        Task tasks[] = _taskContainer.getTasks();
        if (tasks.length == 0) {
            return;
        }
        for (Task task: tasks) {
            TimeRecordFilter taskFilter = new TimeRecordFilter();
            taskFilter.setTask(task);
            timeLog.removeRecords(taskFilter);
        }
        _taskContainer.removeAll();
    }


    /**
     * @return Total time for the project.
     */
    public Duration getTotalDuration(TimeLog timeLog, TimeRecordFilter filter) {
        long projDuration = 0;
        Task tasks[] = getTasks(SortCriteria.DEFAULT);
        for (int i = 0; i < tasks.length; i++) {
            projDuration += tasks[i].getTotalDuration(timeLog, filter)
                    .getValue();
        }
        Project subprojects[] = getSubprojects(SortCriteria.DEFAULT);
        for (int i = 0; i < subprojects.length; i++) {
            projDuration += subprojects[i].getTotalDuration(timeLog, filter)
                    .getValue();
        }
        return new Duration(projDuration);
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String notes) {
        _description = notes;
    }
    
    /**
     * Remove a reference to the item but leave the item as it is without
     * any modifications.
     * @param item
     */
    public void unlinkItem(ProjectTreeItem item) {
        if (item instanceof Task) {
            _taskContainer.removeTask(item.getId());
        }
        else if (item instanceof Project) {
            _projectContainer.removeProject(item.getId());
        }
        item.setParent(null);
    }
    
    @Override
    public ItemType getItemType() {
        return ItemType.PROJECT;
    }
    
    
    /**
     * Checks if the project has any open items (tasks, activities, subprojects) under
     * it.
     * @return True if there are open items.
     */
    public boolean hasOpenItems() {
        Project subprojects[] = this.getSubprojects(SortCriteria.DEFAULT);
        for (Project subproject: subprojects) {
            if (!subproject.isClosed() || subproject.hasOpenItems()) {
                return true;
            }
        }
        for (Task task: getTasks(SortCriteria.DEFAULT)) {
            if (!task.isClosed()) {
                return true;
            }
        }
        return false;
    }
    
    
    @Override
    public boolean isComposite() {
        return true;
    }

    @Override
    public boolean mayHaveDeadline() {
        return true;
    }


    @Override
    public boolean isDeletable() {
        return true;
    }
    
    
    private TaskContainer _taskContainer = null;
    private ProjectContainer _projectContainer = null;
    private String _description = "";

}
