/*
 * File: MemTaskContainer.java
 * Created: 21.05.2005
 *
 * Copyright (c) Rustam Vishnyakov, 2005-2006 (rvishnyakov@yahoo.com)
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
 */
package net.sf.timecult.model.mem;
import java.util.*;
import net.sf.timecult.model.*;

/**
 * Container for the tasks residing in memory.
 * @author rvishnyakov (rvishnyakov@yahoo.com)
 */
public class MemTaskContainer implements TaskContainer
{

    /**
     * @return Array of tasks for the project.
     */
    public Task[] getTasks()
    {
        Task taskArray[] = new Task[_tasks.size()];
        int i = 0;
        for (Iterator iter = _tasks.values().iterator(); iter.hasNext();)
        {
            taskArray[i] = (Task) iter.next();
            i++;
        }
        return taskArray;
    }

    /**
     * Adds a task.
     * @param task The task to add.
     */
    public void addTask(Task task)
    {
        _tasks.put(task.getId(), task);
    }
    
    /**
     * Removes the task
     * @param id	The id of the task to be removed.
     */
    public void removeTask(String id)
    {
        if(_tasks.containsKey(id))
        {
            _tasks.remove(id);
        }
    }
    
    public void removeAll()
    {
        _tasks.clear();
    }
    
    private TreeMap<String,Task> _tasks = new TreeMap<String,Task>();

}
