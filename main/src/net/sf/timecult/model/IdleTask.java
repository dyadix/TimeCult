/*
 * Copyright (c) Rustam Vishnyakov, 2005-2008 (rvishnyakov@yahoo.com)
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
package net.sf.timecult.model;

import net.sf.timecult.ResourceHelper;

/**
 * Idle task is a special case which redefines some standard behaviour.
 * For example, it doesn't allow to launch standalone timers for it. 
 * @author rvishnyakov
 */
public class IdleTask extends Task {

    public final static String ID           = "idle";
    private final       Totals EMPTY_TOTALS = new Totals();

    private boolean   isEnabled = false;
    private Workspace workspace = null;
    private String    defaultNote;

    public IdleTask(Workspace workspace) {
        super(workspace, ID, ResourceHelper.getString("workspace.idle"));
        this.workspace = workspace;
    }

    public boolean isStandaloneTimerEnabled() {
        return false;
    }

    public boolean isDeletable() {
        return false;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        workspace.fireWorkspaceChanged(new WorkspaceEvent(
                WorkspaceEvent.TASK_STATUS_CHANGED, this));
    }

    @Override
    public ItemType getItemType() {
        return ItemType.IDLE_TASK;
    }

    @Override
    public boolean mayHaveDeadline() {
        return false;
    }


    public String getDefaultNote() {
        return defaultNote;
    }

    public void setDefaultNote(String defaultNote) {
        this.defaultNote = defaultNote;
    }


}
