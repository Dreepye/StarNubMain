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

package org.starnub.utilities.file.utility;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;

public class FileManager {

    private final File DIRECTORY;
    private final String[] EXTENSIONS;

    public FileManager(String DIRECTORY, String[] EXTENSIONS) {
        this.DIRECTORY = new File(DIRECTORY);
        this.EXTENSIONS = EXTENSIONS;
    }

    private File[] scan(){
        Collection<File> files = FileUtils.listFiles(DIRECTORY, EXTENSIONS, false);
        return FileUtils.convertFileCollectionToFileArray(files);
    }

}
