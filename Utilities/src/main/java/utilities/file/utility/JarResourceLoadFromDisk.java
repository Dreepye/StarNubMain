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

package utilities.file.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarResourceLoadFromDisk {

    public List<String> loadJarResources (File jarToLoad, String directoryToSearch, String filter) {

        JarFile jarFile = null;
        List<String> fileNames =  new ArrayList<>();

        try {
            jarFile = new JarFile(jarToLoad);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        for (Enumeration<JarEntry> em = jarFile.entries(); em.hasMoreElements(); ) {
            String filePath = em.nextElement().toString();
            if (filePath.contains(directoryToSearch)) {
                if (filePath.endsWith(filter)) {
                    fileNames.add(filePath);
                }
            }
        }
        return fileNames;
    }
}