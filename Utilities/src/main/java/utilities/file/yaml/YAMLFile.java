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

package utilities.file.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Represents a YAMLFile. This provides methods for creating, accessing, dumping
 * file data.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class YAMLFile {

    private final String OWNER;
    private final String FILE_NAME;
    private final Object DEFAULT_FILE_PATH;
    private final String DISK_FILE_PATH;
    private final File DISK_FILE;
    private final YAMLDumper YAML_DUMPER;

    /**
     * This will construct a YAML file, YAML dumper, YAML auto dumper
     *
     * @param OWNER                String owner of this YAMLFile
     * @param FILE_NAME            String file name of the file
     * @param DEFAULT_FILE_PATH    Object default path to the file (String, InputString, Map)
     * @param absolutePath           boolean is this an absolute path (true) (Absolute as in C:/ or /), (false) Folder/
     * @param DISK_FILE_PATH       String default path to file on the disk
     * @param DUMP_ON_MODIFICATION boolean are we dumping on modification
     */
    public YAMLFile(String OWNER, String FILE_NAME, Object DEFAULT_FILE_PATH, String DISK_FILE_PATH, boolean absolutePath, boolean DUMP_ON_MODIFICATION) {
        this.OWNER = OWNER;
        this.FILE_NAME = FILE_NAME;
        if (DEFAULT_FILE_PATH instanceof String) {
            this.DEFAULT_FILE_PATH = buildPath((String) DEFAULT_FILE_PATH, FILE_NAME, absolutePath);
        } else {
            this.DEFAULT_FILE_PATH = DEFAULT_FILE_PATH;
        }
        this.DISK_FILE_PATH = buildPath(DISK_FILE_PATH, FILE_NAME, absolutePath);
        this.DISK_FILE = new File(this.DISK_FILE_PATH);
        this.YAML_DUMPER = new YAMLDumper(null, DUMP_ON_MODIFICATION);
    }

    /**
     * @param OWNER                                      String owner of this YAMLFile
     * @param FILE_NAME                                  String file name of the file
     * @param DEFAULT_FILE_PATH                          Object default path to the file (String, InputString, Map)
     * @param DISK_FILE_PATH                             String default path to file on the disk
     * @param absolutePath           boolean is this an absolute path (true) (Absolute as in C:/ or /), (false) Folder/
     * @param AUTO_DUMP_INTERVAL                         int the auto dump interval in minutes
     * @param DUMP_ON_MODIFICATION                       boolean are we dumping on modification
     * @param AUTO_DUMPER_SCHEDULED_THREAD_POOL_EXECUTOR ScheduledThreadPoolExecutor representing where to submit the auto dump task to
     * @param map                                        Map representing the map to auto dump
     * @throws Exception
     */
    public YAMLFile(String OWNER, String FILE_NAME, Object DEFAULT_FILE_PATH, String DISK_FILE_PATH, boolean absolutePath, int AUTO_DUMP_INTERVAL, boolean DUMP_ON_MODIFICATION, ScheduledThreadPoolExecutor AUTO_DUMPER_SCHEDULED_THREAD_POOL_EXECUTOR, Map map) {
        this.OWNER = OWNER;
        this.FILE_NAME = FILE_NAME;
        if (DEFAULT_FILE_PATH instanceof String) {
            this.DEFAULT_FILE_PATH = buildPath((String) DEFAULT_FILE_PATH, FILE_NAME, absolutePath);
        } else {
            this.DEFAULT_FILE_PATH = DEFAULT_FILE_PATH;
        }
        this.DISK_FILE_PATH = buildPath(DISK_FILE_PATH, FILE_NAME, absolutePath);
        this.DISK_FILE = new File(this.DISK_FILE_PATH);
        YAMLAutoDump yamlAutoDump = null;
        if (AUTO_DUMPER_SCHEDULED_THREAD_POOL_EXECUTOR != null) {
            yamlAutoDump = new YAMLAutoDump(true, AUTO_DUMP_INTERVAL, AUTO_DUMPER_SCHEDULED_THREAD_POOL_EXECUTOR);
            yamlAutoDump.scheduleAutoDumping(this, map);
        }
        YAML_DUMPER = new YAMLDumper(yamlAutoDump, DUMP_ON_MODIFICATION);
    }

    public String getOWNER() {
        return OWNER;
    }

    public String getFILE_NAME() {
        return FILE_NAME;
    }

    public Object getDEFAULT_FILE_PATH() {
        return DEFAULT_FILE_PATH;
    }

    public String getDISK_FILE_PATH() {
        return DISK_FILE_PATH;
    }

    public File getDISK_FILE() {
        return DISK_FILE;
    }

    public YAMLDumper getYAML_DUMPER() {
        return YAML_DUMPER;
    }

    /**
     * This insure the file filePath is correct
     *
     * @param filePath String filePath to file
     * @param file String the file
     * @return String full file filePath
     */
    @SuppressWarnings("unchecked")
    private String buildPath(String filePath, String file, boolean absolutePath){
        File dir = new File(filePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        if (!(filePath).startsWith("/") && absolutePath) {

            filePath = "/" + filePath;
        }
        if (!(filePath).endsWith("/") && filePath.length() > 0){
            filePath = filePath + "/";
        }
        return filePath + file;
    }

    /**
     * This will load the file from disk on construction, but if it does not exist it will load the default file
     * <p>
     *
     * @throws Exception if the file cannot be loaded
     */
    protected Map<String, Object> loadOnConstruct() throws Exception {
        Map<String, Object> DATA;
        try {
            DATA = loadFromDisk();
            if (DATA != null) {
                return DATA;
            }
        } catch (FileNotFoundException e) {
            /* Silent Catch */
        }
        DATA = loadFromDefault();
        if (DATA == null) {
            throw new NullPointerException();
        }
        dumpToFile(DATA);
        return DATA;
    }

    /**
     * This method will load a YAML file from the file path set when this class was constructed.
     * <p>
     *
     * @return boolean returns true if the HashMap containing the file data is empty
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> loadFromDisk() throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(DISK_FILE)) {
            return (Map<String, Object>) new Yaml().load(fileInputStream);
        }
    }

    /**
     * This method will load a YAML file from the default path set when this class was constructed. Typically
     * this is a resource file inside of a jar or a resource input stream. File paths can include "/" or none for
     * Jar Resources.
     * <p>
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> loadFromDefault() throws Exception {
        if (DEFAULT_FILE_PATH instanceof String) {
            String defaultPath = (String) DEFAULT_FILE_PATH;
            try (InputStream resourceAsStream = this.getClass().getResourceAsStream(defaultPath)) {
                return (Map<String, Object>) new Yaml().load(resourceAsStream);
            }
        } else if (DEFAULT_FILE_PATH instanceof InputStream) {
            return (Map<String, Object>) new Yaml().load((InputStream) DEFAULT_FILE_PATH);
        } else if (DEFAULT_FILE_PATH instanceof Map){
            return (Map<String, Object>) DEFAULT_FILE_PATH;
        }
        return null;
    }

    /**
     * This method will dump on modification.
     */
    protected void dumpOnModification(Map map) throws IOException {
        if (YAML_DUMPER.isDUMP_ON_MODIFICATION()) {
            dumpToFile(map);
        }
    }

    /**
     * This method will dump(save) a file to disk and return true if the file exist
     * <p>
     *
     * @return boolean returns true if the HashMap containing the file data is empty
     */
    public boolean dumpToFile(Map map) throws IOException {
        try (Writer writer = new FileWriter(DISK_FILE_PATH)) {
            new Yaml(YAML_DUMPER.getDUMPER_OPTIONS()).dump(map, writer);
        }
        return DISK_FILE.exists();
    }

    /**
     * This method print this YAMLWrappers YAML string to screen
     */
    public void printToConsole(Map map) {
        System.out.println(new Yaml(YAML_DUMPER.getDUMPER_OPTIONS()).dump(map));
    }

    /**
     * This method will return YAMLWrappers YAML string
     * <p>
     *
     * @return String representing a YAML String
     */
    public String getYAMLString(Map map) {
        return new Yaml(YAML_DUMPER.getDUMPER_OPTIONS()).dump(map);
    }
}
