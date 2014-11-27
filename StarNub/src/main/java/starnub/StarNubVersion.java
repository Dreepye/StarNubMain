/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This utilities.file is part of org.starnub a Java Wrapper for Starbound.
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

package starnub;

import utilities.yaml.YAMLWrapper;

import java.util.ArrayList;

/**
 * Represents StarNubsVersion instance
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarNubVersion {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final StarNubVersion instance = new StarNubVersion();

    private static String PHASE;
    private static double VERSION;
    private static String PHASE_VERSION;
    private static ArrayList<String> LANGUAGES;
    private static double SIZE_MBS;
    private static String DESCRIPTION;

    /**
     * This constructor is private - Singleton Pattern
     */
    private StarNubVersion() {
    }

    /**
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This will set and get StarNubVersion Singleton instance
     *
     * @param starnubResources YAMLWrapper containing the resources
     * @return StarNubVersion Singleton Instance
     */
    @SuppressWarnings("unchecked")
    public static StarNubVersion getInstance(YAMLWrapper starnubResources) {
        if (PHASE == null) {
            YAMLWrapper starnubVersion = new YAMLWrapper(
                    "StarNub",
                    (String) starnubResources.getListNestedValue(0, "starnub_version", "file"),
                    "resources",
                    (String) starnubResources.getListNestedValue(1, "starnub_version", "file"),
                    false,
                    false,
                    true,
                    false
            );
            PHASE = (String) starnubVersion.getValue("phase");
            VERSION = (double) starnubVersion.getValue("version");
            PHASE_VERSION = PHASE + "-" + Double.toString(VERSION);
            LANGUAGES = (ArrayList<String>) starnubVersion.getValue("languages");
            SIZE_MBS = (double) starnubVersion.getValue("size");
            DESCRIPTION = (String) starnubVersion.getValue("description");
        }
        return instance;
    }

    public static StarNubVersion getInstance() {
        return instance;
    }

    public static String getPHASE() {
        return PHASE;
    }

    public static double getVERSION() {
        return VERSION;
    }

    public static String getPhaseVersion() {
        return PHASE_VERSION;
    }

    public static ArrayList<String> getLANGUAGES() {
        return LANGUAGES;
    }

    public static double getSizeMbs() {
        return SIZE_MBS;
    }

    public static String getDESCRIPTION() {
        return DESCRIPTION;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will return a String Banner with the StarNub Phase and Version
     *
     * @return
     */
    public String versionBanner() {
        return "\n"
                + "=======================================================\n"
                + "                   StarNub " + PHASE + "\n"
                + "                   Version " + VERSION + "\n"
                + "=======================================================\n";
    }
}
