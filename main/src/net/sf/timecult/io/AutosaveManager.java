/*
 * Copyright (c) Rustam Vishnyakov, 2009 (dyadix@gmail.com)
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
 * $Id: AutosaveManager.java,v 1.1 2009/01/31 16:11:31 dyadix Exp $
 */
package net.sf.timecult.io;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import net.sf.timecult.conf.AppPreferences;
import net.sf.timecult.conf.AppPreferencesListener;

/**
 * If enabled, creates a timer task which periodically notifies its listeners by calling
 * doSave() method.
 */
public class AutosaveManager implements AppPreferencesListener {
    
    public int DEFAULT_INTERVAL = 30000; // 30 sec

    private Timer timer;
    private int interval;
    private Vector<AutosaveManagerListener> listeners;
    
    
    /**
     * Default constructor.
     */
    public AutosaveManager() {
        this.timer = null;
        this.interval = DEFAULT_INTERVAL;
        this.listeners = new Vector<AutosaveManagerListener>();
        AppPreferences.getInstance().addListener(this);
    }
    
    
    /**
     * Creates a new timer task and starts sending autosave events.
     */
    public void enable() {
        synchronized(this) {
            if (this.timer == null) {
                this.timer = new Timer();
                this.timer.schedule(new AutosaveTask(), 0, this.interval);
            }
        }
    }
    
    
    /**
     * Destroys the running timer task and stops sending autosave enents.
     */
    public void disable() {
        synchronized(this) {
            if (this.timer != null) {
                this.timer.cancel();
                this.timer = null;
            }
        }
    }
    
    
    /**
     * @return True if Autosave is enabled.
     */
    public boolean isEnabled() {
        return (this.timer != null);
    }
    
    
    public void addListener(AutosaveManagerListener l) {
        this.listeners.add(l);
    }
    
    
    private void notifyListeners() {
        for (AutosaveManagerListener l: listeners) {
            l.doSave();
        }
    }


    public void preferenceChanged(String prefName) {
        if (AppPreferences.AUTOSAVE.equals(prefName)) {
            if (AppPreferences.getInstance().isAutoSave()) {
                enable();
            }
            else {
                disable();
            }
        }        
    }
    
    private class AutosaveTask extends TimerTask {
        
        @Override
        public void run() {
            notifyListeners();
        }
    }
}
