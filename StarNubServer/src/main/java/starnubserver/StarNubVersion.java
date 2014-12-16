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

package starnubserver;

import starnubserver.resources.ResourceManager;
import starnubserver.resources.StarNubYamlWrapper;

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

    private final String PHASE;
    private final double VERSION;
    private final String PHASE_VERSION;
    private final ArrayList<String> LANGUAGES;
    private final double SIZE_MBS;
    private final String DESCRIPTION;

    /**
     * This constructor is private - Singleton Pattern
     */
    private StarNubVersion() {
        StarNubYamlWrapper starnubVersionYaml = new StarNubYamlWrapper(
                "StarNub",
                (String) ResourceManager.getInstance().getListNestedValue(0, "starnub_version", "file"),
                ResourceManager.getInstance().getNestedValue("starnub_version", "map"),
                (String) ResourceManager.getInstance().getListNestedValue(1, "starnub_version", "file")
        );
        PHASE = (String) starnubVersionYaml.getValue("phase");
        VERSION = (double) starnubVersionYaml.getValue("version");
        PHASE_VERSION = PHASE + "-" + Double.toString(VERSION);
        LANGUAGES = (ArrayList<String>) starnubVersionYaml.getValue("languages");
        SIZE_MBS = (double) starnubVersionYaml.getValue("size");
        DESCRIPTION = (String) starnubVersionYaml.getValue("description");
    }

    /**
     * This constructor is private - Singleton Pattern
     */
    @SuppressWarnings("unchecked")
    public static StarNubVersion getInstance() {
        return instance;
    }

    public String getPHASE() {
        return PHASE;
    }

    public double getVERSION() {
        return VERSION;
    }

    public String getPHASE_VERSION() {
        return PHASE_VERSION;
    }

    public ArrayList<String> getLANGUAGES() {
        return LANGUAGES;
    }

    public double getSIZE_MBS() {
        return SIZE_MBS;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will return a String Banner with the StarNub Phase and Version
     *
     * @return String representing the starnubserver banner
     */
    public String versionBanner() {
        return "\n"
                + "=======================================================\n"
                + "                   StarNub " + PHASE + "\n"
                + "                   Version " + VERSION + "\n"
                + "=======================================================\n";
    }

    @Override
    public String toString() {
        return "StarNubVersion{" +
                "PHASE='" + PHASE + '\'' +
                ", VERSION=" + VERSION +
                ", PHASE_VERSION='" + PHASE_VERSION + '\'' +
                ", LANGUAGES=" + LANGUAGES +
                ", SIZE_MBS=" + SIZE_MBS +
                ", DESCRIPTION='" + DESCRIPTION + '\'' +
                '}';
    }
}
