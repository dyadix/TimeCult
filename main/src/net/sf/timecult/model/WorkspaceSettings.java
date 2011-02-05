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
 */
package net.sf.timecult.model;

/**
 * Contains main workspace configuration settings.
 * @author rvishnyakov
 */
public class WorkspaceSettings {
    
    private long roundUpInterval = 0; // in milliseconds
    private Workspace workspace;

    public WorkspaceSettings(Workspace workspace) {
        this.workspace = workspace;
    }
    
    public long getRoundUpInterval() {
        return roundUpInterval;
    }
    
    public void setRoundUpInterval(long interval) {
        this.roundUpInterval = interval;
        fireSettingsUpdated();
    }

    public void setRoundUpIntervalInMinutes(int roundUpMin) {
        this.roundUpInterval = roundUpMin * 60000;
        fireSettingsUpdated();
    }
    
    public int getRoundUpIntervalInMinutes() {
        return (int) (roundUpInterval / 60000);
    }
    
    public void fireSettingsUpdated() {
        this.workspace.fireWorkspaceChanged(new WorkspaceEvent(WorkspaceEvent.SETTINGS_UPDATED, this));
    }
        
}
