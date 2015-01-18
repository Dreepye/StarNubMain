/*
 * Copyright (C) 2014 www.StarNub.org - Underbalanced
 *
 * This file is part of org.starnub a Java Wrapper for Starbound.
 *
 * This above mentioned StarNub software is free software:
 * you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free
 * Software Foundation, either version  3 of the License, or
 * any later version. This above mentioned CodeHome software
 * is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
 * the GNU General Public License for more details. You should
 * have received a copy of the GNU General Public License in
 * this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.starnub.utilities.plugin;

import org.starnub.utilities.file.utility.FileSizeMeasure;
import org.starnub.utilities.file.utility.GetFileSize;

import java.io.File;


/**
 * This represents a plugable which is any file that can be loaded and executed in some form
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public abstract class Pluggable {

    private final String NAME;
    private final double VERSION;
    private final String DESCRIPTION;
    private final String AUTHOR;
    private final String URL;
    private final File FILE;
    private final double SIZE_KBS;

    public Pluggable(String NAME, double VERSION, String DESCRIPTION, String AUTHOR, String URL, File FILE) {
        this.NAME = NAME;
        this.VERSION = VERSION;
        this.AUTHOR = AUTHOR;
        this.DESCRIPTION = DESCRIPTION;
        this.URL = URL;
        this.FILE = FILE;
        this.SIZE_KBS = GetFileSize.getFileSize(FILE, FileSizeMeasure.KILOBYTES);
    }

    public String getNAME() {
        return NAME;
    }

    public double getVERSION() {
        return VERSION;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public String getAUTHOR() {
        return AUTHOR;
    }

    public String getURL() {
        return URL;
    }

    public File getFILE() {
        return FILE;
    }

    public double getSIZE_KBS() {
        return SIZE_KBS;
    }

    public abstract void load();
    public abstract void unload();
    public abstract void reload();
    public abstract void upgrade();

    @Override
    public String toString() {
        return "Pluggable{" +
                "NAME='" + NAME + '\'' +
                ", VERSION=" + VERSION +
                ", DESCRIPTION='" + DESCRIPTION + '\'' +
                ", AUTHOR='" + AUTHOR + '\'' +
                ", URL='" + URL + '\'' +
                ", FILE=" + FILE +
                ", SIZE_KBS=" + SIZE_KBS +
                '}';
    }
}



