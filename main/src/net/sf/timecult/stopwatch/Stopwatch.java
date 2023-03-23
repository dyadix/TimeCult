/*
 * Copyright (c) TimeCult Project Team, 2005-2023 (dyadix@gmail.com)
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
 * $Id: $
 */
package net.sf.timecult.stopwatch;

import java.util.*;

/**
 * Measures time intervals.
 */
public class Stopwatch extends TimerTask {

    private       Timer                   _timer     = null;
    private       Date                    _startTime = null;
    private       long                    _duration;
    private final long                    _initDuration;
    private final long                    _delay     = 500;         // TODO: Make it changable.
    private final List<StopwatchListener> _listeners = new ArrayList<>();
    private       boolean                 _isRunning = false;
    private final boolean                 _countdown;

    public Stopwatch() {
        this(0, false);
    }

    public Stopwatch(long initDuration, boolean countdown) {
        _initDuration = initDuration;
        _countdown = countdown;
    }

    /**
     * Starts the timer and records the current time as start time.
     */
    public void start() {
        _startTime = getCurrentTime();
        _duration = _initDuration;
        if (!_isRunning) {
            if (_timer == null) {
                _timer = new Timer();
                _timer.schedule(this, 0, _delay);
            }
            _isRunning = true;
        }
        fireStateChanged(new StopwatchEvent(StopwatchEvent.Type.START, this));
    }


    /**
     * Stops the timer and records the current time as end time.
     */
    public void stop() {
        if (_isRunning) {
            _isRunning = false;
            _timer.cancel();
        }
        fireStateChanged(new StopwatchEvent(StopwatchEvent.Type.STOP, this));
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

    public long getCounted() {
        return _duration;
    }

    public long getDuration() {
        return _countdown ? _initDuration - _duration : _duration;
    }


    /**
     * @return The current time.
     */
    private Date getCurrentTime() {
        return Calendar.getInstance().getTime();
    }


    /**
     * Sends a notification to all the listeners.
     * 
     * @param evt
     *            The event to notify the listeners with.
     */
    private void fireStateChanged(StopwatchEvent evt) {
        for (StopwatchListener listener : _listeners) {
            listener.stateChanged(evt);
        }
    }


    /**
     * Produces stopwatch tick events.
     * 
     * @see java.util.TimerTask#run()
     */
    public void run() {
        if (_isRunning) {
            _duration += _countdown ? - _delay : _delay;
            if (_duration <= 0) {
                _duration = 0;
                stop();
            }
            else {
                fireStateChanged(new StopwatchEvent(StopwatchEvent.Type.TICK, this));
            }
        }
    }


    public void copyListenersTo(Stopwatch stopwatch) {
        for (StopwatchListener _listener : _listeners) {
            stopwatch.addStopwatchListener(_listener);
        }
    }

}
