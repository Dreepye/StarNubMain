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
import org.starnub.starnubserver.pluggable.generic.PluggableDetails;
import org.starnub.utilities.file.yaml.YAMLWrapper;

import java.io.File;
import java.io.IOException;

public abstract class Pluggable {

    protected PluggableDetails pluggableDetails;
    protected File pluggableFile;
    protected PluggableFileType pluggableFileType;

    public void setPluggable(UnloadedPluggable unloadedPluggable) throws PluginDirectoryCreationFailed, MissingData, IOException {
        pluggableDetails = unloadedPluggable.getPluggableDetails();
        pluggableFile = unloadedPluggable.getPluggableFile();
        pluggableFileType = unloadedPluggable.getPluggableFileType();
        YAMLWrapper yamlWrapper = unloadedPluggable.getYamlWrapper();
        loadData(yamlWrapper);
    }

    public PluggableDetails getPluggableDetails() {
        return pluggableDetails;
    }

    public File getPluggableFile() {
        return pluggableFile;
    }

    public PluggableFileType getPluggableFileType() {
        return pluggableFileType;
    }

    public abstract void loadData(YAMLWrapper pluggableInfo) throws IOException, PluginDirectoryCreationFailed;
    public abstract void dumpDetails() throws IOException;

    @Override
    public String toString() {
        return "Pluggable{" +
                "pluggableDetails=" + pluggableDetails +
                ", pluggableType=" + pluggableFileType +
                "} " + super.toString();
    }
}
