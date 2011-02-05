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
 * $Id: NotificationWindow.java,v 1.4 2010/10/09 18:03:21 dyadix Exp $
 */
package net.sf.timecult.ui.swt.notifications;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.ui.swt.SWTMainWindow;

public class NotificationWindow {
    
    private Display display;
    private Shell shell;
    private String message;
    private static Color backgroundColor;
    private Image infoBarImage;

    public NotificationWindow (SWTMainWindow mainWindow) {
        this.display = mainWindow.getShell().getDisplay();
        backgroundColor = new Color(this.display, 255,255,200);
        infoBarImage = new Image(display,
            ResourceHelper.openStream("images/notif/notif_info.png"));
    }
    
    public void showMessage(String message) {
        this.message = message;
        this.display.asyncExec( new Runnable() {
            public void run() {
                shell = new Shell(display, SWT.ON_TOP | SWT.BORDER);
                setup(shell);
                shell.pack();
                Rectangle displayBounds = Display.getDefault().getPrimaryMonitor().getBounds();
                int xLoc = displayBounds.x + displayBounds.width - shell.getBounds().width;
                int yLoc = displayBounds.y + displayBounds.height - shell.getBounds().height;
                shell.setLocation(new Point(xLoc - 10, yLoc - 45));
                shell.setAlpha(0);
                shell.open();
                shell.addKeyListener(new KeyAdapter(){
                    @Override
                    public void keyPressed(KeyEvent e) {
                        shell.close();                        
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        //                        
                    }});
            }}
        );
    }
    
    public void setAlpha(final int alpha) {
        this.display.asyncExec( new Runnable() {
            public void run() {
                if (!shell.isDisposed() && shell.isVisible()) {
                    shell.setAlpha(alpha);
                }
            }}
        );
    }
    
    public void close() {
        this.display.asyncExec( new Runnable() {
            public void run() {
                if (!shell.isDisposed() && shell.isVisible()) {
                    shell.close();
                }                
            }} 
        );        
    }
    
    private void setup(Shell shell) {
        shell.setText("TimeCult");
        
        GridLayout gl = new GridLayout();
        gl.numColumns = 3;
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        gl.verticalSpacing = 0;
        gl.marginWidth = 0;
        gl.marginTop = 0;
        this.shell.setLayout(gl);
        this.shell.setBackground(backgroundColor);
        this.shell.setMinimumSize(200, this.shell.getMinimumSize().y);
        
        GridData titleData = getGridData();
        Label titleLabel = new Label(shell, SWT.None);        
        titleLabel.setLayoutData(titleData);
        titleLabel.setImage(this.infoBarImage);
        titleLabel.setBackground(backgroundColor);

        GridData messageData = getGridData();
        messageData.verticalIndent = 10;
        messageData.horizontalIndent = 0;
        messageData.widthHint = 200;
        Label messageText = new Label(shell, SWT.WRAP);        
        messageText.setText(message);
        messageText.setLayoutData(messageData);
        messageText.setBackground(backgroundColor);
    }
    
    private GridData getGridData() {
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.grabExcessHorizontalSpace = true;
        return gd;
    }
}
