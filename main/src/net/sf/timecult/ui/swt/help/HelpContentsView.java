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
 * $Id: HelpContentsView.java,v 1.2 2007/07/07 10:04:01 dyadix Exp $
 */
package net.sf.timecult.ui.swt.help;

import java.util.Vector;

import net.sf.timecult.ResourceHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Represents a view inside Help Window with help topics. 
 * @author dyadix
 */
public class HelpContentsView {

    private HelpWindow                   helpWindow;
    private Tree                         contentTree;
    private Vector<HelpContentsListener> contentsListeners;


    public HelpContentsView(HelpWindow helpWindow) {
        this.contentsListeners = new Vector<HelpContentsListener>();
        this.helpWindow = helpWindow;
        setup();
    }


    private void setup() {
        contentTree = new Tree(this.helpWindow.getContentForm(), SWT.NONE);
        contentTree.addSelectionListener(new ContentTreeSelectionListener());
        TreeItem intro = createItem(null, "help.introduction");
        TreeItem gettingStarted = createItem(null, "help.gettingStarted");
        TreeItem concepts = createItem(intro, "help.concepts");   
    }


    private TreeItem createItem(TreeItem parent, String itemId) {
        TreeItem item = null;
        if (parent == null) {
            item = new TreeItem(this.contentTree, SWT.NONE);
        }
        else {
            item = new TreeItem(parent, SWT.NONE);
            parent.setImage(this.helpWindow.getIconSet().getIcon("book", true));
        }
        item.setText(ResourceHelper.getString(itemId));
        item.setData(ResourceHelper.getString(itemId + ".ref"));
        item.setImage(this.helpWindow.getIconSet().getIcon("page", true));
        return item;
    }
    
    public void addContentsListener(HelpContentsListener l) {
        this.contentsListeners.add(l);
    }
    
    private void notifyListeners(String ref) {
        for(HelpContentsListener l : this.contentsListeners) {
            l.pageRequested(ref);
        }
    }
    
    private class ContentTreeSelectionListener extends SelectionAdapter {
        public void widgetSelected(SelectionEvent evt) {
            TreeItem[] selItem = contentTree.getSelection();
            Object ref = selItem[0].getData();
            if (ref != null) {
                notifyListeners((String) ref);
            }
        }       
    }    

}
