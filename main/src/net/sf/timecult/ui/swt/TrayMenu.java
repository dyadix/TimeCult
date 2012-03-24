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
 * $Id: TrayMenu.java,v 1.3 2007/12/21 20:02:43 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.Task;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class TrayMenu {

    private SWTMainWindow   mainWindow;
    private Shell           trayShell;
    private static TrayMenu instance;
    private Menu            popup;


    private TrayMenu(SWTMainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }


    public static TrayMenu getInstance(SWTMainWindow mainWindow) {
        if (instance == null) {
            instance = new TrayMenu(mainWindow);
        }
        return instance;
    }
    
    
    public static void dispose() {
        if (instance != null) {
            instance.disposeShell();
            instance = null;
        }
    }
    
    private void disposeShell() {
        this.trayShell.dispose();
    }
    
    public void open() {
        Display d = mainWindow.getShell().getDisplay();
        if (trayShell == null || trayShell.isDisposed()) {
            trayShell = new Shell(d, SWT.SINGLE);
            setup(trayShell);
        }
        this.popup.setVisible(true);
        while (!trayShell.isDisposed()) {
            if (!d.readAndDispatch())
                d.sleep();
        }
    }
    
    private void setup(Shell shell) {
        popup = new Menu(shell);
        MenuItem openItem = new MenuItem(popup, SWT.CASCADE);
        openItem.setText(ResourceHelper.getString("traymenu.open"));
        openItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                TrayMenu.this.mainWindow.restoreWindow();               
            }
        });
        MenuItem startItem = new MenuItem(popup, SWT.CASCADE);
        startItem.setText(ResourceHelper.getString("traymenu.start"));
        startItem.setMenu(this.mainWindow.createInProgressStartMenu(
            startItem,
            new SelectionAdapter() {
                public void widgetSelected(SelectionEvent evt) {                    
                    if (evt.getSource() instanceof MenuItem) {                        
                        MenuItem item = (MenuItem) evt.getSource();
                        Object data = item.getData();
                        if (data instanceof Task) {
                            Task task = (Task) data;
                            TimeTracker.getInstance().getUIManager()
                                .startTimer(
                                    TimeTracker.getInstance().getWorkspace(),
                                    task);
                        }
                    }
                }
            }));
        new MenuItem(popup, SWT.SEPARATOR);
        MenuItem exitItem = new MenuItem(popup, SWT.CASCADE);
        exitItem.setText(ResourceHelper.getString("menu.exit"));
        exitItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                TimeTracker.getInstance().exit();         
            }
        });
        shell.setMenu(popup);
        shell.pack();
        popup.addMenuListener(new MenuAdapter() {
            @Override
            public void menuHidden(MenuEvent e) {
                dispose();
            }
        });
    }
           

}
