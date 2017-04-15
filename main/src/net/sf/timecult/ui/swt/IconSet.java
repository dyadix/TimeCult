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
        imageMap.put("open.enabled",createIcon(display, "document-open"));
        imageMap.put("open.disabled", createIcon(display, "document-open"));
        imageMap.put("newWorkspace.enabled", createIcon(display, "workspace-new"));
        imageMap.put("save.enabled", createIcon(display, "save"));
        imageMap.put("find.enabled", createIcon(display, "edit-find"));
        imageMap.put("start.enabled", createIcon(display, "timer"));
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
        imageMap.put("close-project.enabled", createIcon(display, "lock"));
        imageMap.put("cut.enabled", createIcon(display, "cut"));
        imageMap.put("paste.enabled", createIcon(display, "paste"));
        imageMap.put("finished.enabled", createIcon(display, "done"));
        imageMap.put("cancel.enabled", createIcon(display, "cancel"));
        imageMap.put("cancelled.enabled", createIcon(display, "cancel"));
        imageMap.put("waiting.enabled", createIcon(display, "wait"));

        imageMap.put("redFlag.enabled", createIcon(display, "pin-red"));
        imageMap.put("greenFlag.enabled", createIcon(display, "pin-green"));
        imageMap.put("blueFlag.enabled", createIcon(display, "pin-blue"));
        imageMap.put("orangeFlag.enabled", createIcon(display, "pin-orange"));
        imageMap.put("magentaFlag.enabled", createIcon(display, "pin-magenta"));
        imageMap.put("inProgress.enabled", createIcon(display, "task"));

        imageMap.put("project.enabled", createIcon(display, "project"));
        imageMap.put("newTask.enabled", createIcon(display, "star"));
        imageMap.put("notStarted.enabled", createIcon(display, "star"));
        imageMap.put("idle.enabled", createIcon(display, "idle"));
        imageMap.put("workspace.enabled", createIcon(display, "workspace"));
        imageMap.put("activity.enabled", createIcon(display, "activity"));

        imageMap.put("timecult.enabled", createImage(display, "timecult.ico"));
        imageMap.put("timesheet.enabled", createImage(display, "timesheet.png"));
        imageMap.put("timer.0.enabled", createImage(display, "timer/timer0.png"));
        imageMap.put("timer.1.enabled", createImage(display, "timer/timer1.png"));
        imageMap.put("timer.2.enabled", createImage(display, "timer/timer2.png"));
        imageMap.put("timer.3.enabled", createImage(display, "timer/timer3.png"));
        imageMap.put("calendar.enabled", createImage(display, "calendar.png"));
        imageMap.put("min-to-tray.enabled", createImage(display, "min_to_tray.png"));
        imageMap.put("increase.enabled", createImage(display, "increase.png"));
        imageMap.put("decrease.enabled", createImage(display, "decrease.png"));
        imageMap.put("reopen.enabled", createImage(display, "reopen.png"));
        imageMap.put("record-normal.enabled", createIcon(display, "measured_time"));
        imageMap.put("record-partial.enabled", createIcon(display, "measured_time_part"));
        imageMap.put("project-closed.enabled", createImage(display, "project_closed.png"));
        imageMap.put("link.enabled", createImage(display, "link.png"));
        imageMap.put("notes.enabled", createImage(display, "notes.gif"));

        imageMap.put("tasklist.inProgress.enabled", createIcon(display, "task"));
        imageMap.put("tasklist.finished.enabled", createIcon(display, "done"));
        imageMap.put("tasklist.notStarted.enabled", createIcon(display, "star"));
        imageMap.put("tasklist.cancelled.enabled", createIcon(display, "cancel"));
        imageMap.put("tasklist.flagged.enabled", createIcon(display, "pin-red"));
        imageMap.put("tasklist.activity.enabled", createIcon(display, "activity"));
        imageMap.put("tasklist.waiting.enabled", createIcon(display, "wait"));
        imageMap.put("tasklist.pdf.enabled", createIcon(display, "pdf"));

        imageMap.put("past.due.enabled", createImage(display, "past_due.png"));

        imageMap.put("timer.start.enabled", createIcon(display, "timer-start"));
        imageMap.put("timer.pause.enabled", createIcon(display, "timer-pause"));
        imageMap.put("timer.pauseResume.enabled", createIcon(display, "timer-pause"));
        imageMap.put("timer.stop.enabled", createIcon(display, "timer-stop"));
        imageMap.put("timer.minToTray.enabled", createIcon(display, "tray"));
    }

    /**
     * @deprecated
     */
    private Image createImage(Display display, String imgFile) {
        InputStream inputStream = ResourceHelper.openStream("images/" + imgFile);
        return new Image(display, inputStream);
    }

    private Image createIcon(Display display, String iconFile) {
        String resource = "images/icons/" + iconFile + "-" + ICON_SIZE + ".png";
        InputStream inputStream = ResourceHelper.openStream(resource);
        if (inputStream == null) {
            throw new RuntimeException("Icon " + resource + " not found");
        }
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
