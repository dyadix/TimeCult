/*
 * Copyright (c) Rustam Vishnyakov, 2009 (dyadix@gmail.com)
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
 * $Id: CustomFilterList.java,v 1.3 2009/09/20 18:53:21 dyadix Exp $
 */
package net.sf.timecult.model;

import java.util.HashMap;

/**
 * Contains custom filters created by end user.
 */
public class CustomFilterList {
            
    private HashMap<String,TimeRecordFilter> filters;
    private Workspace workspace;
    
    public CustomFilterList(Workspace workspace) {
        this.filters = new HashMap<String,TimeRecordFilter>();
        this.workspace = workspace;
    }

    public void addFilter(TimeRecordFilter filter) {
        this.filters.put(filter.getLabel(), filter);
    }
    
    public TimeRecordFilter getFilter(String label) {
        return this.filters.get(label);
    }
    
    public TimeRecordFilter[] asArray() {
        return this.filters.values().toArray(new TimeRecordFilter[0]);
    }
    
    public void removeFilter(TimeRecordFilter filter) {
        this.filters.remove(filter.getLabel());
        this.workspace.setFilter(null);
    }
}
