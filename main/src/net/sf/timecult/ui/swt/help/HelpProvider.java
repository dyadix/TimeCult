/*
 * Copyright (c) Rustam Vishnyakov, 2007-2010 (dyadix@gmail.com)
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
 * $Id: HelpProvider.java,v 1.3 2010/03/02 12:13:59 dyadix Exp $
 */
package net.sf.timecult.ui.swt.help;

import java.io.File;
import java.io.IOException;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.util.LinkHelper;

import org.eclipse.swt.program.Program;

/**
 * Creates and manages the help window.
 */
public class HelpProvider {
    

    /**
     * Opens help window.
     */
    public static void openHelp() {
        String baseUrl = ResourceHelper.getString("help.baseUrl");
        Program.launch(baseUrl);
    }
}
