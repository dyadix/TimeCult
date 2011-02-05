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
 * 
 * $Id: LocalIdGenerator.java,v 1.2 2008/07/12 06:49:12 dyadix Exp $
 */
package net.sf.timecult.model.mem;
import net.sf.timecult.model.*;

/**
 * Generates local IDs.
 * @author rvishnyakov
 */
public class LocalIdGenerator implements IdGenerator
{

    /* (non-Javadoc)
     * @see dyadix.timetracker.model.IdGenerator#getNewId()
     */
    public String getNewId()
    {
        _currId ++;
        return Integer.toString(_currId);
    }
    
    public String getLastId()
    {
        return Integer.toString(_currId);
    }
    
    public boolean setCurrentId(String id)
    {
        setCurrentId(Integer.parseInt(id));
        return true;
    }
    
    public void setCurrentId(int id)
    {
        _currId = id;
    }
       
    private int _currId = 0;

}
