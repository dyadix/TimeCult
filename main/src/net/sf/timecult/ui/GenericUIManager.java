package net.sf.timecult.ui;

import java.awt.Rectangle;
import java.io.File;

import net.sf.timecult.model.Project;
import net.sf.timecult.model.Task;
import net.sf.timecult.model.Workspace;

/**
 * Defines basic operations for any UI type regardless of its
 * implementation platform.
 *
 * @author rustam
 */
public interface GenericUIManager {

    void initUI();

    void startUI();

    void setLookAndFeel(String name);

    String getLookAndFeel();

    void setBounds(int left, int top, int width, int height);

    Rectangle getBounds();

    /**
     * Rebind all the UI elements interested in receiving workspace
     * event notifications.
     *
     * @param workspace
     */
    void rebindWorkspaceListeners(Workspace workspace);

    void showError(String message);

    void setCurrentSelection(Object object);

    void displaySplashScreen();

    void startTimer(Workspace workspace, Task task, long initTime);

    void updateProjectTree();

    void updateOnSelection(Object object);

    void updateOnRemove(Object object);

    void updateAll();

    File chooseFile(boolean forOpen);

    File chooseTargetCsvFile();

    void setSaveEnabled(boolean enabled);

    void updateFileMenu();

    void updateTimeLog(Object source);

    void updateTotals();

    boolean confirmTaskDeletion(Task task);

    boolean confirmProjectDeletion(Project project);

    boolean confirmExit(String message);

    boolean confirmSave();

    void cancelExit();

    boolean activeTimersExist();

    void setIdleTime(long duration);

    void clearIdleTime();

    void displayWarning(String message);

    void showNotification(String message);
}
