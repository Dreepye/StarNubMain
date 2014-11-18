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
import org.codehome.utilities.directories.DirectoryCheckCreate;
import org.codehome.utilities.files.JarResourceToDisk;
import org.codehome.utilities.files.YamlLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * Enum Singleton representing StarNub's resource manager.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class ResourceManager {

    @Getter
    private static final ResourceManager instance = new ResourceManager();

    private ResourceManager(){
        directoryCheck();
        resourceExtraction();
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
                System.out.println("Directory "+directory+" exist or was successfully created.");
            } else {
                System.err.println("ERROR CREATING DIRECTORY \""+directory+"\" PLEASE CHECK FILE PERMISSIONS. " +
                        "VISIT \"WWW.STARNUB.ORG\" FOR FURTHER HELP... EXITING STARNUB.");
                System.exit(0);
            }
        }
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will extract resources from StarNub if they do not exist on
     * disk. If a resource cannot be extracted the program may not work correctly.
     * <p>
     */
    @SuppressWarnings("unchecked")
    protected void resourceExtraction() {
        File snConf = new File("StarNub/starnub_config.yml");
        if (!snConf.exists()) {
            fileExtraction("servers/starnub.fileextracts/default_configuration.yml", "StarNub/starnub_config.yml", false);
            System.out.println("You did not have a StarNub configuration present on disk, extracting the " +
                    "default configuration. StarNub will now exit and allow you to check the configuration at" +
                    "\"StarNub/starnub_config.yml\"");
            System.exit(0);
        }
        Map<String, Object> configurationValues = new YamlLoader().resourceYamlLoader("servers/resources.yml");
        Map<String, Object> blankFiles = (Map) configurationValues.get("blank_files");
        for (Object value : blankFiles.values()){
            fileCreate(new File((String) value));
        }
        Map<String, Object> nonBlankFiles = (Map) configurationValues.get("non_blank_files");
        for (String key : nonBlankFiles.keySet()){
            fileExtraction(key, (String) nonBlankFiles.get(key), false);
        }
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will create a file in the directory that you want.
     * <p>
     */
    private boolean fileCreate(File file){
        if (!file.exists()) {
            try {
               return file.createNewFile();
            } catch (IOException e) {
                System.err.println("StarNub was unable to create a file used in the program. File: \"" + file.toString() + "\".");
            }
        }
        return false;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will extract resources from StarNub if they do not exist on
     * disk, optionally the resource can be overridden. If a resource cannot be
     * extracted the program may not work correctly.
     * <p>
     */
    private void fileExtraction (String resourcePath, String diskFilePath, boolean overwriteFile){
        JarResourceToDisk fileExtractor = new JarResourceToDisk();
        try {
            fileExtractor.fileUnpack(resourcePath, diskFilePath, overwriteFile);
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                System.err.println();
            } else {
                System.err.println();
            }
        }
    }
}