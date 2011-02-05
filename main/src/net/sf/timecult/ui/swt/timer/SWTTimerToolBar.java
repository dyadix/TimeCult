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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Timer tool bar wrapper.
 * @author rvishnyakov
 */
public class SWTTimerToolBar {

	public SWTTimerToolBar(SWTTimerWindow parent, Shell shell, GridData gridData) {
		_parent = parent;
		setup(shell, gridData);
	}

	private void setup(Shell shell, GridData gridData) {
		_toolBar = new ToolBar(shell, SWT.FLAT);
		//_toolBar.setLayoutData(gridData);		
		createPauseButton();
		createStopButton();
        createMinToTrayButton();
	}

	private ToolItem createStopButton() {
		_stopButton = createButton("stop", "stop.png", "stop.png");
		_stopButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				_parent.terminate();
			}
		});
		return _stopButton;
	}

	private ToolItem createPauseButton() {
		_pauseButton = createButton("pauseResume", "control_pause_blue.png",
				"control_pause_blue.png");
		_pauseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
                pauseOrResume();
			}
		});
		return _pauseButton;
	}
    
    private ToolItem createMinToTrayButton() {
        this.minToTrayButton = createButton("minToTray", "min_to_tray.png", "min_to_tray.png");
        this.minToTrayButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                SWTTimerToolBar.this._parent.hide();
            }
        });
        return this.minToTrayButton;
    }

	private ToolItem createButton(String tag, String imageEnabled,
			String imageDisabled) {
		ToolItem item = new ToolItem(_toolBar, SWT.PUSH);
		item.setToolTipText(ResourceHelper.getString("button." + tag
				+ ".tooltip"));
		item.setImage(new Image(_toolBar.getShell().getDisplay(),
				ResourceHelper.openStream("images/" + imageEnabled)));
		item.setDisabledImage(new Image(_toolBar.getShell().getDisplay(),
				ResourceHelper.openStream("images/" + imageDisabled)));
		return item;
	}
    
    
    public void pauseOrResume() {
        Stopwatch sw = _parent.getStopwatch();
        if (sw.isRunning()) {
            _pauseButton.setImage(new Image(_toolBar.getShell()
                    .getDisplay(), ResourceHelper
                    .openStream("images/control_play_blue.png")));
            sw.pause();
        } else {
            _pauseButton.setImage(new Image(_toolBar.getShell()
                    .getDisplay(), ResourceHelper
                    .openStream("images/control_pause_blue.png")));
            sw.resume();
        }
    }

	private ToolBar        _toolBar     = null;
    private ToolItem       _stopButton  = null;
    private ToolItem       _pauseButton = null;
    private SWTTimerWindow _parent      = null;
    private ToolItem       minToTrayButton;

}
