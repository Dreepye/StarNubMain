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

package org.starnub.starnubserver.pluggable;

import org.starnub.starnubserver.pluggable.exceptions.MissingData;
import org.starnub.starnubserver.pluggable.exceptions.PluginDirectoryCreationFailed;
import org.starnub.starnubserver.pluggable.plugins.resources.PluginYAMLWrapper;
import org.starnub.starnubserver.pluggable.plugins.resources.YAMLFiles;
import org.starnub.utilities.file.utility.JarFromDisk;
import org.starnub.utilities.file.yaml.YAMLWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public abstract class JavaPlugin extends Plugin<JavaPlugin> {

    private YAMLFiles files;

    /**
     * Creates a new <code>File</code> instance by converting the given
     * pathname string into an abstract pathname.  If the given string is
     * the empty string, then the result is the empty abstract pathname.
     *
     * @param pathname A pathname string
     * @throws NullPointerException If the <code>pathname</code> argument is <code>null</code>
     */
    public JavaPlugin(String pathname) throws MissingData, IOException, PluginDirectoryCreationFailed {
        super(pathname);
    }

    /**
     * Creates a new <tt>File</tt> instance by converting the given
     * <tt>file:</tt> URI into an abstract pathname.
     * <p>
     * <p> The exact form of a <tt>file:</tt> URI is system-dependent, hence
     * the transformation performed by this constructor is also
     * system-dependent.
     * <p>
     * <p> For a given abstract pathname <i>f</i> it is guaranteed that
     * <p>
     * <blockquote><tt>
     * new File(</tt><i>&nbsp;f</i><tt>.{@link #toURI() toURI}()).equals(</tt><i>&nbsp;f</i><tt>.{@link #getAbsoluteFile() getAbsoluteFile}())
     * </tt></blockquote>
     * <p>
     * so long as the original abstract pathname, the URI, and the new abstract
     * pathname are all created in (possibly different invocations of) the same
     * Java virtual machine.  This relationship typically does not hold,
     * however, when a <tt>file:</tt> URI that is created in a virtual machine
     * on one operating system is converted into an abstract pathname in a
     * virtual machine on a different operating system.
     *
     * @param uri An absolute, hierarchical URI with a scheme equal to
     *            <tt>"file"</tt>, a non-empty path component, and undefined
     *            authority, query, and fragment components
     * @throws NullPointerException     If <tt>uri</tt> is <tt>null</tt>
     * @throws IllegalArgumentException If the preconditions on the parameter do not hold
     * @see #toURI()
     * @see java.net.URI
     * @since 1.4
     */
    public JavaPlugin(URI uri) throws MissingData, IOException, PluginDirectoryCreationFailed {
        super(uri);
    }

    @Override
    public void loadData(YAMLWrapper pluggableInfo) throws MissingData, IOException, PluginDirectoryCreationFailed {
        super.loadData(pluggableInfo);
        String pluginDir = "Plugins";
        JarFromDisk jarFromDisk = new JarFromDisk(super.getAbsoluteFile());
        List<JarEntry> otherJarFiles = jarFromDisk.getJarEntries("other_files/", null);
        List<JarEntry> yamlJarEntries = jarFromDisk.getJarEntries("yaml_files/", null);
        ArrayList<String> directories = new ArrayList<>();
        directories.addAll(otherJarFiles.stream().filter(ZipEntry::isDirectory).map(JarEntry::toString).collect(Collectors.toList()));
        directories.addAll(yamlJarEntries.stream().filter(ZipEntry::isDirectory).map(JarEntry::toString).collect(Collectors.toList()));
        createDirectories(pluginDir, pluggableDetails.getNAME(), directories);
        otherFilesExtractor(pluginDir, jarFromDisk, otherJarFiles);
    }

    private void otherFilesExtractor(String PLUGIN_DIR, JarFromDisk jarFromDisk, List<JarEntry> otherJarFiles) throws PluginDirectoryCreationFailed, IOException {
        for (JarEntry jarEntry : otherJarFiles) {
            if (!jarEntry.isDirectory()) {
                jarFromDisk.extractEntry(jarEntry,  PLUGIN_DIR + jarEntry.toString());
            }
        }
    }

    private YAMLFiles yamlFiles(String PLUGIN_NAME, String PLUGIN_DIR, URLClassLoader CLASS_LOADER, List<JarEntry> yamlJarEntries, JarFromDisk jarFromDisk) throws IOException, PluginDirectoryCreationFailed {
        HashSet<PluginYAMLWrapper> pluginYAMLWrapperHashSet = new HashSet<>();
        for (JarEntry jarEntry :yamlJarEntries) {
            if (!jarEntry.isDirectory()) {
                String fileName = jarFromDisk.getJarEntryFileName(jarEntry);
                try (InputStream resourceAsStream = CLASS_LOADER.getResourceAsStream(jarEntry.toString())) {
                    final PluginYAMLWrapper pluginYAMLWrapper = new PluginYAMLWrapper(PLUGIN_NAME, fileName, resourceAsStream, PLUGIN_DIR + jarEntry.toString());
                    pluginYAMLWrapperHashSet.add(pluginYAMLWrapper);
                }
            }
        }
        return new YAMLFiles(pluginYAMLWrapperHashSet);
    }
}
