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

import net.sf.timecult.ResourceHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import java.io.InputStream;
import java.util.HashMap;

public class IconSet {
    public final static int ICON_SIZE = 24;

    public IconSet(Display display) {
        loadImages(display);
    }


    private void loadImages(Display display) {
        loadIcon("open", display, "document-open");
        loadIcon("newWorkspace", display, "workspace-new");
        loadIcon("save", display, "save");
        loadIcon("find", display, "edit-find");
        loadIcon("start", display, "timer");
        loadIcon("tasklist", display, "tasklist");
        loadIcon("quickTimesheet", display, "quick-report");
        loadIcon("options", display, "settings");
        loadIcon("about", display, "about");
        loadIcon("homepage", display, "home-page");
        loadIcon("help", display, "wiki");
        loadIcon("delete", display, "delete");
        loadIcon("filter.delete", display, "delete");
        loadIcon("add", display, "add");
        loadIcon("filter.add", display, "add");

        loadIcon("addRecord", display, "add");
        loadIcon("editRecord", display, "edit");
        loadIcon("deleteRecord", display, "delete");
        loadIcon("joinRecords", display, "join");
        loadIcon("edit", display, "properties");
        loadIcon("close-project", display, "lock");
        loadIcon("cut", display, "cut");
        loadIcon("paste", display, "paste");
        loadIcon("finished", display, "done");
        loadIcon("cancel", display, "cancel");
        loadIcon("cancelled", display, "cancel");
        loadIcon("waiting", display, "wait");

        loadIcon("redFlag", display, "pin-red");
        loadIcon("greenFlag", display, "pin-green");
        loadIcon("blueFlag", display, "pin-blue");
        loadIcon("orangeFlag", display, "pin-orange");
        loadIcon("magentaFlag", display, "pin-magenta");
        loadIcon("inProgress", display, "task");

        loadIcon("project", display, "project");
        loadIcon("newTask", display, "star");
        loadIcon("notStarted", display, "star");
        loadIcon("idle", display, "idle");
        loadIcon("workspace", display, "workspace");
        loadIcon("activity", display, "activity");

        imageMap.put("timecult.enabled", createImage(display, "timecult.ico"));
        imageMap.put("timer.0.enabled", createImage(display, "timer/timer0.png"));
        imageMap.put("timer.1.enabled", createImage(display, "timer/timer1.png"));
        imageMap.put("timer.2.enabled", createImage(display, "timer/timer2.png"));
        imageMap.put("timer.3.enabled", createImage(display, "timer/timer3.png"));
        imageMap.put("calendar.enabled", createImage(display, "calendar.png"));
        imageMap.put("increase.enabled", createImage(display, "increase.png"));
        imageMap.put("decrease.enabled", createImage(display, "decrease.png"));
        imageMap.put("reopen.enabled", createImage(display, "reopen.png"));
        loadIcon("record-normal", display, "measured_time");
        loadIcon("record-partial", display, "measured_time_part");
        imageMap.put("project-closed.enabled", createImage(display, "project_closed.png"));
        imageMap.put("link.enabled", createImage(display, "link.png"));
        imageMap.put("notes.enabled", createImage(display, "notes.gif"));

        loadIcon("tasklist.inProgress", display, "task");
        loadIcon("tasklist.finished", display, "done");
        loadIcon("tasklist.notStarted", display, "star");
        loadIcon("tasklist.cancelled", display, "cancel");
        loadIcon("tasklist.flagged", display, "pin-red");
        loadIcon("tasklist.activity", display, "activity");
        loadIcon("tasklist.waiting", display, "wait");
        loadIcon("tasklist.pdf", display, "pdf");

        imageMap.put("past.due.enabled", createImage(display, "past_due.png"));

        loadIcon("timer.start", display, "timer-start");
        loadIcon("timer.pause", display, "timer-pause");
        loadIcon("timer.pauseResume", display, "timer-pause");
        loadIcon("timer.stop", display, "timer-stop");
        loadIcon("timer.minToTray", display, "tray");
    }

    /**
     * @deprecated
     */
    private Image createImage(Display display, String imgFile) {
        InputStream inputStream = ResourceHelper.openStream("images/" + imgFile);
        return new Image(display, inputStream);
    }

    private void loadIcon(String tag, Display display, String iconFile) {
        String resource = "images/icons/" + iconFile + "-" + ICON_SIZE + ".png";
        InputStream inputStream = ResourceHelper.openStream(resource);
        if (inputStream == null) {
            throw new RuntimeException("Icon " + resource + " not found");
        }
        Image enabledImage = new Image(display, inputStream);
        Image disabledImage = new Image(display, enabledImage, SWT.IMAGE_DISABLE);
        imageMap.put(tag + ".enabled", enabledImage);
        imageMap.put(tag + ".disabled", disabledImage);
    }

    public Image getIcon (String tag, boolean enabled) {
        return enabled ? imageMap.get(tag + ".enabled") : imageMap.get(tag + ".disabled");
    }


    private HashMap<String,Image> imageMap = new HashMap<String,Image>();

}
