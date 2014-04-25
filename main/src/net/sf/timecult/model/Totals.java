/*
 * Copyright (c) TimeCult Project Team, 2005-2014 (dyadix@gmail.com)
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

package net.sf.timecult.model;

/**
 * @author rvishnyakov
 */
public class Totals {
    private Duration duration = new Duration(0);
    private int newItems = 0;
    private int closedItems = 0;
    private int openItems = 0;

    public Duration getDuration() {
        return duration;
    }

    public void addDuration(Duration toAdd) {
        duration.inc(toAdd.getValue());
    }

    public int getNewItems() {
        return newItems;
    }

    public int getClosedItems() {
        return closedItems;
    }

    public void incNewItems(int increment) {
        newItems += increment;
    }

    public void incClosedItems(int increment) {
        closedItems += increment;
    }

    public int getOpenItems() {
        return openItems;
    }

    public void incOpenItems(int increment) {
        openItems += increment;
    }

    public void addTotals(Totals toAdd) {
        if (toAdd == null) return;
        newItems += toAdd.getNewItems();
        closedItems += toAdd.getClosedItems();
        openItems += toAdd.getOpenItems();
        duration.inc(toAdd.getDuration().getValue());
    }
}
