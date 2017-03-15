/*
 * Copyright (c) Rustam Vishnyakov, 2005-2017 (dyadix@gmail.com)
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
        imageMap.put("open.enabled",createIcon(display, "document-open"));
        imageMap.put("open.disabled", createIcon(display, "document-open"));
        imageMap.put("newWorkspace.enabled", createIcon(display, "document-new"));
        imageMap.put("save.enabled", createIcon(display, "document-save"));
        imageMap.put("find.enabled", createIcon(display, "edit-find"));
        imageMap.put("start.enabled", createIcon(display, "timer-start"));
        imageMap.put("tasklist.enabled", createIcon(display, "tasklist"));
        imageMap.put("quickTimesheet.enabled", createIcon(display, "quick-report"));
        imageMap.put("options.enabled", createIcon(display, "settings"));
        imageMap.put("about.enabled", createIcon(display, "about"));
        imageMap.put("homepage.enabled", createIcon(display, "home-page"));
        imageMap.put("help.enabled", createIcon(display, "wiki"));
        imageMap.put("delete.enabled", createIcon(display, "delete"));
        imageMap.put("filter.delete.enabled", createIcon(display, "delete"));
        imageMap.put("add.enabled", createIcon(display, "add"));
        imageMap.put("filter.add.enabled", createIcon(display, "add"));

        imageMap.put("addRecord.enabled", createIcon(display, "add"));
        imageMap.put("editRecord.enabled", createIcon(display, "edit"));
        imageMap.put("deleteRecord.enabled", createIcon(display, "delete"));
        imageMap.put("joinRecords.enabled", createIcon(display, "join"));
        imageMap.put("edit.enabled", createIcon(display, "properties"));

        imageMap.put("project.enabled", createImage(display, "project1.png"));
        imageMap.put("newTask.enabled", createImage(display, "star.png"));
        imageMap.put("notStarted.enabled", createImage(display, "star.png"));
        imageMap.put("flaggedList.enabled", createImage(display, "todo.gif"));
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

        imageMap.put("web.enabled", createImage(display, "web.png"));
        imageMap.put("exit.enabled", createImage(display, "exit.png"));
        imageMap.put("copy.enabled", createImage(display, "edit-copy.png"));
        imageMap.put("cut.enabled", createImage(display, "edit-cut.png"));
        imageMap.put("paste.enabled", createImage(display, "edit-paste.png"));
        imageMap.put("book-open.enabled", createImage(display, "book_open.png"));
        imageMap.put("page.enabled", createImage(display, "doc.png"));
        imageMap.put("timecult.enabled", createImage(display, "timecult.ico"));
        imageMap.put("timesheet.enabled", createImage(display, "timesheet.png"));
        imageMap.put("waiting.enabled", createImage(display, "waiting.png"));
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

        imageMap.put("tasklist.inProgress.enabled", createImage(display, "pencil.png"));
        imageMap.put("tasklist.finished.enabled", createImage(display, "check.png"));
        imageMap.put("tasklist.notStarted.enabled", createImage(display, "star.png"));
        imageMap.put("tasklist.cancelled.enabled", createImage(display, "cancelled.png"));
        imageMap.put("tasklist.flagged.enabled", createImage(display, "flag_red.png"));
        imageMap.put("tasklist.activity.enabled", createImage(display, "activity.png"));
        imageMap.put("tasklist.waiting.enabled", createImage(display, "waiting.png"));
        imageMap.put("tasklist.pdf.enabled", createImage(display, "pdf.png"));

        imageMap.put("past.due.enabled", createImage(display, "past_due.png"));
    }

    /**
     * @deprecated
     */
    private Image createImage(Display display, String imgFile) {
        InputStream inputStream = ResourceHelper.openStream("images/" + imgFile);
        return new Image(display, inputStream);
    }

    private Image createIcon(Display display, String iconFile) {
        String resource = "images/icons/" + iconFile + "-24.png";
        InputStream inputStream = ResourceHelper.openStream(resource);
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
