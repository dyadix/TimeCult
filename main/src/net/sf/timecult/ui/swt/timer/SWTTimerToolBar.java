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
 * $Id: SWTTimerToolBar.java,v 1.5 2009/02/20 20:46:34 dyadix Exp $
 */
package net.sf.timecult.ui.swt.timer;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.stopwatch.Stopwatch;
import net.sf.timecult.ui.swt.IconSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Timer tool bar wrapper.
 */
public class SWTTimerToolBar {
    private final ToolBar        toolBar;
    private       ToolItem       pauseButton;
    private final SWTTimerWindow parent;

    private final IconSet iconSet;
    private final Image   startImage;
    private final Image   pauseImage;

    public SWTTimerToolBar(SWTTimerWindow parent, Shell shell) {
        this.parent = parent;
        iconSet = parent.getParent().getIconSet();
        this.toolBar = new ToolBar(shell, SWT.FLAT);
        setup();
        startImage = iconSet.getIcon("timer.start", true);
        pauseImage = iconSet.getIcon("timer.pause", true);
    }

    private void setup() {
        createPauseButton();
        createStopButton();
        createMinToTrayButton();
    }

    private void createStopButton() {
        ToolItem stopButton = createButton("stop");
        stopButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                SWTTimerToolBar.this.parent.terminate();
            }
        });
    }

    private void createPauseButton() {
        this.pauseButton = createButton("pauseResume");
        this.pauseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                pauseOrResume();
            }
        });
    }

    private void createMinToTrayButton() {
        ToolItem minToTrayButton = createButton("minToTray");
        minToTrayButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                SWTTimerToolBar.this.parent.hide();
            }
        });
    }

    private ToolItem createButton(String tag) {
        ToolItem item = new ToolItem(this.toolBar, SWT.PUSH);
        item.setToolTipText(ResourceHelper.getString("button." + tag
            + ".tooltip"));
        item.setImage(iconSet.getIcon("timer." + tag, true));
        item.setDisabledImage(iconSet.getIcon(tag, false));
        return item;
    }


    public void pauseOrResume() {
        Stopwatch sw = this.parent.getStopwatch();
        if (sw.isRunning()) {
            this.pauseButton.setImage(startImage);
            sw.pause();
        } else {
            this.pauseButton.setImage(pauseImage);
            sw.resume();
        }
    }


}
