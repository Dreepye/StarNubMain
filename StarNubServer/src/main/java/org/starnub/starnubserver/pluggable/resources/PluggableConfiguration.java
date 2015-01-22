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

import java.io.InputStream;
import java.util.Map;

/**
 * Represents StarNubs Configuration instance extending YAMLWrapper
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class PluggableConfiguration extends StarNubYamlWrapper {

    /**
     * For Java Plugins
     *
     * @param pluggableName
     * @param defaultPath
     */
    public PluggableConfiguration(String baseDir, String pluggableName, InputStream defaultPath) {
        super(
                pluggableName,
                pluggableName.toLowerCase() + "_configuration.yml",
                defaultPath,
                baseDir + pluggableName,
                false,
                true,
                true,
                true,
                true
        );
    }

    /**
     * For Python Plugins
     *
     * @param pluginName
     * @param configurationMap
     */
    public PluggableConfiguration(String baseDir, String pluginName, Map configurationMap) {
        super(
                pluginName,
                pluginName.toLowerCase() + "_configuration.yml",
                configurationMap,
                baseDir + pluginName,
                false,
                true,
                true,
                true,
                true
        );
    }
}
