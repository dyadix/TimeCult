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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class ItemStyle {
    private Image _image;
    private Font  _font;
    private Color _foreground;
    private Color _background;

    public Font getFont() {
        return _font;
    }

    public void setFont(Font _font) {
        this._font = _font;
    }

    public Color getForeground() {
        return _foreground;
    }

    public void setForeground(Color _foreground) {
        this._foreground = _foreground;
    }

    public Color getBackground() {
        return _background;
    }

    public void setBackground(Color _background) {
        this._background = _background;
    }

    public Image getImage() {
        return _image;
    }

    public void setImage(Image _image) {
        this._image = _image;
    }
}
