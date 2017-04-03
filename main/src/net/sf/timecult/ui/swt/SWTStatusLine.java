/*
 * Copyright (c) Rustam Vishnyakov, 2005-2010 (dyadix@gmail.com)
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
 * $Id: SWTStatusLine.java,v 1.12 2011/01/16 05:22:53 dragulceo Exp $
 */
package net.sf.timecult.ui.swt;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.model.DescriptionHolder;
import net.sf.timecult.model.ProjectTreeItem;
import net.sf.timecult.model.WorkspaceEvent;
import net.sf.timecult.model.WorkspaceListener;
import net.sf.timecult.util.Formatter;
import net.sf.timecult.util.ObjectInfoHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class SWTStatusLine implements WorkspaceListener {

    private Label         _currTimeLabel  = null;
    private Label         _selectionLabel = null;
    private Label         _idleLabel      = null;
    private CurrTimeClock _clock          = new CurrTimeClock();
    private SWTMainWindow _mainWindow     = null;
    private long          _idleDuration   = 0;
    private Label _hyperlinkIndicator;
    private Label _hyperlinkLabel;
    private Image _hyperlinkImage;
    private Label _notesLabel;
    private Image _notesImage;

    public SWTStatusLine(SWTMainWindow mainWindow) {
        _mainWindow = mainWindow;
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
        gridData.horizontalSpan = 2;
        GridLayout grid = new GridLayout();
        grid.numColumns = 10;
        grid.makeColumnsEqualWidth = false;
        Composite statusComposite = new Composite(mainWindow.getShell(), SWT.BORDER);
        statusComposite.setLayout(grid);
        statusComposite.setLayoutData(gridData);
        _selectionLabel = createLabel(statusComposite, -1, 200, true, SWT.LEFT);
        createSeparator(statusComposite);
        _notesLabel = createLabel(statusComposite, 16, 16, false, SWT.CENTER);
        createSeparator(statusComposite);
        _hyperlinkIndicator = createLabel(statusComposite, 16, 16, false, SWT.CENTER);
        _hyperlinkLabel = createLabel(statusComposite, 300, 300, true, SWT.LEFT);
        createSeparator(statusComposite);
        _idleLabel = createLabel(statusComposite, 100, 100, true, SWT.CENTER);
        createSeparator(statusComposite);
        _currTimeLabel = createLabel(statusComposite, -1, 180, true, SWT.RIGHT);
        IconSet iconSet = _mainWindow.getIconSet();
        _hyperlinkImage = iconSet.getIcon("link", true);
        _notesImage = iconSet.getIcon("notes", true);
    }

    public void setSelectionLabel(String s) {
        _selectionLabel.setText(s);
    }

    private Label createLabel(Composite c, int widthHint, int minWidth, boolean grabSpace, int align) {
        return createLabel(c, widthHint, minWidth, grabSpace, false, align);
    }
    private void createSeparator(Composite c) {
        createLabel(c, -1, 5, false, true, SWT.NONE);
    }

    private Label createLabel(Composite c, int widthHint, int minWidth, boolean grabSpace, boolean isSeparator, int align) {
        GridData labelData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        if (widthHint > 0) labelData.widthHint = widthHint;
        labelData.minimumWidth = minWidth;
        labelData.minimumHeight = 20;
        labelData.grabExcessHorizontalSpace = grabSpace;
        labelData.horizontalAlignment = SWT.FILL;
        if (isSeparator) labelData.heightHint = 20;
        Label l = new Label(c, isSeparator ? SWT.SEPARATOR : align);
        l.setText("");
        l.setLayoutData(labelData);
        return l;
    }

    public void setSelection(Object o) {
        _selectionLabel.setText(ObjectInfoHelper.getObjectInfo(o));
        if (o instanceof ProjectTreeItem) {
            ProjectTreeItem item = (ProjectTreeItem) o;
            if (item.getHyperlink() != null) {
                _hyperlinkIndicator.setImage(_hyperlinkImage);
                _hyperlinkLabel.setToolTipText(item.getHyperlink());
                if (item.getHyperlink().length() < 50) {
                    _hyperlinkLabel.setText(item.getHyperlink());
                } else {
                    _hyperlinkLabel.setText("..."
                        + item.getHyperlink().substring(
                        item.getHyperlink().length() - 50,
                        item.getHyperlink().length()));
                }
            } else {
                _hyperlinkIndicator.setImage(null);
                _hyperlinkLabel.setToolTipText("");
                _hyperlinkLabel.setText("");
            }
        }
        updateNotesIndicator(o);
    }


    private void updateNotesIndicator(Object o) {
        if (o == null) {
            _notesLabel.setImage(null);
            return;
        }
        if (o instanceof DescriptionHolder) {
            String description = ((DescriptionHolder) o).getDescription();
            if (description != null && !description.isEmpty()) {
                _notesLabel.setImage(_notesImage);
            } else {
                _notesLabel.setImage(null);
            }
        }
    }


    private class CurrTimeClock extends TimerTask {

        public CurrTimeClock() {
            Timer timer = new Timer();
            timer.schedule(this, 0, 1000);
        }

        /**
         * Runs in an endless loop showing the current time.
         */
        public void run() {
            if (!_mainWindow.getShell().isDisposed()) {
                Display display = _mainWindow.getShell().getDisplay();
                display.asyncExec(new Runnable() {
                    public void run() {
                        if (!_currTimeLabel.isDisposed()) {
                            _currTimeLabel.setText(Formatter.toDateTimeString(
                                Calendar.getInstance().getTime(),
                                true));
                        } else {
                            CurrTimeClock.this.cancel();
                        }
                    }
                });
            } else {
                this.cancel();
            }
        }

    }

    public void clearIdleTime() {
        _idleLabel.setText("");
    }

    public void setIdleTime(long duration) {
        _idleDuration = duration;
        if (!_mainWindow.getShell().isDisposed()) {
            Display display = _mainWindow.getShell().getDisplay();
            display.asyncExec(new Runnable() {
                public void run() {
                    SWTStatusLine.this._idleLabel.setText(
                        ResourceHelper.getString("workspace.idle")
                            + ": " + Formatter.toDurationString(_idleDuration, true));
                }
            });
        }
    }

    public void workspaceChanged(WorkspaceEvent we) {
        if (we.getId() == WorkspaceEvent.NOTES_UPDATED) {
            updateNotesIndicator(we.getSource());
        }
    }
}
