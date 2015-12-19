/*
 * File: Timer.java
 * Created: 30.05.2005
 *
 * Copyright (c) Rustam Vishnyakov, 2005-2006 (rvishnyakov@yahoo.com)
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
 */
package net.sf.timecult.stopwatch;

import java.util.*;

/**
 * Measures time intervals.
 * 
 * @author rvishnyakov (rvishnyakov@yahoo.com)
 */
public class Stopwatch extends TimerTask {

    /**
     * Starts the timer and records the current time as start time.
     */
    public void start() {
        _startTime = getCurrentTime();
        _duration = 0;
        if (!_isRunning) {
            if (_timer == null) {
                _timer = new Timer();
                _timer.schedule(this, 0, _delay);
            }
            _isRunning = true;
        }
        fireStateChanged(new StopwatchEvent(StopwatchEvent.START, this));
    }


    /**
     * Stops the timer and records the current time as end time.
     */
    public void stop() {
        if (_isRunning) {
            _isRunning = false;
            _timer.cancel();
        }
        fireStateChanged(new StopwatchEvent(StopwatchEvent.STOP, this));
    }


    /**
     * Pause the stopwatch.
     * 
     */
    public void pause() {
        _isRunning = false;
    }


    /**
     * Resume the previously paused stopwatch.
     */
    public void resume() {
        _isRunning = true;
    }


    public boolean isRunning() {
        return _isRunning;
    }


    /**
     * Adds new listener to the stopwatch.
     * 
     * @param listener
     *            The listener to add.
     */
    public void addStopwatchListener(StopwatchListener listener) {
        _listeners.add(listener);
    }


    public Date getStartTime() {
        return _startTime;
    }


    public long getDuration() {
        return _duration;
    }


    /**
     * @return The current time.
     */
    private Date getCurrentTime() {
        return Calendar.getInstance().getTime();
    }


    /**
     * Sends a notification to all the listners.
     * 
     * @param evt
     *            The event to notify the listeners with.
     */
    private void fireStateChanged(StopwatchEvent evt) {
        for (Iterator iter = _listeners.iterator(); iter.hasNext();) {
            StopwatchListener l = (StopwatchListener) iter.next();
            l.stateChanged(evt);
        }
    }


    /**
     * Produces stopwatch tick events.
     * 
     * @see java.util.TimerTask#run()
     */
    public void run() {
        if (_isRunning) {
            _duration += _delay;
            fireStateChanged(new StopwatchEvent(StopwatchEvent.TICK, this));
        }
    }


    public void copyListenersTo(Stopwatch stopwatch) {
        for (Object _listener : _listeners) {
            stopwatch.addStopwatchListener((StopwatchListener) _listener);
        }
    }


    /**
     * @return the _interval
     */
    public long getInterval() {
        return _interval;
    }


    /**
     * @param interval
     *            the _interval to set
     */
    public void setInterval(long ticks) {
        this._interval = ticks;
    }

    private Timer   _timer     = null;
    private Date    _startTime = null;
    private long    _duration  = 0;
    private long    _delay     = 500;         // TODO: Make it changable.
    private long    _interval  = 10;          // 600 - 5 minutes
    private long    _crtTicks  = 10;          // 600 - 5 minutes
    private Vector  _listeners = new Vector();
    private boolean _isRunning = false;
}
