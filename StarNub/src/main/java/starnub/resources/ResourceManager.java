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

package starnub.resources;

import utilities.dircectories.DirectoryCheckCreate;
import utilities.file.yaml.YAMLWrapper;

/**
 * Represents StarNubs ResourceManager Singleton
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ResourceManager {

    private static final ResourceManager instance = new ResourceManager();
    private static YAMLWrapper STARNUB_RESOURCES;

    private ResourceManager() {
        directoryCheck();
        try {
            STARNUB_RESOURCES = new YAMLWrapper("StarNub", "resources.yml", "resources/", "StarNub/Databases", false, false, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResourceManager getInstance() {
        return instance;
    }

    public YAMLWrapper getStarnubResources() {
        return STARNUB_RESOURCES;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will check to make sure StarNub and SubDirectories
     * are created or exist.
     * <p>
     * Notes: If a directory cannot be created the program will exit.
     */
    protected void directoryCheck() {
        DirectoryCheckCreate directories = new DirectoryCheckCreate(
                "StarNub",
//                "Resources",
                "Plugins",
                "Logs",
                "Logs/Events_Debug",
                "Logs/Chat",
                "Logs/Commands",
                "Logs/Information_Warning",
                "Logs/Error",
                "Databases");
        for (String directory : directories.getResults().keySet()) {
            if (directories.getResults().get(directory)) {
                System.out.println("Directory " + directory + " exist or was successfully created.");
            } else {
                System.err.println("ERROR CREATING DIRECTORY \"" + directory + "\" PLEASE CHECK FILE PERMISSIONS. " +
                        "VISIT \"WWW.STARNUB.ORG\" FOR FURTHER HELP... EXITING STARNUB.");
                System.exit(0);
            }
        }
    }
}