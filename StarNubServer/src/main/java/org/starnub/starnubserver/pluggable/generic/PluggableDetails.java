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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class PluggableDetails {

    private final String NAME;
    private final String CLASS;
    private final double VERSION;
    private final double SIZE_KBS;
    private final String AUTHOR;
    private final String URL;
    private final String DESCRIPTION;
    private final HashSet<String> DEPENDENCIES;

    /**
     * @param VERSION                double the pluggableOLD version
     * @param SIZE_KBS               double size of the pluggableOLD
     * @param AUTHOR                 String the authors name
     * @param URL                    String the url for the plugins page, download, help, etc
     * @param DESCRIPTION            String a short description of the pluggableOLD
     */
    public PluggableDetails(String NAME, String CLASS, double VERSION, double SIZE_KBS, String AUTHOR, String URL, String DESCRIPTION, ArrayList<String> DEPENDENCIES) {
        this.NAME = NAME;
        this.CLASS = CLASS;
        this.VERSION = VERSION;
        this.SIZE_KBS = SIZE_KBS;
        this.AUTHOR = AUTHOR;
        this.URL = URL;
        this.DESCRIPTION = DESCRIPTION;
        if (DEPENDENCIES != null) {
            this.DEPENDENCIES = new HashSet<>();
            this.DEPENDENCIES.addAll(DEPENDENCIES);
        } else {
            this.DEPENDENCIES = null;
        }
    }

    public String getNAME() {
        return NAME;
    }

    public String getCLASS() {
        return CLASS;
    }

    public double getVERSION() {
        return VERSION;
    }

    public double getSIZE_KBS() {
        return SIZE_KBS;
    }

    public String getAUTHOR() {
        return AUTHOR;
    }

    public String getURL() {
        return URL;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public HashSet<String> getDEPENDENCIES() {
        return DEPENDENCIES;
    }

    public LinkedHashMap<String, Object> getPluginDetailsMap(){
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("Name", NAME);
        linkedHashMap.put("Class", CLASS);
        linkedHashMap.put("Version", VERSION);
        linkedHashMap.put("Size KBs", SIZE_KBS);
        linkedHashMap.put("Author", AUTHOR);
        linkedHashMap.put("URL", URL);
        linkedHashMap.put("Description", DESCRIPTION);
        linkedHashMap.put("Dependencies", DEPENDENCIES);
        return linkedHashMap;
    }

    @Override
    public String toString() {
        return "PluggableDetails{" +
                "NAME='" + NAME + '\'' +
                ", CLASS='" + CLASS + '\'' +
                ", VERSION=" + VERSION +
                ", SIZE_KBS=" + SIZE_KBS +
                ", AUTHOR='" + AUTHOR + '\'' +
                ", URL='" + URL + '\'' +
                ", DESCRIPTION='" + DESCRIPTION + '\'' +
                ", DEPENDENCIES=" + DEPENDENCIES +
                '}';
    }
}
