package net.sf.timecult.ui;

import java.awt.Rectangle;
import java.io.File;

import net.sf.timecult.model.Project;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.Workspace;

/**
 * Defines basic operations for any UI type regardless of its
 * implementation platform.
 * @author rustam
 *
 */
public interface GenericUIManager {
	
	public void initUI();
	public void startUI();
	
	public void setLookAndFeel(String name);
	public String getLookAndFeel();
	public void setBounds(int left, int top, int width, int height);
	public Rectangle getBounds();
	
    /**
     * Rebind all the UI elements interested in receiving workspace
     * event notifications.
     * @param workspace
     */
    public void rebindWorkspaceListeners(Workspace workspace);
	
	public void showError(String message);
	public void setCurrentSelection(Object object);	
	public void displaySplashScreen();
	public void startTimer(Workspace workspace, Task task);
	public void updateProjectTree();
	public void updateOnSelection(Object object);
	public void updateOnRemove(Object object);
	public void updateAll();
	public File chooseFile(boolean forOpen);
	public File chooseTargetCsvFile();
	public void setSaveEnabled(boolean enabled);
	
	public void updateFileMenu();
	public void updateTimeLog(Object source);
	public void updateTotals();
	
	public boolean confirmTaskDeletion(Task task);
	public boolean confirmProjectDeletion(Project project);
	public boolean confirmExit(String message);	
	public boolean confirmSave();
	
	public void cancelExit();
    
    public boolean activeTimersExist();
    public void setIdleTime(long duration);
    public void clearIdleTime();
    
    public void displayWarning(String message);
    public void showNotification(String message);
}
