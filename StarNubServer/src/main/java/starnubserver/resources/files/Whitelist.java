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
import utilities.exceptions.CollectionDoesNotExistException;
import utilities.file.yaml.YAMLWrapper;

import java.io.IOException;

/**
 * Represents StarNubs User Whitelist instance extending YAMLWrapper
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Whitelist extends YAMLWrapper {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final Whitelist instance = new Whitelist();

    /**
     * This constructor is private - Singleton Pattern
     */
    public Whitelist( ) {
        super(
                "StarNub",
                (String) ResourceManager.getInstance().getListNestedValue(0, "whitelist", "file"),
                ResourceManager.getInstance().getNestedValue("whitelist", "map"),
                (String) ResourceManager.getInstance().getListNestedValue(1, "whitelist", "file"),
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
    public static Whitelist getInstance() {
        return instance;
    }

    /**
     * This method will add a value to your whitelist
     *
     * @param value the Object that you would like to add to your list or set
     * @return boolean if the item was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean addToWhitelist(Object value) throws IOException, CollectionDoesNotExistException {
        return super.addToCollection(value, "uuid_ip");
    }

    /**
     * This method will remove a value to your  whitelist
     *
     * @param value the Object that you would like to remove to your list or set
     * @return boolean if the item was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean removeFromWhitelist(Object value) throws IOException {
        return super.removeFromCollection(value, "uuid_ip");
    }

    /**
     * This method will check to see if your whitelist specific value
     *
     * @param value Object to check the list or set for
     * @return boolean if the item was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean whitelistContains(Object value) throws IOException, NullPointerException {
        return super.collectionContains(value, "uuid_ip");
    }
}
