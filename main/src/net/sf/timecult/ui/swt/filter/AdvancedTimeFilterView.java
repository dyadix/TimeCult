/*
 * Copyright (c) Rustam Vishnyakov, 2005-2009 (dyadix@gmail.com)
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
 * $Id: AdvancedTimeFilterView.java,v 1.5 2010/09/26 12:13:31 dyadix Exp $
 */
package net.sf.timecult.ui.swt.filter;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.CustomFilterList;
import net.sf.timecult.model.TimeRecordFilter;
import net.sf.timecult.model.TimeRecordFilterFactory;
import net.sf.timecult.model.Workspace;
import net.sf.timecult.ui.swt.SWTMainWindow;

/**
 * Replaces SWTTimeFilterView. Allows to add custom filters.
 */
public class AdvancedTimeFilterView {
    
    private TimeTracker              tracker;
    private List                     filterList;
    private SWTMainWindow            mainWindow;
    private HashMap<Integer, String> indexToLabel = new HashMap<Integer, String>();
    private TimeFilterToolbar        toolbar;
    
    public AdvancedTimeFilterView(SWTMainWindow mainWindow) {
        this.tracker = TimeTracker.getInstance();
        this.mainWindow = mainWindow;
        setup();
    }

    private void setup() {
        GridLayout contentLayout = new GridLayout();
        contentLayout.numColumns = 1;
        contentLayout.makeColumnsEqualWidth = true;
        Composite contentPanel = new Composite(this.mainWindow.getFilterContainer(), SWT.BORDER);
        toolbar = new TimeFilterToolbar(this, contentPanel);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        contentPanel.setLayout(contentLayout);
        contentPanel.setLayoutData(gridData);
        
        //toolbar = new TimeFilterToolbar(this, contentPanel);
        
        createFilterList(contentPanel);
    }

    private List createFilterList(Composite contentPanel) {        
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        this.filterList = new List(contentPanel, SWT.BORDER | SWT.V_SCROLL);
        this.filterList.setLayoutData(gridData);
        updateFilterList();
        this.filterList.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent evt) {
                tracker.getWorkspace().setFilter(getSelectedFilter());                
            }
        });
        return this.filterList;
    }
    
    
    /**
     * Redraw the list of filters using the current workspace.
     */
    public void updateFilterList() {
        this.filterList.removeAll();
        //
        // Add built-in filters
        //
        String[] filterLabels = TimeRecordFilterFactory.getFilterLabels();
        TimeRecordFilter defaultFilter = this.tracker.getWorkspace().getFilter();
        for (int i = 0; i < filterLabels.length; i ++) {
            String filterName = ResourceHelper.getString("filter." + filterLabels[i]);
            this.indexToLabel.put(new Integer(i), filterLabels[i]);            
            this.filterList.add(filterName);
            if (defaultFilter != null && defaultFilter.getLabel().equals(filterLabels[i])) {
                this.filterList.select(i);
            }
        }
        //
        // Add custom filters (stored in the workspace)
        //
        Workspace ws = this.tracker.getWorkspace();
        for (TimeRecordFilter customFilter : ws.getCustomFilters().asArray()) {
            String filterName = customFilter.getLabel();            
            this.filterList.add(filterName);
            int index = this.filterList.indexOf(filterName);
            this.indexToLabel.put(new Integer(index), filterName);
            if (defaultFilter != null && defaultFilter.getLabel().equals(filterName)) {
                this.filterList.select(index);
            }
        }
        
    }
    
    
    public void setFilterSelection(TimeRecordFilter filter) {
        boolean selected = false;
        if (filter != null) {
            String lcName = "";
            if (!filter.isCustom()) {
                lcName = ResourceHelper.getString("filter."
                    + filter.getLabel());
                toolbar.setDeleteEnabled(false);
            }
            else {
                lcName = filter.getLabel();
                toolbar.setDeleteEnabled(true);
            }
            String items[] = this.filterList.getItems();
            for (int i = 0; i < this.filterList.getItemCount() && !selected; i++) {
                if (items[i].equals(lcName)) {
                    this.filterList.setSelection(i);
                    this.filterList.showSelection();
                    selected = true;
                }
            }
        }
        if (!selected) {
            this.filterList.deselectAll();
        }
    }
    
    public SWTMainWindow getMainWindow() {
        return this.mainWindow;
    }

    /**
     * Add custom filter to the workspace and set it as a current filter.
     * @param filter The filter to add.
     */
    public void addCustomFilter(TimeRecordFilter filter) {        
        this.filterList.add(filter.getLabel());
        int index = this.filterList.indexOf(filter.getLabel());
        this.indexToLabel.put(new Integer(index), filter.getLabel());
        Workspace ws = TimeTracker.getInstance().getWorkspace();
        CustomFilterList customFilters = ws.getCustomFilters();
        customFilters.addFilter(filter);
        ws.setFilter(filter);
        setFilterSelection(filter);
        toolbar.setDeleteEnabled(true);
    }
    
    public void deleteSelectedFilter() {
        TimeRecordFilter filter = getSelectedFilter();
        if (filter != null && filter.isCustom()) {
            tracker.getWorkspace().getCustomFilters().removeFilter(filter);
            toolbar.setDeleteEnabled(false);
            updateFilterList();
        }
    }
    
    private TimeRecordFilter getSelectedFilter() {
        Integer index = new Integer(AdvancedTimeFilterView.this.filterList.getSelectionIndex());
        String label = AdvancedTimeFilterView.this.indexToLabel.get(index);
        if (label != null) {
            toolbar.setDeleteEnabled(false);
            TimeRecordFilter filter = TimeRecordFilterFactory.createFilter(label);
            if (filter == null) {
                CustomFilterList customFilters = TimeTracker
                    .getInstance().getWorkspace().getCustomFilters();
                filter = customFilters.getFilter(label);
                if (filter != null) {
                    toolbar.setDeleteEnabled(true);
                }
            }
            return filter;
        }
        return null;
    }

}
