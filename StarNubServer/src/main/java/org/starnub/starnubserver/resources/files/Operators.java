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

package org.starnub.starnubserver.resources.files;

import org.starnub.starnubserver.resources.ResourceManager;
import org.starnub.starnubserver.resources.StarNubYamlWrapper;
import org.starnub.utilities.cache.exceptions.CollectionDoesNotExistException;

import java.io.IOException;
import java.util.UUID;

/**
 * Represents StarNubs Operators instance extending YAMLWrapper
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Operators extends StarNubYamlWrapper {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final Operators instance = new Operators();

    /**
     * This constructor is private - Singleton Pattern
     */
    public Operators( ) {
        super(
                "StarNub",
                (String) ResourceManager.getInstance().getListNestedValue(0, "operators", "file"),
                ResourceManager.getInstance().getNestedValue("operators", "map"),
                (String) ResourceManager.getInstance().getListNestedValue(1, "operators", "file"),
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
    public static Operators getInstance() {
        return instance;
    }

    /**
     * This method will add a value to your operators
     *
     * @param value the Object representing a UUID that you would like to add to your list or set
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean addToOperators(Object value) throws IOException, CollectionDoesNotExistException {
        if (value instanceof UUID){
            value = value.toString();
        }
        return super.addToCollection(value, false, false, "uuids");
    }

    /**
     * This method will remove a value to your operators
     *
     * @param value the Object that you would like to remove to your list or set
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean removeFromOperators(Object value) throws IOException {
        if (value instanceof UUID){
            value = value.toString();
        }
        return super.removeFromCollection(value, "uuids");
    }

    /**
     * This method will check to see if a  your operators has a specific value
     *
     * @param value Object to check the list or set for
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean operatorsContains(Object value) throws IOException, NullPointerException {
        return super.collectionContains(value, "uuids");
    }
}
