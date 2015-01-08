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

package org.starnub.starnubserver;

import org.starnub.starnubserver.resources.ResourceManager;
import org.starnub.starnubserver.resources.StarNubYamlWrapper;

import java.util.List;

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
    private final String AUTHOR;
    private final List<String> CONTRIBUTORS;
    private final List<Integer> STARBOUND_VERSION;
    private final List<String> LANGUAGES;
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
        AUTHOR = (String) starnubVersionYaml.getValue("author");
        CONTRIBUTORS = (List<String>) starnubVersionYaml.getValue("contributors");
        STARBOUND_VERSION = (List<Integer>) starnubVersionYaml.getValue("starbound_versions");
        LANGUAGES = (List<String>) starnubVersionYaml.getValue("languages");
        SIZE_MBS = (double) starnubVersionYaml.getValue("size");
        DESCRIPTION = (String) starnubVersionYaml.getValue("description");
        System.out.println(versionBanner());
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

    public List<Integer> getSTARBOUND_VERSION() {
        return STARBOUND_VERSION;
    }

    public List<String> getLANGUAGES() {
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
                + "             StarNub " + PHASE                   + "\n"
                + "             Version " + VERSION                 + "\n"
                + "             Author: " + AUTHOR                   + "\n"
                + "             Contributors: " + CONTRIBUTORS       + "\n"
                + ""                                                   + "\n"
                + "             www.StarNub.org "                   + "\n"
                + ""                                                   + "\n"
                + "             Compatible Starbound Versions: " + STARBOUND_VERSION  + "\n"
                + "=======================================================\n";
    }

    @Override
    public String toString() {
        return "StarNubVersion{" +
                "PHASE='" + PHASE + '\'' +
                ", VERSION=" + VERSION +
                ", PHASE_VERSION='" + PHASE_VERSION + '\'' +
                ", STARBOUND_VERSION=" + STARBOUND_VERSION +
                ", LANGUAGES=" + LANGUAGES +
                ", SIZE_MBS=" + SIZE_MBS +
                ", DESCRIPTION='" + DESCRIPTION + '\'' +
                '}';
    }
}
