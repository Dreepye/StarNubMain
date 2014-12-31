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

import starnubserver.resources.tokens.StringToken;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTokens extends ConcurrentHashMap<String, StringToken>{

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final StringTokens instance = new StringTokens();

    /**
     * This constructor is private - Singleton Pattern
     */
    private StringTokens() {
    }

    /**
     * This returns this Singleton - Singleton Pattern
     */
    public static StringTokens getInstance() {
        return instance;
    }


    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will search for any string tokens {}, {players} and replace it with an object from the executed method
     * <p>
     *
     * @param string String representing the whole entire message to be scanned for color shortcuts
     * @return String repenting the whole entire message with the shortcut colors replaced with hex colors for game display
     */
    public static String replaceTokens(String string) {
        Pattern p = Pattern.compile("\\{.*?\\}");
        Matcher m = p.matcher(string);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String shortcut = m.group().toLowerCase();
            StringToken stringToken = StringTokens.getInstance().get(shortcut);
            Object results = stringToken.getResults();
            m.appendReplacement(sb, results.toString());
        }
        return m.appendTail(sb).toString();
    }
}
