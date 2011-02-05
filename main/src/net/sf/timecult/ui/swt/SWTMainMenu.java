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
 * $Id: SWTMainMenu.java,v 1.29 2011/01/21 08:00:10 dyadix Exp $
 */
package net.sf.timecult.ui.swt;

import java.io.File;

import net.sf.timecult.ui.swt.search.FindDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.*;
import net.sf.timecult.ui.swt.help.HelpProvider;
import net.sf.timecult.ui.swt.help.SWTAboutDialog;
import net.sf.timecult.ui.swt.prefs.PreferencesDialog;
import net.sf.timecult.ui.swt.tasklist.TaskListView;
import net.sf.timecult.util.LinkHelper;

/**
 * Main TimeTracker menu.
 * 
 * @author rvishnyakov
 */
public class SWTMainMenu {
    
    public final static int MAX_FILE_CHARS = 40;
    private Shell         _parent;
    private SWTMainWindow _mainWindow;
    private Menu          _mainMenu;
    private MenuItem      _saveItem;
    private MenuItem      _fileMenu;
    private MenuItem      _addMenuItem;
    private MenuItem      _deleteItem;
    private MenuItem      _startTimerItem;

    public SWTMainMenu(SWTMainWindow mainWindow) {
        _parent = mainWindow.getShell();
        _mainWindow = mainWindow;
        setup();
    }


    public void setSaveItemEnabled(boolean enabled) {
        _saveItem.setEnabled(enabled);
    }


    public void updateFileMenu() {
        _fileMenu.dispose();
        _fileMenu = createFileMenu(0);
    }


    public void updateOnSelection(Object selection) {
        _addMenuItem.setEnabled(false);
        if (selection instanceof Project) {
            _addMenuItem.setEnabled(true);
        }
        if (selection instanceof IdleTask || selection instanceof Workspace) {
            _deleteItem.setEnabled(false);
        }
        else {
            _deleteItem.setEnabled(true);
        }
        _startTimerItem.setEnabled(selection instanceof Task);
    }


    private void setup() {
        _mainMenu = new Menu(_parent, SWT.BAR);
        _fileMenu = createFileMenu(0);
        createEditMenu(1);
        createSearchMenu(2);
        createViewMenu(3);
        createToolsMenu(4);
        createHelpMenu(5);
        _parent.setMenuBar(_mainMenu);
    }


