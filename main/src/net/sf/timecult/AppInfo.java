/*
 * File: AppInfo.java
 *  
 * Copyright (c) Rustam Vishnyakov, 2005-2006 (rvishnyakov@yahoo.com)
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
package net.sf.timecult;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class AppInfo {

    public final static String APPNAME = "appname";
    public final static String AUTHOR = "author";
    public final static String E_MAIL = "eMail";
    public final static String MAJOR_VERSION = "majorVersion";
    public final static String MINOR_VERSION = "minorVersion";
    public final static String VERSION_TYPE = "Beta";
    public final static String BUILD = "build";
    public final static String BUILD_DATE = "buildDate";

    public static String getValue(String attrName) {
        return appInfoBundle.getString(attrName);
    }

    public static String getAppName() {
        return getValue(APPNAME);
    }

    public static String getAuthor() {
        return getValue(AUTHOR);
    }

    public static String getEMail() {
        return getValue(E_MAIL);
    }

    public static String getMajorVersion() {
        return getValue(MAJOR_VERSION);
    }

    public static String getMinorVersion() {
        return getValue(MINOR_VERSION);
    }

    public static String getVersionType() {
        return getValue(VERSION_TYPE);
    }

    public static String getBuild() {
        return getValue(BUILD);
    }

    public static String getBuildDate() {
        String dateValue = getValue(BUILD_DATE);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
        DateFormat localDF = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        try {
            Date date = df.parse(dateValue);
            dateValue = localDF.format(date);
        }
        catch (ParseException e) {
            // ignore
        }
        return dateValue;
    }
    
    public static String getVersionString() {
        return getMajorVersion() + "." + getMinorVersion() + ", Build " + getBuild();
    }

    private static ResourceBundle appInfoBundle = ResourceBundle
            .getBundle(ResourceHelper.class.getPackage().getName() + ".appinfo");
}
