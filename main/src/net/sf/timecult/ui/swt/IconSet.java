/*
 * Copyright (c) Rustam Vishnyakov, 2005-2007 (dyadix@gmail.com)
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
 * $Id: IconSet.java,v 1.32 2011/01/18 02:57:53 dragulceo Exp $
 */
package net.sf.timecult.ui.swt;

import java.io.InputStream;
import java.util.HashMap;

import net.sf.timecult.ResourceHelper;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class IconSet {
    
    public IconSet(Display display) {
        loadImages(display);
    }
    
    
    private void loadImages(Display display) {
        imageMap.put("project.enabled", createImage(display, "project1.png"));
        imageMap.put("newTask.enabled", createImage(display, "star.png"));
        imageMap.put("notStarted.enabled", createImage(display, "star.png"));
        imageMap.put("newWorkspace.enabled", createImage(display, "filenew.png"));
        imageMap.put("save.enabled", createImage(display, "disk.png"));
        //imageMap.put("save.disabled", createImage(display, "disk_disabled.png"));
        imageMap.put("start.enabled", createImage(display, "time_go.png"));
        imageMap.put("flaggedList.enabled", createImage(display, "todo.gif"));
        imageMap.put("tasklist.enabled", createImage(display, "todo.gif"));
        //imageMap.put("flaggedList.disabled", createImage(display, "todo.gif"));
        imageMap.put("inProgress.enabled", createImage(display, "pencil.png"));
        imageMap.put("finished.enabled", createImage(display, "check.png"));
        imageMap.put("idle.enabled", createImage(display, "bullet_blue.png"));
        imageMap.put("workspace.enabled", createImage(display, "cog.png"));
        imageMap.put("cancel.enabled", createImage(display, "cancel.png"));
        imageMap.put("cancelled.enabled", createImage(display, "cancelled.png"));

        imageMap.put("redFlag.enabled", createImage(display, "flag_red.png"));
        imageMap.put("greenFlag.enabled", createImage(display, "flag_green.png"));
        imageMap.put("blueFlag.enabled", createImage(display, "flag_blue.png"));
        imageMap.put("orangeFlag.enabled", createImage(display, "flag_orange.png"));
        imageMap.put("magentaFlag.enabled", createImage(display, "flag_magenta.png"));

        imageMap.put("open.enabled",createImage(display, "fileopen.png"));
        imageMap.put("open.disabled", createImage(display, "fileopen.png"));
        imageMap.put("delete.enabled", createImage(display, "delete.png"));
        imageMap.put("web.enabled", createImage(display, "web.png"));
        imageMap.put("exit.enabled", createImage(display, "exit.png"));
        imageMap.put("copy.enabled", createImage(display, "edit-copy.png"));
        imageMap.put("cut.enabled", createImage(display, "edit-cut.png"));
        imageMap.put("paste.enabled", createImage(display, "edit-paste.png"));
        imageMap.put("help.enabled", createImage(display, "help-browser.png"));
        imageMap.put("book-open.enabled", createImage(display, "book_open.png"));
        imageMap.put("page.enabled", createImage(display, "doc.png"));
        imageMap.put("timecult.enabled", createImage(display, "timecult.ico"));
        imageMap.put("edit.enabled", createImage(display, "edit.png"));
        imageMap.put("add.enabled", createImage(display, "add.png"));
        imageMap.put("timesheet.enabled", createImage(display, "timesheet.png"));
        imageMap.put("quickTimesheet.enabled", createImage(display, "qtimesheet.png"));
        imageMap.put("waiting.enabled", createImage(display, "waiting.png"));
        imageMap.put("options.enabled", createImage(display, "options.png"));
        imageMap.put("timer.0.enabled", createImage(display, "timer/timer0.png"));
        imageMap.put("timer.1.enabled", createImage(display, "timer/timer1.png"));
        imageMap.put("timer.2.enabled", createImage(display, "timer/timer2.png"));
        imageMap.put("timer.3.enabled", createImage(display, "timer/timer3.png"));
        imageMap.put("calendar.enabled", createImage(display, "calendar.png"));
        imageMap.put("min-to-tray.enabled", createImage(display, "min_to_tray.png"));
        imageMap.put("increase.enabled", createImage(display, "increase.png"));
        imageMap.put("decrease.enabled", createImage(display, "decrease.png"));
        imageMap.put("reopen.enabled", createImage(display, "reopen.png"));
        imageMap.put("activity.enabled", createImage(display, "activity.png"));
        imageMap.put("record-normal.enabled", createImage(display, "record-normal.png"));
        imageMap.put("record-partial.enabled", createImage(display, "record-partial.png"));
        imageMap.put("project-closed.enabled", createImage(display, "project_closed.png"));
        imageMap.put("close-project.enabled", createImage(display, "close_project.png"));
        imageMap.put("link.enabled", createImage(display, "link.png"));
        imageMap.put("notes.enabled", createImage(display, "notes.gif"));
        imageMap.put("about.enabled", createImage(display, "about.png"));
        imageMap.put("homepage.enabled", createImage(display, "home.png"));

        imageMap.put("tasklist.inProgress.enabled", createImage(display, "pencil.png"));
        imageMap.put("tasklist.finished.enabled", createImage(display, "check.png"));
        imageMap.put("tasklist.notStarted.enabled", createImage(display, "star.png"));
        imageMap.put("tasklist.cancelled.enabled", createImage(display, "cancelled.png"));
        imageMap.put("tasklist.flagged.enabled", createImage(display, "flag_red.png"));
        imageMap.put("tasklist.activity.enabled", createImage(display, "activity.png"));
        imageMap.put("tasklist.waiting.enabled", createImage(display, "waiting.png"));
        imageMap.put("tasklist.pdf.enabled", createImage(display, "pdf.png"));

        imageMap.put("filter.add.enabled", createImage(display, "add.png"));
        imageMap.put("filter.delete.enabled", createImage(display, "delete.png"));

        imageMap.put("addRecord.enabled", createImage(display, "add.png"));
        imageMap.put("editRecord.enabled", createImage(display, "edit.png"));
        imageMap.put("deleteRecord.enabled", createImage(display, "delete.png"));
        imageMap.put("joinRecords.enabled", createImage(display, "arrow_join.png"));

        imageMap.put("find.enabled", createImage(display, "find.png"));
        imageMap.put("past.due.enabled", createImage(display, "past_due.png"));
    }    
    
    private Image createImage(Display display, String imgFile) {
        InputStream inputStream = ResourceHelper.openStream("images/" + imgFile);
        return new Image(display, inputStream);
    }
        
    public Image getIcon (String tag, boolean enabled) {
        if (enabled) {
            return imageMap.get(tag + ".enabled");
        }
        else {
            return imageMap.get(tag + ".disabled");
        }
    }
    
        
    private HashMap<String,Image> imageMap = new HashMap<String,Image>();

}
