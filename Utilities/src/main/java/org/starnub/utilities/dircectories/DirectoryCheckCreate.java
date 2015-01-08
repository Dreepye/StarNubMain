
/*
 * Copyright (C) 2014 www.StarNub.org - Underbalanced
 *
 * This file is part of org.starnubserver a Java Wrapper for Starbound.
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

package org.starnub.utilities.dircectories;


import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Directory Check and Create utility.
 * <p>
 * This class will conduct operations when constructed. It will create the main directory and
 * subdirectories in the same directory of the program that called this class.
 * It will only however create the directory "one" directory deep.
 * <ul>
 * <li>String/
 * <li>String/String1
 * <li>String/String2
 * <li>String/String3
 * </ul>
 * <p>
 * Construction 1 - Check and create the directories but provide no confirmation of check and creation.
 * <br>
 * <b>new DirectoryConfiguration("StringDirectory", "String1SubDirectory", "String2SubDirectory")</b>
 * <p>
 * Construction 2 - Enable catching of the results from Directory Check and Creation with dir.getResults().
 * <br>
 * <b>DirectoryConfiguration dir = new DirectoryConfiguration("StringDirectory", "String1SubDirectory", "String2SubDirectory")</b>
 *
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */

public class DirectoryCheckCreate {

    /**
     * Program Name will be the base directory.
     */
    private String programName;

    /**
     * Directory Names will be the sub directories.
     */
    private String[] directoryNames;

    /**
     * Results of directory check.
     * <p>
     * Already exist (true), created (true) or failed to created (false).
     */
    private final LinkedHashMap<String, Boolean> results = new LinkedHashMap<>();

    /**
     *
     * @return LinkedHashMap {@code<Directory, Boolean>} containing the directory creation results.
     * @see DirectoryCheckCreate#results
     */
    public LinkedHashMap<String, Boolean> getResults() {
        return results;
    }

    /**
     * Runs the programName through dirAdd();. Then loops through the String[]
     * and builds new strings for dirAdd();/
     *
     * @see DirectoryCheckCreate#dirAdd(String)
     */
    public static LinkedHashMap<String, Boolean> dirCheck(String programName, String... directoryNames) {
        LinkedHashMap<String, Boolean> linkedHashMap = dirAdd(programName);
        if (!programName.endsWith("/")){
            programName = programName + "/";
        }
        for (String directoryFromArray : directoryNames) {
            linkedHashMap.putAll(dirAdd(programName + directoryFromArray));
        }
        return linkedHashMap;
    }

    /**
     * Runs the programName through dirAdd();. Then loops through the String[]
     * and builds new strings for dirAdd();/
     *
     * @see DirectoryCheckCreate#dirAdd(String)
     */
    public static LinkedHashMap<String, Boolean> dirCheck(String programName, ArrayList<String> directoryNames) {
        LinkedHashMap<String, Boolean> linkedHashMap = dirAdd(programName);
        if (!programName.endsWith("/")){
            programName = programName + "/";
        }
        for (String directoryFromArray : directoryNames) {
            linkedHashMap.putAll(dirAdd(programName + directoryFromArray));
        }
        return linkedHashMap;
    }

    /**
     * Checks the String it was given to see if the directory exist. If exist,
     * was created or failed we will add the result to the LinkedHashMap
     *
     * @param directoryString Directory String that will be converted to a Directory.
     * @see DirectoryCheckCreate#results
     */
    private static LinkedHashMap<String, Boolean> dirAdd(String directoryString) {
        LinkedHashMap<String, Boolean> results = new LinkedHashMap<>();
        try {
            File directory = new File(directoryString);
            if (!directory.exists()) {
                boolean result = directory.mkdir();
                if (result) {
                    results.put(directoryString, true);
                } else {
                    results.put(directoryString, false);
                }
            } else if (directory.exists()){
                results.put(directoryString, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
