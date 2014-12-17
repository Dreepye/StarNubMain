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

package starnubserver.plugins.generic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class PluginDetails {

    private final double VERSION;
    private final double SIZE_KBS;
    private final HashSet<String> DEPENDENCIES;
    private final HashSet<String> LANGUAGE;
    private final String AUTHOR;
    private final String URL;
    private final HashSet<String> ADDITIONAL_PERMISSIONS;
    private final String DESCRIPTION;

    /**
     * @param VERSION                double the plugin version
     * @param SIZE_KBS               double size of the plugin
     * @param DEPENDENCIES           HashSet a comma separated list of dependencies
     * @param LANGUAGE               String what languages does the plugin support
     * @param AUTHOR                 String the authors name
     * @param URL                    String the url for the plugins page, download, help, etc
     * @param ADDITIONAL_PERMISSIONS HashSet containing additional permission not used in commands
     * @param DESCRIPTION            String a short description of the plugin
     */
    public PluginDetails(double VERSION, double SIZE_KBS, HashSet<String> DEPENDENCIES, HashSet<String> LANGUAGE, String AUTHOR, String URL, HashSet<String> ADDITIONAL_PERMISSIONS, String DESCRIPTION) {
        this.VERSION = VERSION;
        this.SIZE_KBS = SIZE_KBS;
        this.DEPENDENCIES = DEPENDENCIES;
        this.LANGUAGE = LANGUAGE;
        this.AUTHOR = AUTHOR;
        this.URL = URL;
        this.ADDITIONAL_PERMISSIONS = ADDITIONAL_PERMISSIONS;
        this.DESCRIPTION = DESCRIPTION;
    }

    public double getVERSION() {
        return VERSION;
    }

    public double getSIZE_KBS() {
        return SIZE_KBS;
    }

    public HashSet<String> getDEPENDENCIES() {
        return DEPENDENCIES;
    }

    public HashSet<String> getLANGUAGE() {
        return LANGUAGE;
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

    public LinkedHashMap<String, Object> getPluginDetailsMap(){
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("Version", VERSION);
        linkedHashMap.put("Size KBs", SIZE_KBS);
        ArrayList<String> dependancyList = new ArrayList<>(DEPENDENCIES);
        linkedHashMap.put("Dependencies", dependancyList);
        ArrayList<String> languageList = new ArrayList<>(LANGUAGE);
        linkedHashMap.put("Language", languageList);
        linkedHashMap.put("Author", AUTHOR);
        linkedHashMap.put("URL", URL);
        ArrayList<String> additionalPermissionList = new ArrayList<>(ADDITIONAL_PERMISSIONS);
        linkedHashMap.put("Additional Permissions", additionalPermissionList);
        linkedHashMap.put("Description", DESCRIPTION);
        return linkedHashMap;
    }
}
