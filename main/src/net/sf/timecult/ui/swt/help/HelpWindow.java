/*
 * Copyright (c) Rustam Vishnyakov, 2007 (dyadix@gmail.com)
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
 * $Id: HelpWindow.java,v 1.2 2007/06/25 17:56:27 dyadix Exp $
 */
package net.sf.timecult.ui.swt.help;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.ui.swt.IconSet;
import net.sf.timecult.ui.swt.SWTMainWindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class HelpWindow {

    private Shell         shell;
    private SWTMainWindow mainWindow;
    private SashForm      contentForm;
    private Browser       browser;


    public HelpWindow(SWTMainWindow mainWindow) {
        this.shell = new Shell(mainWindow.getShell().getDisplay());
        this.mainWindow = mainWindow;
    }


    private void setup(Shell shell) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.numColumns = 2;
        gridLayout.marginTop = 0;
        gridLayout.horizontalSpacing = 0;
        shell.setLayout(gridLayout);
        shell.setImage(mainWindow.getIconSet().getIcon("help", true));
        shell.setText(ResourceHelper.getString("dialog.help"));

        contentForm = new SashForm(shell, SWT.NONE);
        contentForm.setOrientation(SWT.HORIZONTAL);
        GridData sashData = new GridData(GridData.FILL_HORIZONTAL
            | GridData.FILL_VERTICAL);
        sashData.horizontalSpan = 3;
        contentForm.setLayoutData(sashData);

        HelpContentsView contentsView = new HelpContentsView(this);
        contentsView.addContentsListener(new ContentsListener());

        browser = new Browser(contentForm, SWT.BORDER);
        browser.setLayoutData(new GridData(GridData.FILL_BOTH));
        browser.setUrl(getPageUrl("help/about.html"));
        
    }


    public void open() {
        setup(shell);
        this.shell.open();
        Display display = this.shell.getDisplay();
        while (!this.shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }


    public String getPageUrl(String page) {
        String url = ResourceHelper.getUrl(page).getPath()
            .toString();
        if (!url.startsWith("file:")) {
            url = "file:" + url;
        }
        return url;
    }


    public SashForm getContentForm() {
        return this.contentForm;
    }


    public IconSet getIconSet() {
        return this.mainWindow.getIconSet();
    }


    private class ContentsListener implements HelpContentsListener {
        public void pageRequested(String ref) {
            browser.setUrl(getPageUrl(ref));
        }
    }

}
