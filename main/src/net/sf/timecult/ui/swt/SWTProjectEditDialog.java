/*
 * Copyright (c) Rustam Vishnyakov, 2005-2008 (dyadix@gmail.com)
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
 * $Id: SWTProjectEditDialog.java,v 1.13 2010/02/20 12:55:00 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.Project;
import net.sf.timecult.model.Workspace;
import net.sf.timecult.model.ProjectTreeItem.ItemType;

public class SWTProjectEditDialog extends ProjectItemEditDialog {
    

	public SWTProjectEditDialog(SWTMainWindow parent, Project project) {
		super(parent, project);
		_project = project;
	}

    
    public SWTProjectEditDialog(SWTMainWindow parent) {
        super(parent, ItemType.PROJECT);
    }
    
	
    @Override
    protected boolean afterUpdate() {
        TimeTracker.getInstance().updateProject(_project);
        if (_project instanceof Workspace) {
            getMainWindow().updateTitle();
        }
        return true;
    }
	

    @Override
    protected String getTitle() {
        if (_project == null) {
            return ResourceHelper.getString("dialog.newProject");
        }
        else {
            return ResourceHelper.getString("dialog.properties.project");
        }
    }    
		
	private Project _project;

}
