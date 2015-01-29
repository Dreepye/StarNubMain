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

package org.starnub.starnubserver.pluggable.generic;

import org.starnub.starnubserver.pluggable.Pluggable;
import org.starnub.starnubserver.pluggable.UnloadedPluggable;
import org.starnub.utilities.arrays.ArrayUtilities;

import java.util.Comparator;

public class DependencyComparator<T> implements Comparator<T> {

    public int compare(T pd1, T pd2) {
        PluggableDetails pluggableDetails1 = null;
        PluggableDetails pluggableDetails2 = null;
        if (pd1 instanceof UnloadedPluggable){
            pluggableDetails1 = ((UnloadedPluggable) pd1).getDetails();
            pluggableDetails2 = ((UnloadedPluggable) pd2).getDetails();
        } else if (pd1 instanceof Pluggable) {
            pluggableDetails1 = ((Pluggable) pd1).getDetails();
            pluggableDetails2 = ((Pluggable) pd2).getDetails();
        }
        return compareDetails(pluggableDetails1, pluggableDetails2);
    }

    private static int compareDetails(PluggableDetails pd1, PluggableDetails pd2){
        String[] pd1Dependencies = pd1.getDEPENDENCIES();
        String[] pd2Dependencies = pd2.getDEPENDENCIES();

        boolean pd1HasDependencies = false;
        if (pd1Dependencies != null) {
            if (pd1Dependencies.length != 0) {
                pd1HasDependencies = !pd1Dependencies[0].isEmpty();
            }
        }
        boolean pd2HasDependencies = false;
        if (pd2Dependencies != null) {
            if (pd2Dependencies.length != 0) {
                pd2HasDependencies = !pd2Dependencies[0].isEmpty();
            }
        }
        if (!pd1HasDependencies && !pd2HasDependencies){
            return 1;
        } else if (pd1HasDependencies && !pd2HasDependencies) {
            return -1;
        } else {
            String pd1Name = pd1.getNAME();
            boolean pd1DecencyOfPd2 = ArrayUtilities.arrayContains(pd2Dependencies, pd1Name);
            if (pd1DecencyOfPd2){
                return -1;
            }
            String pd2Name = pd2.getNAME();
            boolean pd2DecencyOfPd1 = ArrayUtilities.arrayContains(pd1Dependencies, pd2Name);
            if(pd2DecencyOfPd1){
                return 1;
            }
        }
        return 1;
    }
}