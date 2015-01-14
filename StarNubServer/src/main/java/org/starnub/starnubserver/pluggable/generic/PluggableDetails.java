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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PluggableDetails {

    private final String OWNER;
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
     * @param DEPENDENCIES
     */
    public PluggableDetails(String OWNER, String NAME, String CLASS, double VERSION, double SIZE_KBS, String AUTHOR, String URL, String DESCRIPTION, List<String> DEPENDENCIES) {
        this.OWNER = OWNER;
        this.NAME = NAME;
        this.CLASS = CLASS;
        this.VERSION = VERSION;
        this.SIZE_KBS = SIZE_KBS;
        this.AUTHOR = AUTHOR;
        this.URL = URL;
        this.DESCRIPTION = DESCRIPTION;
        if (DEPENDENCIES != null) {
            this.DEPENDENCIES = new HashSet<>();
            this.DEPENDENCIES.addAll(DEPENDENCIES.stream().collect(Collectors.toList()));
        } else {
            this.DEPENDENCIES = null; /* Commands currently do not have dependencies, this will save space */
        }
    }

    public String getOWNER() {
        return OWNER;
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

    public LinkedHashMap<String, Object> getDetailsMap(){
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("Owner", OWNER);
        linkedHashMap.put("Name", NAME);
        linkedHashMap.put("Class", CLASS);
        linkedHashMap.put("Version", VERSION);
        linkedHashMap.put("Size KBs", SIZE_KBS);
        linkedHashMap.put("Author", AUTHOR);
        linkedHashMap.put("URL", URL);
        linkedHashMap.put("Description", DESCRIPTION);
        String dependenciesString = "";
        if(DEPENDENCIES == null){
            dependenciesString = "None";
        } else {
            dependenciesString = DEPENDENCIES.toString();
        }
        linkedHashMap.put("Dependencies", dependenciesString);
        return linkedHashMap;
    }

    @Override
    public String toString() {
        return "PluggableDetails{" +
                "OWNER='" + OWNER + '\'' +
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
