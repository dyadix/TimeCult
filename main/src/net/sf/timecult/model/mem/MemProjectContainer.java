/*
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
 * 
 * $Id: MemProjectContainer.java,v 1.3 2007/05/30 17:57:17 dyadix Exp $ 
 */
package net.sf.timecult.model.mem;

import java.util.Iterator;
import java.util.TreeMap;
import net.sf.timecult.model.*;

/**
 * Stores the projects in memory.
 * @author rvishnyakov (rvishnyakov@yahoo.com)
 */
public class MemProjectContainer implements ProjectContainer
{


    /**
     * @return Subprojects
     */
    public Project[] getProjects()
    {
        Project subprojArray[] = new Project[_projects.size()];
        int i = 0;
        for(Iterator iter = _projects.values().iterator();iter.hasNext();)
        {
            subprojArray[i] = (Project)iter.next();
            i ++;
        }
        return subprojArray;
    }


    /**
     * Adds a project.
     * @param project The project to add.
     */
    public void addProject(Project project)
    {
        _projects.put(project.getId(), project);
    }
    
    
    /**
     * Removes a project
     * @param id	The id of the project to be removed.
     */
    public void removeProject(String id)
    {
        if(_projects.containsKey(id))
        {
            _projects.remove(id);
        }
    }
    
    public void removeAll()
    {
        _projects.clear();
    }
    
    public Project getProjectById(String id)
    {
        return (Project)_projects.get(id);
    }
    
    private TreeMap<String,Project> _projects = new TreeMap<String,Project>();
}