    private MenuItem createFileMenu(int order) {
        MenuItem fileItem = new MenuItem(_mainMenu, SWT.CASCADE, order);
        fileItem.setText(ResourceHelper.getString("menu.file"));
        Menu fileMenu = new Menu(_parent, SWT.DROP_DOWN);
        fileItem.setMenu(fileMenu);
        // 
        // New
        //        
        MenuItem newItem = new MenuItem(fileMenu, SWT.CASCADE);
        newItem.setText(ResourceHelper.getString("menu.new"));
        newItem
            .setImage(_mainWindow.getIconSet().getIcon("newWorkspace", true));
        newItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                TimeTracker.getInstance().resetWorkspace();
            }
        });
        //
        // Open
        //
        MenuItem openItem = new MenuItem(fileMenu, SWT.CASCADE);
        openItem.setText(ResourceHelper.getString("menu.open"));
        openItem.setImage(_mainWindow.getIconSet().getIcon("open", true));
        openItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                TimeTracker.getInstance().openWorkspace();
            }
        });
        //
        // Save
        //
        _saveItem = new MenuItem(fileMenu, SWT.CASCADE);
        _saveItem.setText(ResourceHelper.getString("menu.save") + "\tCtrl+S");
        _saveItem.setEnabled(false);
        _saveItem.setImage(_mainWindow.getIconSet().getIcon("save", true)); 
        _saveItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                TimeTracker.getInstance().saveWorkspace(true);
            }
        });
        _saveItem.setAccelerator(SWT.CTRL | 'S');
        // 
        // Save As
        //
        MenuItem saveAsItem = new MenuItem(fileMenu, SWT.CASCADE);
        saveAsItem.setText(ResourceHelper.getString("menu.saveAs"));
        saveAsItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                TimeTracker.getInstance().saveWorkspace(false);
            }
        });
        //      
        // Export
        //
        MenuItem exportItem = new MenuItem(fileMenu, SWT.CASCADE);
        exportItem.setText(ResourceHelper.getString("menu.export"));
        Menu exportMenu = new Menu(exportItem);
        exportItem.setMenu(exportMenu);
        MenuItem exportToCsvItem = new MenuItem(exportMenu, SWT.CASCADE);
        exportToCsvItem.setText(ResourceHelper.getString("menu.export.csv"));
        exportToCsvItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                TimeTracker.getInstance().exportToCsv();
            }
        });
        new MenuItem(fileMenu, SWT.SEPARATOR);

        //
        // Recently open files
        //
        createRecentlyOpenFileItems(fileMenu);
        //
        // Exit
        //
        new MenuItem(fileMenu, SWT.SEPARATOR);
        MenuItem menuFileExit = new MenuItem(fileMenu, SWT.CASCADE);
        menuFileExit.setText(ResourceHelper.getString("menu.exit"));
        menuFileExit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                TimeTracker.getInstance().exit();
            }
        });
        return fileItem;
    }


    private void createEditMenu(int order) {
        MenuItem editMenuItem = new MenuItem(_mainMenu, SWT.CASCADE, order);
        editMenuItem.setText(ResourceHelper.getString("menu.edit"));
        Menu editMenu = new Menu(_parent, SWT.DROP_DOWN);
        editMenuItem.setMenu(editMenu);
        //
        // Add menu
        //
        _addMenuItem = new MenuItem(editMenu, SWT.CASCADE);
        _addMenuItem.setText(ResourceHelper.getString("menu.add"));
        _addMenuItem.setImage(_mainWindow.getIconSet().getIcon("add", true));
        _mainWindow.getMenuFactory().createAddMenu(_addMenuItem);
        //
        // Delete
        //
        _deleteItem = new MenuItem(editMenu, SWT.CASCADE);
        _deleteItem.setText(ResourceHelper.getString("menu.delete"));
        _deleteItem.setImage(_mainWindow.getIconSet().getIcon("delete", true));
        _deleteItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                TimeTracker.getInstance().removeSelection();
            }
        });
        new MenuItem(editMenu, SWT.SEPARATOR);
        
        this._mainWindow.getMenuFactory().createPropertyItem(editMenu);
    }


    private void createRecentlyOpenFileItems(Menu menu) {
        File[] fileList = TimeTracker.getInstance().getRecentlyOpenFiles();
        for (int i = fileList.length - 1; i >= 0; i --) {
            MenuItem fileItem = new MenuItem(menu, SWT.CASCADE);
            String filePath = fileList[i].getAbsolutePath();
            if (filePath.length() > MAX_FILE_CHARS) {
                filePath = getFileRoot(fileList[i]) + File.separator
                    + fileList[i].getParentFile().getName() + File.separator
                    + fileList[i].getName();
                if (filePath.length() > MAX_FILE_CHARS) {
                    filePath = getFileRoot(fileList[i]) + File.separator
                        + fileList[i].getName();
                    if (filePath.length() > MAX_FILE_CHARS) {
                        if (MAX_FILE_CHARS > fileList[i].getName().length()) {
                            filePath = getFileRoot(fileList[i])
                                + File.separator + fileList[i].getName();
                        }
                        else {
                            filePath = getFileRoot(fileList[i])
                                + File.separator
                                + fileList[i].getName().substring(
                                    0,
                                    MAX_FILE_CHARS) + "...";
                        }
                    }
                }
            }
            fileItem.setText(filePath);
            fileItem.setData(fileList[i].getAbsolutePath());
            fileItem.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent e) {
                    MenuItem item = (MenuItem) e.getSource();
                    File file = new File((String) item.getData());
                    TimeTracker.getInstance().loadWorkspace(file);
                }


                public void widgetDefaultSelected(SelectionEvent e) {
                    // Do nothing
                }
            });
        }
    }
    
    /*
     * Returns a file root as /bin/... or C:/... 
     */
    private String getFileRoot(File file) {
        String path = file.getParentFile().getAbsolutePath();
        int pos = path.indexOf(File.separatorChar, 1);
        if (pos > 0) {
            if (pos < file.length() - 1) {
                path = path.substring(0, pos) + File.separator + "...";
            }
        }
        else {
            path = "...";
        }
        return path;
    }


    private void createHelpMenu(int order) {
        MenuItem helpMenuItem = new MenuItem(_mainMenu, SWT.CASCADE, order);
        helpMenuItem.setText(ResourceHelper.getString("menu.help"));
        Menu helpMenu = new Menu(helpMenuItem);
        helpMenuItem.setMenu(helpMenu);
        
        MenuItem helpItem = new MenuItem(helpMenu, SWT.CASCADE);
        helpItem.setText(ResourceHelper.getString("menu.helpContent"));
        helpItem.setImage(_mainWindow.getIconSet().getIcon("help", true));
        helpItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                //HelpWindow helpWindow = new HelpWindow(_mainWindow);
                //helpWindow.open();
                HelpProvider.openHelp();
            }
        });                
        
        //new MenuItem(helpMenu, SWT.SEPARATOR);
        
        MenuItem webPageItem = new MenuItem(helpMenu, SWT.CASCADE);
        webPageItem.setText(ResourceHelper.getString("menu.homepage"));
        webPageItem.setImage(_mainWindow.getIconSet().getIcon("homepage", true));
        webPageItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                Program.launch("http://timecult.sf.net");
            }
        });                
        
        new MenuItem(helpMenu, SWT.SEPARATOR); 
        
        MenuItem aboutItem = new MenuItem(helpMenu, SWT.CASCADE);
        aboutItem.setText(ResourceHelper.getString("menu.about"));
        aboutItem.setImage(_mainWindow.getIconSet().getIcon("about", true));
        aboutItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                SWTAboutDialog aboutDialog = new SWTAboutDialog(_parent);
                aboutDialog.open();
            }
        });

    }
    
    private MenuItem createViewMenu(int order) {
        MenuItem viewItem = new MenuItem(_mainMenu, SWT.CASCADE, order);
        viewItem.setText(ResourceHelper.getString("menu.view"));
        Menu viewMenu = new Menu(_parent, SWT.DROP_DOWN);
        viewItem.setMenu(viewMenu);
        
        MenuItem flaggedItem = new MenuItem(viewMenu, SWT.CASCADE);
        flaggedItem.setText(ResourceHelper.getString("tasklist.title") + "\tCtrl+T");
        flaggedItem.setImage(_mainWindow.getIconSet().getIcon("tasklist", true));
        flaggedItem.setAccelerator(SWT.CTRL | 'T');
        flaggedItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                TaskListView taskListView = new TaskListView(_mainWindow);
                taskListView.open();
            }
        });
        
        return viewItem;
    }
    
    private MenuItem createToolsMenu(int order) {
        MenuItem toolsItem = new MenuItem(_mainMenu, SWT.CASCADE, order);
        toolsItem.setText(ResourceHelper.getString("menu.tools"));
        Menu toolsMenu = new Menu(_parent, SWT.DROP_DOWN);
        toolsItem.setMenu(toolsMenu);

        _startTimerItem = new MenuItem(toolsMenu, SWT.CASCADE);
        _startTimerItem.setText(ResourceHelper.getString("button.start.tooltip") + "\tCtrl+S");
        _startTimerItem.setImage(_mainWindow.getIconSet().getIcon("start", true));
        _startTimerItem.setAccelerator(SWT.CTRL | 'S');
        _startTimerItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                TimeTracker.getInstance().startTimer();
            }
        });
        
        MenuItem optionsItem = new MenuItem(toolsMenu, SWT.CASCADE);
        optionsItem.setText(ResourceHelper.getString("menu.options"));
        optionsItem.setImage(_mainWindow.getIconSet().getIcon("options", true));
        optionsItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                PreferencesDialog prefsDialog =  new PreferencesDialog(_parent);
                prefsDialog.open();
            }
        });
        
        return toolsItem;
    }

    private MenuItem createSearchMenu(int order) {
        MenuItem searchItem = new MenuItem(_mainMenu, SWT.CASCADE, order);
        searchItem.setText(ResourceHelper.getString("menu.search"));
        Menu searchMenu = new Menu(_parent, SWT.DROP_DOWN);
        searchItem.setMenu(searchMenu);

        MenuItem findItem = new MenuItem(searchMenu, SWT.CASCADE);
        findItem.setText(ResourceHelper.getString("menu.search.find") + "\tCtrl+F");
        findItem.setImage(_mainWindow.getIconSet().getIcon("find", true));
        findItem.setAccelerator(SWT.CTRL | 'F');
        findItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                FindDialog findDialog = new FindDialog(_mainWindow, TimeTracker.getInstance().getWorkspace());
                findDialog.open();
            }
        });

        return searchItem;
    }

}
