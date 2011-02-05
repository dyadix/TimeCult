package net.sf.timecult.ui.swt.prefs;

import net.sf.timecult.ResourceHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;

public class GeneralPrefsPanel {

    private PreferencesDialog prefsDialog;
    private Composite contentArea;
    private Button minToTrayBox;
    private Button keepTimerPosBox;
    private Button autoOpenRecentFileBox;
    private Button hideClosedBox;
    private Button autoSaveBox;
    
    public GeneralPrefsPanel(PreferencesDialog prefsDialog) {
        this.prefsDialog = prefsDialog;
        setup();
    }
    
    private void setup() {
        TabItem generalPrefsTab = new TabItem(this.prefsDialog.getTabFolder(), SWT.BORDER);
        generalPrefsTab.setText(ResourceHelper.getString("dialog.options.general"));
        this.contentArea = new Composite(this.prefsDialog.getTabFolder(), SWT.NONE);
        GridLayout areaLayout = new GridLayout();
        areaLayout.numColumns = 2;        
        this.contentArea.setLayout(areaLayout);
        generalPrefsTab.setControl(this.contentArea);
        
        addMinimizeToTray();
        addKeepTimerPos();
        addAutoOpenRecentFile();
        addHideClosed();
        addAutoSave();
    }
    
    private void addMinimizeToTray() {        
        this.minToTrayBox = new Button(this.contentArea, SWT.CHECK);
        this.minToTrayBox.setSelection(prefsDialog.getAppPreferences().isHideWhenMinimized());
        Label l = new Label(this.contentArea, SWT.None);
        l.setText(ResourceHelper.getString("dialog.options.hideWhenMinimized"));
    }
    
    private void addKeepTimerPos() {        
        this.keepTimerPosBox = new Button(this.contentArea, SWT.CHECK);
        this.keepTimerPosBox.setSelection(prefsDialog.getAppPreferences().isKeepTimerPos());
        Label l = new Label(this.contentArea, SWT.None);
        l.setText(ResourceHelper.getString("dialog.options.keepTimerPos"));
    }
    
    private void addAutoOpenRecentFile() {        
        this.autoOpenRecentFileBox = new Button(this.contentArea, SWT.CHECK);
        this.autoOpenRecentFileBox.setSelection(prefsDialog.getAppPreferences().isAutoOpenRecentFile());
        Label l = new Label(this.contentArea, SWT.None);
        l.setText(ResourceHelper.getString("dialog.options.autoOpenRecentFile"));
    }
    
    
    private void addHideClosed() {        
        this.hideClosedBox = new Button(this.contentArea, SWT.CHECK);
        this.hideClosedBox.setSelection(prefsDialog.getAppPreferences().isHideClosed());
        Label l = new Label(this.contentArea, SWT.None);
        l.setText(ResourceHelper.getString("dialog.options.hideClosedItems"));
    }
    
    
    private void addAutoSave() {        
        this.autoSaveBox = new Button(this.contentArea, SWT.CHECK);
        this.autoSaveBox.setSelection(prefsDialog.getAppPreferences().isAutoSave());
        Label l = new Label(this.contentArea, SWT.None);
        l.setText(ResourceHelper.getString("dialog.options.autosave"));
    }    
    
    public boolean apply() {
        prefsDialog.getAppPreferences().setHideWhenMinimized(this.minToTrayBox.getSelection());
        prefsDialog.getAppPreferences().setKeepTimerPos(this.keepTimerPosBox.getSelection());
        prefsDialog.getAppPreferences().setAutoOpenRecentFile(this.autoOpenRecentFileBox.getSelection());
        prefsDialog.getAppPreferences().setHideClosed(this.hideClosedBox.getSelection());
        prefsDialog.getAppPreferences().setAutoSave(this.autoSaveBox.getSelection());
        return true;
    }
        
    
}
