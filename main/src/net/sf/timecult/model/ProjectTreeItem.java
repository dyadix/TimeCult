/*
 * Copyright (c) Rustam Vishnyakov, 2007-2009 (dyadix@gmail.com)
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
 * $Id: ProjectTreeItem.java,v 1.12 2010/11/30 15:47:56 dyadix Exp $ 
 */
package net.sf.timecult.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Generic named project tree item identified by unique ID. 
 * 
 * @author rvishnyakov (dyadix@gmail.com)
 */
public abstract class ProjectTreeItem {
    
    public enum ItemType {
        TASK, PROJECT, WORKSPACE, IDLE_TASK, ACTIVITY
    }

    private String name;
    private String id;
    protected Project parent;
    private boolean expanded = false;
    private Date creationDateTime;
    private Date closeDateTime;
    private Date deadline;
    private String hyperlink;
    
    public ProjectTreeItem(String id, String name, Project parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.creationDateTime = new Date(0);
        this.closeDateTime = null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String toString() {
        return this.name + " [id: " + this.id + "]";
    }
    
    public Project getParent() {
        return parent;
    }
    
    public void setParent(Project parent) {
        this.parent = parent;
    }
    
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    public boolean isExpanded() {
        return this.expanded;
    }
    
    
    public Date getCreationDateTime() {
        return this.creationDateTime;
    }
    
    public void setCreationDateTime(Date creationDateTime) {
        this.creationDateTime = creationDateTime;
    }    
    
    public Date getCloseDateTime() {
        return this.closeDateTime;
    }
    
    public void setCloseDateTime(Date closeDateTime) {
        this.closeDateTime = closeDateTime;
    }
    
    public boolean isClosed() {
        return (this.closeDateTime != null);
    }
    
    public boolean hasClosedParent() {
        return checkClosedParent(this);
    }
    
    private static boolean checkClosedParent(ProjectTreeItem item) {
        ProjectTreeItem parentItem = item.getParent();
        if (parentItem == null) {
            return false;
        }
        if (parentItem.isClosed()) {
            return true;
        }
        return checkClosedParent(parentItem);
    }
    
    public abstract ItemType getItemType();

    public String getHyperlink() {
        return hyperlink;
    }

    
    /**
     * Sets a hyperlink if it is a non-empty string. Otherwise removes it (sets to null).
     * @param hyperlink The hyperlink to set.
     */
    public void setHyperlink(String hyperlink) {
        if (hyperlink != null && !hyperlink.isEmpty()) {
            this.hyperlink = hyperlink;
        }
        else {
            this.hyperlink = null;
        }
    }
    
    
    /**
     * Tells whether or not the item can be deleted from the project tree. It also
     * tells whether or not the item can be moved with cut and paste operations.
     * @return True if the item can be deleted.
     */
    public abstract boolean isDeletable();

    
    /**
     * Tells whether or not the item may contain other (child) items.
     * @return True if the item is composite.
     */
    public abstract boolean isComposite();
    
    /**
     * Checks whether the item belongs to project, not necessarily as its immediate child.
     * @param project The project containing the item as one of its children.
     * @return True if the item belongs to the project. False otherwise.
     */
    public boolean belongsTo(Project project) {
        if (this.parent == null) return false;
       if (project == this.parent) return true;       
       return this.parent.belongsTo(project);
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public abstract boolean mayHaveDeadline();

    public boolean isPastDeadline() {
        return !isClosed() && deadline != null && deadline.compareTo(Calendar.getInstance().getTime()) < 0;
    }
}
