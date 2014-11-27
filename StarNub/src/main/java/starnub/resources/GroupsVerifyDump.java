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

package starnub.resources;

import utilities.file.yaml.YAMLWrapper;

/**
 * Represents StarNubs GroupsVerifyDump instance extending YAMLWrapper
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class GroupsVerifyDump extends YAMLWrapper {

    /**
     * This will construct a YAML file, YAML dumper, YAML auto dumper
     *
     * @param starnubResources YAMLWrapper containing starnub resources
     */
    public GroupsVerifyDump(YAMLWrapper starnubResources) {
        super(
                "StarNub",
                (String) starnubResources.getListNestedValue(0, "default_groups_configuration", "file"),
                "resources",
                (String) starnubResources.getListNestedValue(1, "default_groups_configuration", "file"),
                false,
                true,
                true,
                true
        );
    }
}
