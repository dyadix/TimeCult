/*
 * Copyright (c) Rustam Vishnyakov, 2009-2010 (dyadix@gmail.com)
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
 * $Id: TimeFilterToolbar.java,v 1.4 2010/09/26 12:13:31 dyadix Exp $
 */
package net.sf.timecult.ui.swt.filter;

import net.sf.timecult.ui.swt.ToolBarBase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;

public class TimeFilterToolbar extends ToolBarBase {

    private AdvancedTimeFilterView filterView;
    private ToolItem     deleteFilter;

    public TimeFilterToolbar(AdvancedTimeFilterView filterView, Composite contentPanel) {
        super(contentPanel, filterView.getMainWindow().getIconSet(), 1);
        this.filterView = filterView;
    }

    @Override
    protected void setup(int toolBarNumber) {
        switch (toolBarNumber) {
            case 0:

                ToolItem customFilter = createButton("filter.add", SWT.PUSH);
                customFilter.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent evt) {
                        FilterSetupDialog addDialog = new FilterSetupDialog(filterView);
                        addDialog.open();
                    }
                });

                this.deleteFilter = createButton("filter.delete", SWT.PUSH);
                this.deleteFilter.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent evt) {
                        filterView.deleteSelectedFilter();
                    }
                });
                this.deleteFilter.setEnabled(false);
        }
    }
    
    public void setDeleteEnabled(boolean enabled) {
        this.deleteFilter.setEnabled(enabled);
    }
           
}
