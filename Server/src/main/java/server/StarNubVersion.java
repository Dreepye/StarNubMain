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

package server;

import lombok.Getter;
import org.codehome.utilities.files.YamlLoader;

import java.util.ArrayList;
import java.util.Map;

/**
 * Represents this StarNub's version information. This class is a singleton.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class StarNubVersion {

    @Getter
    private static final StarNubVersion instance = new StarNubVersion();

    private StarNubVersion(){}

    /**
     * String of the current phase (alpha, beta, full release)
     */
    @Getter
    private String PHASE;

    /**
     * double of the current version (X.X)
     */
    @Getter
    private double VERSION;

    /**
     * String of the current phase and version combined
     */
    @Getter
    private String PHASE_VERSION;

    /**
     * String of the current phase and version combined
     */
    @Getter
    private ArrayList<String> LANGUAGES;

    /**
     * double the current the file size
     */
    @Getter
    private double SIZE_MBS;

    /**
     * String of the current information about the release
     */
    @Getter
    private String DESCRIPTION;

    {
        setVersionInstance();
        System.out.println(versionBanner());
    }
    /**
     * These items are generated from versions.yml within
     * the classpath / or resources folder.
     *
     * @param PHASE         String of the current phase (alpha, beta, full release)
     * @param VERSION       double of the current version (X.X)
     * @param PHASE_VERSION String of the current phase and version combined
     * @param LANGUAGES     ArrayList of the current support languages
     * @param SIZE_MBS      double the current the file size
     * @param DESCRIPTION   String of the current information about the release
     */
    protected void setStarNubVersion(String PHASE, double VERSION, String PHASE_VERSION, ArrayList<String> LANGUAGES, double SIZE_MBS, String DESCRIPTION) {
        this.PHASE = PHASE;
        this.VERSION = VERSION;
        this.PHASE_VERSION = PHASE_VERSION;
        this.LANGUAGES = LANGUAGES;
        this.SIZE_MBS = SIZE_MBS;
        this.DESCRIPTION = DESCRIPTION;
    }

    /**
     * This method will set the StarNubVersion
     * variables. These items are generated from
     * versions.yml within the classpath / or
     * resources folder.
     */
    @SuppressWarnings("unchecked")
    protected synchronized void setVersionInstance() {
        Map<String, Object> data = new YamlLoader().resourceYamlLoader("servers/version.yml");
        this.PHASE = (String) data.get("phase");
        this.VERSION = (double) data.get("version");
        this.PHASE_VERSION = PHASE + "-" + Double.toString(VERSION);
        this.LANGUAGES = (ArrayList<String>) data.get("languages");
        this.SIZE_MBS = (double) data.get("size");
        this.DESCRIPTION = (String) data.get("description");
    }

    public String versionBanner(){
        return    "\n"
                + "=======================================================\n"
                + "                   StarNub " + getPHASE() + "\n"
                + "                   Version " + getVERSION() + "\n"
                + "=======================================================\n";
    }
}
