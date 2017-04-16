/*
 * Copyright (c) TimeCult Project Team, 2005-2011 (dyadix@gmail.com)
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

import com.sun.istack.internal.NotNull;
import net.sf.timecult.ResourceHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.io.*;

public class FontResource {
    private final Font _font;

    public FontResource(@NotNull Shell shell, @NotNull String fontName, int fontSize, @NotNull String resourceUri) {
        _font = load(shell.getDisplay(), fontName, shell.getFont(), fontSize, resourceUri);
        shell.addDisposeListener(
            new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent e) {
                    _font.dispose();
                }
            }
        );
    }

    private Font load(
        Display display,
        @NotNull String fontName,
        @NotNull Font fallbakcFont,
        int fontSize,
        @NotNull String resourceUrl) {
        InputStream inputStream = ResourceHelper.openStream(resourceUrl);
        OutputStream outputStream = null;
        try {
            File tmpFile = File.createTempFile("tmp_", ".ttf");
            outputStream = new FileOutputStream(tmpFile);
            int byteRead;
            while ((byteRead = inputStream.read()) >= 0) {
                outputStream.write(byteRead);
            }
            outputStream.close();
            if (display.loadFont(tmpFile.getPath())) {
                return new Font(display, fontName, fontSize, SWT.NORMAL);
            }
        } catch (IOException e) {
            /*ignore*/
        }
        finally {
            try {
                inputStream.close();
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (IOException e) { /*ignore*/ }
        }
        FontData[] fd = fallbakcFont.getFontData();
        fd[0].setHeight(fontSize);
        fd[0].setStyle(SWT.BOLD);
        return new Font(display, fd);
    }

    public Font getFont() {
        return _font;
    }
}
