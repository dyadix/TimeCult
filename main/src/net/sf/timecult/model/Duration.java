/*
 * Copyright (c) Rustam Vishnyakov, 2005-2008 (dyadix@gmail.com)
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
package net.sf.timecult.model;

import net.sf.timecult.util.Formatter;

public class Duration {
    
    private long duration = 0;

    public Duration(long duration) {
        this.duration = duration;
    }

    public String toString() {
        return Formatter.toDurationString(this.duration, false);
    }

    public long getValue() {
        return this.duration;
    }
    
    /**
     * Adjust the duration to a given interval in milliseconds upwards or
     * increase it by that interval if the duration is already adjusted.
     * @param ms
     */
    public void incTo(long ms) {
        long newDuration = (this.duration / ms + 1) * ms;
        this.duration = newDuration;
    }
    
    /**
     * Adjust the duration to a given interval in milliseconds downwards or
     * decrease it by that interval if the duration is already adjusted. Sets
     * the duration to 0 if it has reached the 0 limit.
     * @param ms
     */
    public void decTo(long ms) {
        long newDuration = (this.duration / ms) * ms;
        if (newDuration == this.duration) {
            newDuration -= ms;
        }
        if (newDuration < 0) {
            newDuration = 0;
        }
        this.duration = newDuration;
    }

}
