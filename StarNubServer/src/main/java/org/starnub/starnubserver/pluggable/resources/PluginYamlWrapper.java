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

package org.starnub.starnubserver.pluggable.resources;

import org.starnub.starnubserver.resources.StarNubYamlWrapper;

public class PluginYamlWrapper extends StarNubYamlWrapper {

    /**
     * This will construct a YAML file, YAML dumper, YAML auto dumper
     * <p>
     * Note: Absolute file paths are not support and can only be references as such "" - For base starbound directory or StarNub/ for starnub directory, ect...
     * Resource paths are references by "/" or /StarNub/, we will build out the full path using the file and path you supply.
     *
     * @param OWNER                  String owner of this YAMLFile
     * @param FILE_NAME              String file name of the file
     * @param DEFAULT_FILE_PATH      Object default path to the file
     * @param DISK_FILE_PATH         String default path to file on the disk
     */
    public PluginYamlWrapper(String OWNER, String FILE_NAME, Object DEFAULT_FILE_PATH, String DISK_FILE_PATH) {
        super(OWNER,
              FILE_NAME,
              DEFAULT_FILE_PATH,
              DISK_FILE_PATH,
              false,
              true,
              true,
              false,
              true
        );
    }
}
