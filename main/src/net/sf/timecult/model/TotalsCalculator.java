/*
 * File: Totals.java
 * Created: 10.06.2005
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
package net.sf.timecult.model;

/**
 * Base interface for classes which may calculate totals.
 * @author rvishnyakov (rvishnyakov@yahoo.com)
 */
public interface TotalsCalculator {

    /**
     * @return Total duration in milliseconds.
     * @param timeLog The time log to use.
     */
    Totals getTotals(TimeLog timeLog, TimeRecordFilter filter);
}
