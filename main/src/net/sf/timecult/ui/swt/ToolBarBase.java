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
 * $Id: ToolBarBase.java,v 1.4 2010/09/28 15:41:17 dyadix Exp $
 */

package net.sf.timecult.ui.swt;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.model.Activity;
import net.sf.timecult.model.TaskStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;

import java.util.ArrayList;

/**
 * A base class for other toolbars.
 */
public abstract class ToolBarBase {

    private CoolBar coolBar;
    private ArrayList<ToolBar> toolBars = new ArrayList<ToolBar>();
    private IconSet iconSet;
    private ToolBar currToolBar;

    public ToolBarBase(Composite composite, IconSet iconSet, int nToolBars) {
        this(composite, iconSet, nToolBars, null);
    }

    public ToolBarBase(Composite composite, IconSet iconSet, int nToolBars, Color background) {
        coolBar = new CoolBar(composite, SWT.FLAT);
        coolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        coolBar.setBackground(background);
        this.iconSet = iconSet;

        for (int i = 0; i < nToolBars; i ++) {
            currToolBar = new ToolBar(this.coolBar, SWT.BORDER_DOT);
            setup(i);
            currToolBar.pack();

            Point size = currToolBar.getSize();
            CoolItem item = new CoolItem(coolBar, SWT.NONE);
            item.setControl(currToolBar);
            Point preferred = item.computeSize(size.x + 5, size.y);
            item.setPreferredSize(preferred);
        }
    }

    protected abstract void setup(int toolBarNumber);


    protected ToolItem createButton(String tag, int type) {
        ToolItem item = new ToolItem(currToolBar, type);
        item.setToolTipText(ResourceHelper.getString("button." + tag
                + ".tooltip"));
        item.setImage(this.iconSet.getIcon(tag, true));
        item.setDisabledImage(this.iconSet.getIcon(tag, false));
        return item;
    }

}
