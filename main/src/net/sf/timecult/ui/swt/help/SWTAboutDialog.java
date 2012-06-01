/*
 * Copyright (c) Rustam Vishnyakov, 2007-2008 (dyadix@gmail.com)
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
 * $Id: SWTAboutDialog.java,v 1.12 2010/04/22 18:01:08 dyadix Exp $
 */
package net.sf.timecult.ui.swt.help;

import net.sf.timecult.AppInfo;
import net.sf.timecult.ResourceHelper;
import net.sf.timecult.ui.swt.SWTDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SWTAboutDialog extends SWTDialog{
    
    private Composite aboutPanel;
    private Button creditsButton;
    private Composite creditsPanel;
    private Composite infoPanel;
    private StackLayout infoPanelLayout;
    private static SWTAboutDialog instance;
    
	private SWTAboutDialog(Shell shell) {
		super(shell, false);
	}

    public static SWTAboutDialog getInstance(Shell parent) {
        if (instance == null) {
            instance = new SWTAboutDialog(parent);
        }
        return instance;
    }
		

    @Override
    protected Composite createContentPanel(Shell shell) {
        Composite contentPanel = new Composite(shell, SWT.NULL);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.numColumns = 2;
        gridLayout.marginTop = 0;
        gridLayout.horizontalSpacing = 0;
        contentPanel.setLayout(gridLayout);

        Image logo = new Image(shell.getDisplay(), ResourceHelper
                .openStream("images/timecult_logo.png"));
        Label logoLabel = new Label(contentPanel, SWT.NULL);
        logoLabel.setImage(logo);
        
        this.infoPanel = new Composite(contentPanel, SWT.NONE);
        this.infoPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.infoPanelLayout = new StackLayout(); 
        this.infoPanel.setLayout(this.infoPanelLayout);
        this.aboutPanel = createAboutPanel(this.infoPanel, shell);
        this.creditsPanel = createCreditsPanel(this.infoPanel, shell);
        this.infoPanelLayout.topControl = this.aboutPanel;
        return contentPanel;
    }
    
    private Composite createAboutPanel(Composite parent, Shell shell) {

        Composite aboutPanel = new Composite(parent, SWT.NULL);
        GridLayout aboutGL = new GridLayout();
        aboutGL.marginWidth = 0;
        aboutGL.marginHeight = 0;
        aboutGL.numColumns = 1;
        aboutGL.marginTop = 0;
        aboutGL.marginLeft = 10;
        aboutGL.marginRight = 10;
        aboutGL.horizontalSpacing = 0;
        aboutPanel.setLayout(aboutGL);

        Color titleColor = new Color(shell.getDisplay(), 142, 36, 0);
        Label titleLabel = new Label(aboutPanel, SWT.NULL);
        FontData titleFdata[] = titleLabel.getFont().getFontData();
        titleFdata[0].height = 18;
        titleFdata[0].setStyle(SWT.BOLD);
        titleFdata[0].setName("Arial");
        titleLabel.setFont(new Font(shell.getDisplay(), titleFdata));
        titleLabel.setText("TimeCult");
        titleLabel.setForeground(titleColor);
        
        Label versionLabel = new Label(aboutPanel, SWT.NULL);
        versionLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
        versionLabel.setText("  "
            + ResourceHelper.getString("message.about.version") + " "
            + AppInfo.getMajorVersion() + "." + AppInfo.getMinorVersion());

        FontData fdata[] = versionLabel.getFont().getFontData();
        fdata[0].height = 10;
        fdata[0].setStyle(SWT.BOLD);
        fdata[0].setName("Arial");
        versionLabel.setFont(new Font(shell.getDisplay(), fdata));
        
        Label infoLabel = new Label(aboutPanel, SWT.NULL);
        infoLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
        infoLabel.setText("\n" + ResourceHelper.getString("message.about.build")
            + " " + AppInfo.getBuild() + ", "
            + AppInfo.getBuildDate()
            + "\nCopyright \u00a9 TimeCult Project Team 2005-2012\n\n\n");
        
        
        return aboutPanel;        
    }
    
    
    private Composite createCreditsPanel(Composite parent, Shell shell) {
        Composite creditsPanel = new Composite(parent, SWT.NULL);
        GridLayout creditsGL = new GridLayout();
        creditsGL.marginWidth = 0;
        creditsGL.marginHeight = 0;
        creditsGL.numColumns = 1;
        creditsGL.marginTop = 0;
        creditsGL.marginLeft = 10;
        creditsGL.marginRight = 10;
        creditsGL.horizontalSpacing = 0;
        creditsPanel.setLayout(creditsGL);
        Label creditsLabel = new Label(creditsPanel, SWT.NONE);
        creditsLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
        creditsLabel.setText(getCreditsText());
        return creditsPanel;
    }
    
    private String getCreditsText() {
        StringBuffer buf = new StringBuffer();
        buf.append("Author, Project Admin:\n");
        buf.append("\tRustam Vishnyakov\n");
        buf.append("Translators:\n");
        buf.append("\tKristian Struck\n");
        buf.append("\tPhilippe Charles\n");
        return buf.toString();
    }
    
    @Override
    protected String getTitle() {
        return ResourceHelper.getString("dialog.about");
    }

    @Override
    protected boolean handleOk() {
        return true;
    }
    
    @Override
    protected void createButtons(Composite buttonPanel) {
        GridData buttonLayout = new GridData(GridData.FILL);
        buttonLayout.widthHint = 80;
        
        creditsButton = new Button(buttonPanel, SWT.FLAT );
        creditsButton.setLayoutData(buttonLayout);
        creditsButton.setText("Credits"); //TODO: Localize
        creditsButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                if (infoPanelLayout.topControl == aboutPanel) {
                    infoPanelLayout.topControl = creditsPanel;
                    infoPanel.layout();
                    creditsButton.setText("About"); // TODO: Localize
                }
                else {
                    infoPanelLayout.topControl = aboutPanel;
                    infoPanel.layout();
                    SWTAboutDialog.this.creditsButton.setText("Credits"); // TODO: Localize
                }
            }
        });
        
        Button okButton = new Button(buttonPanel, SWT.FLAT );
        okButton.setLayoutData(buttonLayout);
        okButton.setText(ResourceHelper.getString("button.ok"));
        okButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                if (handleOk()) {
                    getShell().setVisible(false);
                }
            }
        });
        
    }
	
}
