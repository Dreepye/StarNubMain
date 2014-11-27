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

import utilities.exceptions.CollectionDoesNotExistException;
import utilities.file.yaml.YAMLWrapper;

import java.io.IOException;

/**
 * Represents StarNubs Internally Banned IPs instance extending YAMLWrapper
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class BannedIPs extends YAMLWrapper {

    /**
     * This will construct a YAML file, YAML dumper, YAML auto dumper
     *
     * @param starnubResources YAMLWrapper containing starnub resources
     */
    public BannedIPs(YAMLWrapper starnubResources) {
        super(
                "StarNub",
                (String) starnubResources.getListNestedValue(0, "internally_blocked_ips", "file"),
                "resources",
                (String) starnubResources.getListNestedValue(1, "internally_blocked_ips", "file"),
                false,
                true,
                true,
                true
        );
    }

    /**
     * This method will add a value to your internally blocked ips
     *
     * @param value the Object that you would like to add to your list or set
     * @return boolean if the item was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean addToBannedIPs(Object value) throws IOException, CollectionDoesNotExistException {
        return super.addToCollection(value, "ips");
    }

    /**
     * This method will remove a value to your internally blocked ips
     *
     * @param value the Object that you would like to remove to your list or set
     * @return boolean if the item was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean removeFromBannedIPs(Object value) throws IOException {
        return super.removeFromCollection(value, "ips");
    }

    /**
     * This method will check to see if a  your internally blocked ips has a specific value
     *
     * @param value Object to check the list or set for
     * @return boolean if the item was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean bannedIPsContains(Object value) throws IOException, NullPointerException {
        return super.collectionContains(value, "ips");
    }
}
