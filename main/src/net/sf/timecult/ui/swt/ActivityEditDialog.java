/*
 * Copyright (c) TimeCult Project Team, 2005-2023 (dyadix@gmail.com)
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
package net.sf.timecult.ui.swt;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.Activity;
import net.sf.timecult.model.ProjectTreeItem.ItemType;

public class ActivityEditDialog extends ProjectItemEditDialog {
    
    private Activity activity; 
    
	public ActivityEditDialog(SWTMainWindow parent, Activity activity) {
		super(parent, activity);
		this.activity = activity;
	}
    
    public ActivityEditDialog(SWTMainWindow parent) {
        super(parent, ItemType.ACTIVITY);
    }
	
		
    @Override
    protected boolean afterUpdate() {
        TimeTracker.getInstance().updateTask();
        return true;
    }

	
    @Override
    protected String getTitle() {        
        if (this.activity == null) {
            return ResourceHelper.getString("dialog.newActivity");
        }
        else {
            return ResourceHelper.getString("dialog.properties.activity");
        }
    }
    


}
