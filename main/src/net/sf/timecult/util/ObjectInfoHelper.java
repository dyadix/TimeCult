/*
 * Copyright (c) Rustam Vishnyakov, 2007-2010 (dyadix@gmail.com)
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
 * $Id: ObjectInfoHelper.java,v 1.3 2010/04/12 14:58:49 dyadix Exp $
 */
package net.sf.timecult.util;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.model.ProjectTreeItem;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.TaskStatus;

public class ObjectInfoHelper {
    
    public static String getObjectInfo(Object o) {
        StringBuilder infoBuf = new StringBuilder();
        if (o instanceof ProjectTreeItem) {
            ProjectTreeItem item = (ProjectTreeItem) o;
            if (item instanceof Task) {
                infoBuf.append(taskStatusToLocalizedString((Task)item));
            }
            if (infoBuf.length() != 0) {
                infoBuf.append(". ");
            }
            if (item.getCloseDateTime() != null) {
                infoBuf.append(ResourceHelper.getString("status.closed")).append(": ");
                infoBuf.append(item.getCloseDateTime().toString());
            }
            else if (item.getCreationDateTime().getTime() != 0) {
                infoBuf.append(ResourceHelper.getString("status.created")).append(": ");
                infoBuf.append(Formatter.toDateTimeString(item.getCreationDateTime(), true));
            }
        }
        return infoBuf.toString();
    }
    
    
    public static String taskStatusToLocalizedString(Task task) {
        String statusStr = task.getStatusAsString();
        String localStr = ResourceHelper.getString("task.status." + statusStr);
        if (task.getStatus().getId() == TaskStatus.WAITING
            && task.getWaitReason() != null) {
            localStr = localStr + " " + task.getWaitReason().getText();
        }
        return localStr;
    }
    
}
