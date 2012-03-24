/*
 * Copyright (c) Rustam Vishnyakov, 2005-2007 (rvishnyakov@yahoo.com)
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
 * $Id: SWTNotesDialog.java,v 1.6 2007/09/15 07:19:33 dyadix Exp $
 */
package net.sf.timecult.ui.swt.timer;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.TimeRecord;
import net.sf.timecult.ui.swt.SWTDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A dialog to add notes
 * @author rvishnyakov
 */
public class SWTNotesDialog extends SWTDialog {
    
    public static int MAX_NOTE_LENGTH = 80;

	public SWTNotesDialog(SWTTimerWindow timerWindow, Task task) {
		super(timerWindow.getParent(), false);
		_timerWindow = timerWindow;
		_task = task;
		this.setText(_task.getName());
	}

	public String getNotes() {
		return _notesField.getText();
	}

	@Override
	protected Composite createContentPanel(Shell shell) {
        Composite textPanel = new Composite(shell, SWT.BORDER);
        GridLayout grid = new GridLayout();
        grid.numColumns = 2;
        textPanel.setLayout(grid);
        Label label = new Label(textPanel, SWT.None);
        label.setText("Notes:");
        GridData projectNameLayout = new GridData(GridData.FILL_HORIZONTAL);
        projectNameLayout.widthHint = 500;
        _notesField = new Text(textPanel, SWT.LEFT | SWT.BORDER );
        _notesField.setLayoutData(projectNameLayout);
        _notesField.addKeyListener(getDefaultKeyListener());
        _notesField.setFocus();
        return textPanel;
	}

	@Override
    protected boolean handleOk() {
        TimeRecord timeRec = new TimeRecord(_task, _timerWindow.getStopwatch()
            .getStartTime(), _timerWindow.getStopwatch().getDuration(),
            _notesField.getText());
        TimeTracker.getInstance().getWorkspace().recordTime(timeRec);
        return true;
    }

    @Override
    protected String getTitle() {
        return "Notes"; // TODO: Localize
    }
    
	private Text           _notesField  = null;
    private SWTTimerWindow _timerWindow = null;
    private Task           _task        = null;

}
