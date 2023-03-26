/*
 * Copyright (c) TimeCult Project Team, 2005-2023 (dyadix@gmail.com)
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

package net.sf.timecult.ui.swt.timer;

import net.sf.timecult.ui.swt.IconSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class TimerTrayImage {

    private final Image _image;
    private final GC    _gc;
    private final Color _blackColor;
    private final Color _whiteColor;
    private final Color _darkGrayColor;

    public TimerTrayImage(Display display) {
        _image = new Image(display, IconSet.ICON_SIZE, IconSet.ICON_SIZE);
        _gc = new GC(_image);
        _blackColor = display.getSystemColor(SWT.COLOR_BLACK);
        _whiteColor = display.getSystemColor(SWT.COLOR_WHITE);
        _darkGrayColor = display.getSystemColor(SWT.COLOR_DARK_GRAY);
    }

    public void fill(int degrees) {
        _gc.setBackground(_blackColor);
        _gc.fillRectangle(0, 0, IconSet.ICON_SIZE, IconSet.ICON_SIZE);
        _gc.setBackground(_darkGrayColor);
        _gc.fillOval(1,1, IconSet.ICON_SIZE - 1, IconSet.ICON_SIZE - 1);
        _gc.setBackground(_whiteColor);
        _gc.fillArc(1, 1, IconSet.ICON_SIZE - 1, IconSet.ICON_SIZE - 1, 0, degrees);
    }

    public Image get() {
        return _image;
    }
}
