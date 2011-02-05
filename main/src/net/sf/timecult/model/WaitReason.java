/*
 * Copyright (c) Rustam Vishnyakov, 2007 (dyadix@gmail.com)
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
 * $Id: WaitReason.java,v 1.1 2007/12/07 21:30:33 dyadix Exp $
 */
package net.sf.timecult.model;

import java.util.TreeMap;

/**
 * Contains a detailed description of the task's wait reason. In the future it may have
 * more properties to describe task dependencies. The class also keeps track on all the previously
 * defined wait reasons so that a user can reuse them for several tasks at a time.
 * @author Rustam Vishnyakov
 */
public class WaitReason {

    private String reasonText;
    private static TreeMap<String,WaitReason> allReasons = new TreeMap<String,WaitReason>();
    
    private WaitReason(String reasonText) {
        this.reasonText = reasonText;
    }
    
    public static WaitReason newInstance(String reasonText) {
        WaitReason instance = allReasons.get(reasonText);
        if (instance == null) {
            instance = new WaitReason(reasonText);
            allReasons.put(reasonText, instance);
        }        
        return instance;
    }
    
    public String getText() {
        return this.reasonText;
    }
    
    public static WaitReason[] getAllReasons() {
        return allReasons.values().toArray(new WaitReason[0]);
    }
    
    
}
