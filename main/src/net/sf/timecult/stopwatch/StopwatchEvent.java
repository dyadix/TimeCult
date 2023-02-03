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

/**
 * Stopwatch event.
 */
public class StopwatchEvent {

    public enum Type {
        START,
        STOP,
        TICK
    }

    private final Stopwatch _source;
    private final Type      _eventType;

    public StopwatchEvent(Type eventType, Stopwatch source) {
        _eventType = eventType;
        _source = source;
    }

    public Type getType() {
        return _eventType;
    }

    public Stopwatch getSource() {
        return _source;
    }
}
