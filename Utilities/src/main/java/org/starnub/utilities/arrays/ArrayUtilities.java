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

package org.starnub.utilities.arrays;

import java.util.List;

public class ArrayUtilities {

    public static String[] arrayBuilder(List<String> list){
        if (list != null && list.size() > 0) {
            String[] arrayList = new String[list.size()];
            int index = 0;
            for (String string : list) {
                arrayList[index] = string;
                index++;
            }
            return arrayList;
        } else {
            return new String[0];
        }
    }

    public static Object[] joinArrays(Object[] a, Object[] b) {
        if (a == null){
            return b;
        } else if (b == null){
            return a;
        } else {
            int aLen = a.length;
            int bLen = b.length;
            Object[] c = new Object[aLen + bLen];
            System.arraycopy(a, 0, c, 0, aLen);
            System.arraycopy(b, 0, c, aLen, bLen);
            return c;
        }
    }
}
