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

package starboundmanager;

import org.apache.commons.io.FileUtils;
import utilities.concurrency.thread.ThreadSleep;
import utilities.file.simplejson.JSONObject;
import utilities.file.simplejson.JSONPrettyPrint;
import utilities.file.simplejson.parser.JSONParser;
import utilities.file.simplejson.parser.ParseException;

import java.io.*;
import java.util.Map;

/**
 * Represents StarboundConfiguration will configure the starbound configuration for you
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class StarboundConfiguration {

    private final StarboundManagement STARBOUND_MANAGEMENT;
    private final String STARBOUND_CONFIGURATION;
    private final String STARBOUND_CONFIGURATION_BACKUP;
    private final File STARBOUND_CONFIGURATION_FILE;
    private final File STARBOUND_CONFIGURATION_FILE_BACKUP;

    /**
     * Recommended: For internal use.
     * <p>
     * Uses: This will return the Starbound Status and can be casted into (Running, Starting, Stopped, Stopping, Unresponsive) and used
     * as a per status bases
     *
     * @param STARBOUND_MANAGEMENT StarboundManagement representing the class that constructed this instance
     * @param STARBOUND_CONFIGURATION String representing the name of the starbound configuration
     * @throws FileNotFoundException throws this exception if the file does not exist
     */
    public StarboundConfiguration(StarboundManagement STARBOUND_MANAGEMENT, String STARBOUND_CONFIGURATION) throws FileNotFoundException {
        this.STARBOUND_MANAGEMENT = STARBOUND_MANAGEMENT;
        this.STARBOUND_CONFIGURATION = STARBOUND_CONFIGURATION;
        this.STARBOUND_CONFIGURATION_BACKUP = STARBOUND_CONFIGURATION + ".bak";
        this.STARBOUND_CONFIGURATION_FILE = new File(STARBOUND_CONFIGURATION);
        if (!STARBOUND_CONFIGURATION_FILE.exists()){
            throw new FileNotFoundException();
        }
        this.STARBOUND_CONFIGURATION_FILE_BACKUP= new File(STARBOUND_CONFIGURATION_BACKUP);
    }

    public StarboundManagement getSTARBOUND_MANAGEMENT() {
        return STARBOUND_MANAGEMENT;
    }

    public String getSTARBOUND_CONFIGURATION() {
        return STARBOUND_CONFIGURATION;
    }

    public String getSTARBOUND_CONFIGURATION_BACKUP() {
        return STARBOUND_CONFIGURATION_BACKUP;
    }

    public File getSTARBOUND_CONFIGURATION_FILE() {
        return STARBOUND_CONFIGURATION_FILE;
    }

    public File getSTARBOUND_CONFIGURATION_FILE_BACKUP() {
        return STARBOUND_CONFIGURATION_FILE_BACKUP;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to generate a Starbound Configuration if one does not exist
     *
     * @throws IOException various exceptions can be thrown, IO or File related
     */
    public boolean generateConfiguration() throws IOException {
        boolean firstLoop = true;
        while (!STARBOUND_CONFIGURATION_FILE.exists()) {
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Configuration_Missing", STARBOUND_CONFIGURATION);
            if(firstLoop) {
                STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Configuration_Generating", STARBOUND_CONFIGURATION);
                STARBOUND_MANAGEMENT.setStarboundProcess(false, false);
                firstLoop = false;
            } else {
                STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Configuration_Generating_Still", STARBOUND_CONFIGURATION);
            }
            ThreadSleep.timerSeconds(5);
            if (STARBOUND_CONFIGURATION_FILE.exists()){
                STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Configuration_Generated", STARBOUND_CONFIGURATION);
                STARBOUND_MANAGEMENT.getStarboundProcess().PROCESS.destroy();
                return true;
            }
        }
        generateBackupConfiguration();
        return false;
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt to generate a Back up Starbound Configuration and will overwrite the old one
     *
     */
    public void generateBackupConfiguration(){
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Configuration_Backing_Up", STARBOUND_CONFIGURATION_BACKUP);
        try {
            FileUtils.copyFile(STARBOUND_CONFIGURATION_FILE, STARBOUND_CONFIGURATION_FILE_BACKUP);
        } catch (IOException e) {
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Configuration_Backing_Up_Failed", STARBOUND_CONFIGURATION_BACKUP);
        }
    }

    /**
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This will attempt configure starbound using a Map with String keys and Object Values you provided to
     * be updated in the starbound configuration
     *
     * @param configurationValues Map representing the string keys and objects to place in the Starbound Configuration
     * @throws IOException various exceptions can be thrown, IO or File related
     */
    public void configure(Map<String, Object> configurationValues) throws IOException, ParseException {
        STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Configuring_Starbound", STARBOUND_CONFIGURATION_BACKUP);
        generateBackupConfiguration();
        JSONParser parser = new JSONParser();
        FileReader fileRead = new FileReader(STARBOUND_CONFIGURATION);
        Object obj = parser.parse(fileRead);
        JSONObject jsonObject = (JSONObject) obj;
        for (Map.Entry<String, Object> mapEntry : configurationValues.entrySet()){
            jsonObject.put(mapEntry.getKey(), mapEntry.getValue());
        }
        try  (FileWriter fileWrite = new FileWriter(STARBOUND_CONFIGURATION)){
            fileWrite.write(JSONPrettyPrint.toJSONString(jsonObject));
            fileWrite.flush();
        } catch (IOException e){
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Configuring_Starbound_Failed_Saving", STARBOUND_CONFIGURATION_BACKUP);
            STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Configuring_Starbound_Restoring_Configuration", STARBOUND_CONFIGURATION_BACKUP);
            try {
                FileUtils.copyFile(STARBOUND_CONFIGURATION_FILE_BACKUP, STARBOUND_CONFIGURATION_FILE);
            } catch (IOException ex) {
                STARBOUND_MANAGEMENT.printOrEvent("Starbound_Status_Configuring_Starbound_Restoring_Configuration_Failed", STARBOUND_CONFIGURATION_BACKUP);
                System.exit(0);
            }
        }
    }

}





