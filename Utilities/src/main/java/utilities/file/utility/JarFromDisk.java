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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFromDisk {


    private final JarFile JAR_FILE;

    public JarFromDisk(String jarFileString) throws IOException {
        File jarFileFile = new File(jarFileString);
        this.JAR_FILE = new JarFile(jarFileFile);
    }

    public JarFromDisk(File jarFileFile) throws IOException {
        this.JAR_FILE = new JarFile(jarFileFile);;
    }

    public JarFromDisk(JarFile JAR_FILE) {
        this.JAR_FILE = JAR_FILE;
    }

    /**
     * * This will search a Jar recursively for specific files in a directory and with a filter.
     *
     * @param directoryToSearch String directory to search
     * @param fileExtensionFilter String file extension filter
     * @return List of resource paths
     */
    public List<String> getJarResourcePaths (String directoryToSearch, String fileExtensionFilter) {
        List<String> fileNames =  new ArrayList<>();
        for (Enumeration<JarEntry> em = JAR_FILE.entries(); em.hasMoreElements();) {
            JarEntry jarEntry = em.nextElement();
            String filePath = jarEntry.toString();
            if (filePath.contains(directoryToSearch)) {
                if (fileExtensionFilter != null){
                    if (filePath.endsWith(fileExtensionFilter)) {
                        fileNames.add(filePath);
                    }
                } else {
                    fileNames.add(filePath);
                }
            }
        }
        return fileNames;
    }

    /**
     * * This will search a Jar recursively for specific files in a directory and with a filter.
     *
     * @param directoryToSearch String directory to search
     * @param fileExtensionFilter String file extension filter
     * @return List of jar entries paths
     */
    public List<JarEntry> getJarEntries (String directoryToSearch, String fileExtensionFilter) {
        List<JarEntry> jarEntries =  new ArrayList<>();
        for (Enumeration<JarEntry> em = JAR_FILE.entries(); em.hasMoreElements();) {
            JarEntry jarEntry = em.nextElement();
            String filePath = jarEntry.toString();
            if (filePath.startsWith(directoryToSearch)) {
                if (fileExtensionFilter != null){
                    if (filePath.endsWith(fileExtensionFilter)) {
                        jarEntries.add(jarEntry);
                    }
                } else {
                    jarEntries.add(jarEntry);
                }
            }
        }
        return jarEntries;
    }

    public void extractEntry(JarEntry jarEntry, String destinationPath) throws IOException {
        try (
        InputStream is = JAR_FILE.getInputStream(jarEntry);
        FileOutputStream fos = new FileOutputStream(destinationPath)){
            while (is.available() > 0) {
                fos.write(is.read());
            }
        }
    }

    public String getJarEntryFileName(JarEntry jarEntry){
        String jarEntryString = jarEntry.toString();
        int index = jarEntryString.lastIndexOf("/") + 1;
        return jarEntryString.substring(index);
    }
}