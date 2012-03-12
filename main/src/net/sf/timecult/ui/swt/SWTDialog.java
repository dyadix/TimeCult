/*
 * Copyright (c) Rustam Vishnyakov, 2007-2010 (dyadix@gmail.com)
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
 * $Id: SWTDialog.java,v 1.12 2010/11/30 15:47:56 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import net.sf.timecult.PlatformUtil;
import net.sf.timecult.ResourceHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * Abstract base class for all kind of dialogs.
 */
public abstract class SWTDialog extends Dialog {

	public SWTDialog(Shell parent) {
		super(parent);
        _defaultKeyListener = new DialogKeyListener();
	}
	
	private void setup(Shell shell) {
        Image iconImage = new Image(_shell.getDisplay(), ResourceHelper
            .openStream("images/timecult_icon.png"));
        shell.setImage(iconImage);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        shell.setLayout(layout);
        
        GridData contentPanelLayout = new GridData(GridData.FILL_BOTH);        
        _contentPanel = createContentPanel(shell);
        _contentPanel.addKeyListener(_defaultKeyListener);
        _contentPanel.setLayoutData(contentPanelLayout);
        
        addButtonPanel(shell);

        shell.setText(getTitle());
        shell.pack();

        if (PlatformUtil.isWindows)
            SWTMainWindow.centerShell(shell);
        else
        // TODO: Why not doing a similar thing on Windows? Check.
            SWTMainWindow.centerShellRelatively(getParent(), shell);
    }
	
	
	protected abstract Composite createContentPanel(Shell shell);
	protected abstract boolean handleOk();
    protected abstract String getTitle();
	
	
	private void addButtonPanel(Shell shell) {        
        GridData buttonPanelData = new GridData(GridData.FILL_BOTH);
        Group buttonPanel = new Group(shell, SWT.SHADOW_ETCHED_IN);

        buttonPanel.setLayoutData(buttonPanelData);
        
        Label space = new Label(buttonPanel, SWT.NONE);
        GridData spaceLayout = new GridData(GridData.GRAB_HORIZONTAL);
        space.setLayoutData(spaceLayout);       
        
		createButtons(buttonPanel);

        GridLayout layout = new GridLayout();
		layout.numColumns = buttonPanel.getChildren().length + 1;

		buttonPanel.setLayout(layout);
	}

    
    /**
     * Adds buttons to the panel. By default creates OK/Cancel buttons.
     * @param buttonPanel The panel to add buttons to.
     */
    protected void createButtons(Composite buttonPanel) {
        GridData buttonLayout = new GridData(GridData.FILL);
        buttonLayout.widthHint = 80;
        Button okButton = new Button(buttonPanel, SWT.FLAT );
        okButton.setLayoutData(buttonLayout);
        okButton.setText(ResourceHelper.getString("button.ok"));
        okButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                if (handleOk()) {
                    _shell.setVisible(false);
                }
            }
        });
        Button cancelButton = new Button(buttonPanel, SWT.FLAT);
        cancelButton.setLayoutData(buttonLayout);
        cancelButton.setText(ResourceHelper.getString("button.cancel"));
        cancelButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent evt) {
                _shell.setVisible(false);
            }
        });
    }
	
	public void open () {
		Shell parent = getParent();
		if (_shell == null) {
            if (PlatformUtil.isWindows) {
			    _shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
            }
            else {
                _shell = new Shell(parent, SWT.DIALOG_TRIM);
            }
			_shell.setText(getText());
			setup(_shell);
			initFields();
			_shell.open();
		}
		else {
			_shell.setVisible(true);
		}
		Display display = parent.getDisplay();
		while (!_shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
	}			

    public void close() {
        if (_shell != null && !_shell.isDisposed()) {
            _shell.setVisible(false);
        }
    }
	
	protected void initFields() {
		// Do nothing
	}
    
    
    public KeyListener getDefaultKeyListener() {
        return _defaultKeyListener;
    }
    
    
    private class DialogKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            switch (evt.keyCode) {
            case SWT.CR:
                handleOk();
                _shell.setVisible(false);
                break;
            case SWT.ESC:
                _shell.setVisible(false);
                break;
            }
        }
    }
    
    /**
     * Creates a text field with a given default value and width hint. Width
     * hint is used only if a parent composite has grid lyout, otherwise it is
     * ignored.
     * 
     * @param parent
     *            A composite within which the text field is created.
     * @param defaultValue
     *            A default value.
     * @param widthHint
     *            Width hint (to be used only with grid layout) or -1.
     * @return The newly created text field.
     */
    protected Text createTextField(Composite parent, String defaultValue,
        int widthHint) {
        Text textField = new Text(parent, SWT.BORDER);
        if (defaultValue != null) {
            textField.setText(defaultValue);
        }
        Object layout = parent.getLayout();
        if (layout != null && layout instanceof GridLayout) {
            GridData gdata = new GridData();
            if (widthHint != -1) {
                gdata.widthHint = widthHint;
            }
            textField.setLayoutData(gdata);
        }
        return textField;
    }
    
    protected void errorMessage(String message) {
        MessageBox m = new MessageBox(_shell, SWT.ICON_ERROR | SWT.OK);
        m.setMessage(message);
        m.open();
    }
    
    public Shell getShell() {
        return this._shell;
    }
    
    	
	private Shell       _shell;
    private Composite   _contentPanel;
    private KeyListener _defaultKeyListener;
}
