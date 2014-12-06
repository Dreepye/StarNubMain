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

package starnubserver.resources;

import utilities.file.yaml.YAMLWrapper;

/**
 * Represents StarNubs ServerStats instance extending YAMLWrapper
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ServerStats extends YAMLWrapper {

    /**
     * This will construct a YAML file, YAML dumper, YAML auto dumper
     *
     * @param starnubResources YAMLWrapper containing starnubserver resources
     */
    public ServerStats(YAMLWrapper starnubResources) {
        super(
                "StarNub",
                (String) starnubResources.getListNestedValue(0, "server_stats", "file"),
                starnubResources.getNestedValue("server_stats", "map"),
                (String) starnubResources.getListNestedValue(1, "server_stats", "file"),
                false,
                true,
                true,
                true,
                true
        );
    }
}
//DB