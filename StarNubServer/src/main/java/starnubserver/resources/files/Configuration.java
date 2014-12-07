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

package starnubserver.resources.files;

import starnubserver.resources.ResourceManager;
import utilities.file.yaml.YAMLWrapper;

/**
 * Represents StarNubs Configuration instance extending YAMLWrapper
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Configuration extends YAMLWrapper {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final Configuration instance = new Configuration();

    /**
     * This constructor is private - Singleton Pattern
     */
    public Configuration() {
        super(
                "StarNub",
                (String) ResourceManager.getInstance().getListNestedValue(0, "default_configuration", "file"),
                ResourceManager.getInstance().getNestedValue("default_configuration", "map"),
                (String) ResourceManager.getInstance().getListNestedValue(1, "default_configuration", "file"),
                false,
                true,
                true,
                true,
                true
        );
    }

    /**
     * This returns this Singleton - Singleton Pattern
     */
    public static Configuration getInstance() {
        return instance;
    }
}
