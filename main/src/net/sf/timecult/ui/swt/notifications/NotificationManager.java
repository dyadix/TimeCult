package net.sf.timecult.ui.swt.notifications;

import net.sf.timecult.ui.swt.SWTMainWindow;

public class NotificationManager {
    
    private NotificationWindow notificationWindow;
    private Thread notificationThread;
    private String message;
    
    public NotificationManager(SWTMainWindow mainWindow) {
        this.notificationWindow = new NotificationWindow(mainWindow);
        this.notificationThread = new Thread(new Notifier());
    }
    

    public void sendMessage(String message) {
        synchronized (message) {
            if (!this.notificationThread.isAlive()) {
                this.notificationThread.start();
            }
            this.message = message;
        }
    }
    
    private class Notifier implements Runnable {

        public void run() {
            while (true) {
                if (message != null) {
                    synchronized (message) {
                        notificationWindow.showMessage(message);
                        for (int i = 0; i <= 255; i += 10) {
                            notificationWindow.setAlpha(i);
                            doWait(100);
                        }
                        doWait(1000);
                        for (int i = 255; i >= 0; i -= 10) {
                            notificationWindow.setAlpha(i);
                            doWait(100);
                        }
                        notificationWindow.close();
                        message = null;
                    }
                }
                doWait(1000);
            }
        }
        
    }
    
    private void doWait(long ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            // ignore
        }
    }

}
