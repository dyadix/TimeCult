/*
 * Copyright (c) TimeCult Project Team, 2005-2019 (dev@codeflections.com)
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

package net.sf.timecult.ui.swt;

import net.sf.timecult.model.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.TreeItem;

import java.util.HashMap;

public class ItemStyleFactory {
    private final SWTMainWindow          _mainWindow;
    private final HashMap<Integer, Font> _fontMap = new HashMap<Integer, Font>();

    private       Color normalTextColor;
    private       Color disabledTextColor;
    private final Color refFlagTextColor;
    private final Color greenFlagTextColor;
    private final Color blueFlagTextColor;
    private final Color orangeFlagTextColor;
    private final Color magentaFlagTextColor;
    private final Color dueHighlihgtColor;

    final private Image _workspaceImage;
    final private Image _projectImage;
    final private Image _newTaskImage;
    final private Image _inProgressImage;
    final private Image _finishedImage;
    final private Image _cancelledImage;
    final private Image _redFlagImage;
    final private Image _greenFlagImage;
    final private Image _blueFlagImage;
    final private Image _orangeFlagImage;
    final private Image _magentaFlagImage;
    final private Image _idleImage;
    final private Image _waitingImage;
    final private Image _activityImage;
    final private Image _closedProjectImage;
    final private Image pastDeadlineImage;

    public ItemStyleFactory(SWTMainWindow mainWindow, Color background) {
        _mainWindow = mainWindow;
        IconSet iconSet = mainWindow.getIconSet();
        _workspaceImage = iconSet.getIcon("workspace", true);
        _projectImage = iconSet.getIcon("project", true);
        _newTaskImage = iconSet.getIcon("newTask", true);
        _inProgressImage = iconSet.getIcon("inProgress", true);
        _finishedImage = iconSet.getIcon("finished", true);
        _cancelledImage = iconSet.getIcon("cancelled", true);

        _redFlagImage = iconSet.getIcon("redFlag", true);
        _greenFlagImage = iconSet.getIcon("greenFlag", true);
        _blueFlagImage = iconSet.getIcon("blueFlag", true);
        _orangeFlagImage = iconSet.getIcon("orangeFlag", true);
        _magentaFlagImage = iconSet.getIcon("magentaFlag", true);

        _idleImage = iconSet.getIcon("idle", true);
        _waitingImage = iconSet.getIcon("waiting", true);
        _activityImage = iconSet.getIcon("activity", true);
        _closedProjectImage = iconSet.getIcon("project-closed", true);
        pastDeadlineImage = iconSet.getIcon("past.due", true);

        Display display = mainWindow.getShell().getDisplay();
        this.normalTextColor = display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
        this.disabledTextColor = new Color(display, 128, 128, 192);
        this.refFlagTextColor = new Color(display, 255, 0, 0);
        this.greenFlagTextColor = new Color(display, 0, 127, 0);
        this.blueFlagTextColor = new Color(display, 0, 0, 255);
        this.orangeFlagTextColor = new Color(display, 255, 127, 0);
        this.magentaFlagTextColor = new Color(display, 200, 0, 100);
        dueHighlihgtColor = new Color(
            display,
            background.getRed(), background.getGreen() * 3/4, background.getBlue() *3/4);

    }

    public ItemStyle getItemStyle(ProjectTreeItem modelItem) {
        ItemStyle itemStyle = new ItemStyle();
        itemStyle.setForeground(normalTextColor);
        switch (modelItem.getItemType()) {
            case WORKSPACE:
                itemStyle.setImage(_workspaceImage);
                break;
            case PROJECT:
                if (modelItem.getCloseDateTime() == null) {
                    itemStyle.setImage(_projectImage);
                }
                else {
                    itemStyle.setImage(_closedProjectImage);
                }
                break;
            case TASK:
                Task task = (Task) modelItem;
                switch (task.getStatus().getId()) {
                    case TaskStatus.NOT_STARTED:
                        itemStyle.setImage(_newTaskImage);
                        setFontStyle(itemStyle, SWT.BOLD);
                        break;
                    case TaskStatus.IN_PROGRESS:
                        itemStyle.setImage(_inProgressImage);
                        setFontStyle(itemStyle, SWT.NORMAL);
                        break;
                    case TaskStatus.FINISHED:
                        itemStyle.setImage(_finishedImage);
                        setFontStyle(itemStyle, SWT.NORMAL);
                        itemStyle.setForeground(this.disabledTextColor);
                        itemStyle.setBackground(null);
                        break;
                    case TaskStatus.CANCELLED:
                        itemStyle.setImage(_cancelledImage);
                        setFontStyle(itemStyle, SWT.NORMAL);
                        itemStyle.setForeground(this.disabledTextColor);
                        itemStyle.setBackground(null);
                        break;
                    case TaskStatus.FLAGGED:
                        setFontStyle(itemStyle, SWT.NORMAL);
                        itemStyle.setImage(getFlagImage(task.getFlagColor()));
                        itemStyle.setForeground(getFlagTextColor(task.getFlagColor()));
                        break;
                    case TaskStatus.WAITING:
                        setFontStyle(itemStyle, SWT.NORMAL);
                        itemStyle.setImage(_waitingImage);
                        break;
                }
                break;
            case IDLE_TASK:
                itemStyle.setImage(_idleImage);
                IdleTask idle = (IdleTask) modelItem;
                if (!idle.isEnabled()) {
                    itemStyle.setForeground(this.disabledTextColor);
                }
                else {
                    itemStyle.setForeground(this.normalTextColor);
                }
                break;
            case ACTIVITY:
                Activity activity = (Activity) modelItem;
                switch (activity.getStatus().getId()) {
                    case TaskStatus.IN_PROGRESS:
                        itemStyle.setImage(_activityImage);
                        break;
                    case TaskStatus.CANCELLED:
                        itemStyle.setImage(_cancelledImage);
                        itemStyle.setForeground(this.disabledTextColor);
                        break;
                    case TaskStatus.FLAGGED:
                        itemStyle.setImage(getFlagImage(activity.getFlagColor()));
                        itemStyle.setForeground(getFlagTextColor(activity.getFlagColor()));
                        break;
                }
                break;
        }

        if (modelItem.isPastDeadline()) {
            itemStyle.setImage(pastDeadlineImage);
            itemStyle.setBackground(dueHighlihgtColor);
        }
        return itemStyle;
    }


    private void setFontStyle(ItemStyle item, int style) {
        Integer fontKey = style;
        Font fn;
        if (!_fontMap.containsKey(fontKey)) {
            FontData[] f = _mainWindow.getShell().getFont().getFontData();
            for (FontData fontData : f) {
                fontData.setStyle(style);
            }
            fn = new Font(_mainWindow.getShell().getDisplay(), f);
            _fontMap.put(fontKey, fn);
        }
        else {
            fn = _fontMap.get(fontKey);
        }
        item.setFont(fn);
    }


    private Image getFlagImage(TaskStatus.FlagColor flagColor) {
        switch (flagColor) {
            case RED:
                return _redFlagImage;
            case GREEN:
                return _greenFlagImage;
            case BLUE:
                return _blueFlagImage;
            case ORANGE:
                return _orangeFlagImage;
            case MAGENTA:
                return _magentaFlagImage;
        }
        return null;
    }

    private Color getFlagTextColor(TaskStatus.FlagColor flagColor) {
        switch (flagColor) {
            case RED:
                return refFlagTextColor;
            case GREEN:
                return greenFlagTextColor;
            case BLUE:
                return blueFlagTextColor;
            case ORANGE:
                return orangeFlagTextColor;
            case MAGENTA:
                return magentaFlagTextColor;
        }
        return null;
    }

}
