/*
 * Copyright (c) Rustam Vishnyakov, 2005-2007 (dyadix@gmail.com)
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
 * $Id: SWTTimeFilterView.java,v 1.4 2007/12/21 20:00:30 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.List;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.TimeRecordFilter;
import net.sf.timecult.model.TimeRecordFilterFactory;

public class SWTTimeFilterView {
    
    private TimeTracker              tracker;
    private List                     filterList;
    private SWTMainWindow            mainWindow;
    private HashMap<Integer, String> indexToLabel = new HashMap<Integer, String>();
    
    public SWTTimeFilterView(SWTMainWindow mainWindow) {
        this.tracker = TimeTracker.getInstance();
        this.mainWindow = mainWindow;
        setup();
    }

    private void setup() {
        createFilterList();
    }

    private List createFilterList() {        
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        this.filterList = new List(this.mainWindow.getFilterContainer(), SWT.BORDER);
        this.filterList.setLayoutData(gridData);
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
        this.filterList.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent evt) {
                Integer index = new Integer(SWTTimeFilterView.this.filterList.getSelectionIndex());
                String label = SWTTimeFilterView.this.indexToLabel.get(index);
                if (label != null) {
                    TimeRecordFilter filter = TimeRecordFilterFactory.createFilter(label);
                    tracker.getWorkspace().setFilter(filter);
                }                
            }
        });
        return this.filterList;
    }    
    
    public void setFilterSelection(TimeRecordFilter filter) {
        boolean selected = false;
        if (filter != null) {
            String lcName = ResourceHelper.getString("filter."
                + filter.getLabel());
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


}
