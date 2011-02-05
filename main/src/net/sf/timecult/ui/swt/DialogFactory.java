/*
 * Copyright (c) Rustam Vishnyakov, 2010 (dyadix@gmail.com)
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
 * $Id: DialogFactory.java,v 1.2 2010/11/30 13:43:13 dyadix Exp $
 */

package net.sf.timecult.ui.swt;

import net.sf.timecult.model.Activity;
import net.sf.timecult.model.IdleTask;
import net.sf.timecult.model.Project;
import net.sf.timecult.model.ProjectTreeItem;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.Workspace;

public class DialogFactory {

    public static SWTDialog createPropertyDialog(SWTMainWindow mainWindow,
        ProjectTreeItem item) {
        switch (item.getItemType()) {
        case WORKSPACE:
            return new WorkspaceEditDialog(mainWindow, (Workspace) item);
        case PROJECT:
            return new SWTProjectEditDialog(mainWindow, (Project) item);
        case TASK:
            return new SWTTaskEditDialog(mainWindow, (Task) item);
        case ACTIVITY:
            return new ActivityEditDialog(mainWindow, (Activity) item);
        case IDLE_TASK:
            return new IdleTaskEditDialog(mainWindow, (IdleTask) item);
        }
        return null;
    }
}
