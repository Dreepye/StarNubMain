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

package utilities.yaml;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Represents a YAMLWrapper. The YAML Wrapper is constructed and then contains methods for manipulating
 * YAML files. This class will load, dump(save), add and remove data from the YAML. If you do not specify
 * DUMP_ON_MODIFICATION you will have to manually dump(save) the file to disk.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class YAMLWrapper extends YAMLFile{

    private final Object HASHMAP_LOCK_OBJECT = new Object();
    private HashMap<String, Object> DATA;

    /**
     * This will construct a YAML file, YAML dumper, YAML auto dumper
     *
     * @param OWNER String owner of this YAMLFile
     * @param FILE_NAME String file name of the file
     * @param DEFAULT_FILE_PATH String default path to the file
     * @param DISK_FILE_PATH String default path to file on the disk
     * @param DUMP_ON_MODIFICATION boolean are we dumping on modification
     * @param loadOnConstruct boolean load the file on construction of this wrapper
     * @param validateOnConstruction boolean validate the Map against the Default Map on construction
     * @throws Exception
     */
    public YAMLWrapper(String OWNER, String FILE_NAME, Object DEFAULT_FILE_PATH, String DISK_FILE_PATH, boolean DUMP_ON_MODIFICATION, boolean loadOnConstruct, boolean validateOnConstruction) throws Exception {
        super(OWNER, FILE_NAME, DEFAULT_FILE_PATH, DISK_FILE_PATH, DUMP_ON_MODIFICATION);
        if (loadOnConstruct) {
            DATA = loadOnConstruct();
        }
        if (validateOnConstruction){
            mapVerifyInternally();
        }
    }

    /**
     * @param OWNER String owner of this YAMLFile
     * @param FILE_NAME String file name of the file
     * @param DEFAULT_FILE_PATH String default path to the file
     * @param DISK_FILE_PATH String default path to file on the disk
     * @param AUTO_DUMP_INTERVAL int the auto dump interval in minutes
     * @param DUMP_ON_MODIFICATION boolean are we dumping on modification
     * @param loadOnConstruct boolean load the file on construction of this wrapper
     * @param validateOnConstruction boolean validate the Map against the Default Map on construction
     * @param AUTO_DUMPER_SCHEDULED_THREAD_POOL_EXECUTOR ScheduledThreadPoolExecutor representing where to submit the auto dump task to
     * @param map Map representing the map to auto dump
     * @throws Exception
     */
    public YAMLWrapper(String OWNER, String FILE_NAME, Object DEFAULT_FILE_PATH, String DISK_FILE_PATH, int AUTO_DUMP_INTERVAL, boolean DUMP_ON_MODIFICATION, boolean loadOnConstruct, boolean validateOnConstruction, ScheduledThreadPoolExecutor AUTO_DUMPER_SCHEDULED_THREAD_POOL_EXECUTOR, Map map) throws Exception {
        super(OWNER, FILE_NAME, DEFAULT_FILE_PATH, DISK_FILE_PATH, AUTO_DUMP_INTERVAL, DUMP_ON_MODIFICATION, AUTO_DUMPER_SCHEDULED_THREAD_POOL_EXECUTOR, map);
        if (loadOnConstruct) {
            DATA = loadOnConstruct();
        }
        if (validateOnConstruction){
            mapVerifyInternally();
        }
    }

    public HashMap<String, Object> getDATA() {
        return DATA;
    }

    /**
     * This will make sure the Map set here is valid
     */
    public void mapVerifyInternally()throws Exception{
        DATA = mapVerify(DATA, loadFromDefault());
        super.dumpOnModification(DATA);
    }

    /**
     * This method will check a Map against a Default Map and remove keys from the Map that are not
     * in the Default Map, thus purging old keys and values. It then checks the Default Map Key:Values
     * and insure they exist inside of the Map. Then this method will make sure that the Data Types of the
     * Map match the Default Map data types. If the a List exist then the quantity is trivial and only
     * the data types are checked.
     * <p>
     *  @param mapToVerify Map the map to be verified
     * @param defaultMap Map the default map to be used as the verification map
     */
    public static HashMap<String, Object> mapVerify(HashMap<String, Object> mapToVerify, HashMap<String, Object> defaultMap){
        mapToVerify = mapPurge(mapToVerify, defaultMap);
        for (String s : defaultMap.keySet()){
            Object objectToVerify = mapToVerify.get(s);
            Object defaultObject = defaultMap.get(s);
            if(objectToVerify == null){
                mapToVerify.put(s, defaultObject);
            } else if (defaultObject instanceof Map){
                if (objectToVerify instanceof Map){
                    mapVerify((HashMap<String, Object>) objectToVerify, (HashMap<String, Object>) defaultObject);
                } else {
                    mapToVerify.put(s, defaultObject);
                }
            } else if (defaultObject instanceof List){
                if (objectToVerify instanceof List){
                    listVerify((List) objectToVerify, (List) defaultObject);
                } else {
                    mapToVerify.put(s, defaultObject);
                }
            } else {
                mapToVerify.put(s, allElseVerify(objectToVerify, defaultObject));
            }
        }
        return mapToVerify;
    }

    /**
     * This method will take a Map and a Default Map and remove any keys from the Map of which are not in
     * the Default Map. This method will iterate through all of the inner maps and remove keys that do not
     * exist.
     * <p>
     *
     * @param mapToVerify Map the map that is to be purged
     * @param defaultMap Map the map that is to be used as the basis for this purge method
     * @return Map returns the purged map
     */
    @SuppressWarnings("unchecked")
    private static HashMap<String, Object> mapPurge(HashMap<String, Object> mapToVerify, HashMap<String, Object> defaultMap) {
        HashSet<String> keysToRemove = new HashSet<>();
        for (String s : mapToVerify.keySet()){
            if(!(defaultMap.containsKey(s))){
                keysToRemove.add(s);
            } else {
                Object objectToVerify = mapToVerify.get(s);
                Object defaultObject = defaultMap.get(s);
                if (objectToVerify instanceof Map && defaultObject instanceof Map) {
                    mapPurge((HashMap<String, Object>) objectToVerify, ((HashMap<String, Object>) defaultObject));
                } else if (!(objectToVerify instanceof Map) && defaultObject instanceof Map) {
                    mapToVerify.put(s, defaultObject);
                }
            }
        }
        keysToRemove.forEach(mapToVerify::remove);
        return mapToVerify;
    }

    /**
     * This will iterate through a list and insure that all of the list elements are
     * the Class type supplied. If the list elements are not the same class type as
     * the supplied class type, they will be removed.
     * <p>
     *
     * @param listToVerify List the list to be checked for element type
     * @param defaultList List the default list to use in checking the list element types
     */
    public static void listVerify(List listToVerify, List defaultList) {
        try {
            Object defaultListObject = defaultList.get(0);
            Class<?> aClass = defaultListObject.getClass();
            Iterator it = listToVerify.iterator();
            while(it.hasNext()){
                Object elementToCheck = it.next();
                if (!elementToCheck.getClass().equals(aClass)){
                    it.remove();
                }
            }
        } catch (IndexOutOfBoundsException e){
            /* Silent Catch */
        }
    }

    /**
     * This method will check the Object to be verified against the default object and insure they are the same
     * if they are not the same then it will try to correct the data type.
     * <p>
     *
     * @param objectToVerify Object representing the object type to be checked
     * @param defaultObject Object representing the default object type to be used
     * @return Object return the correct object
     */
    private static Object allElseVerify(Object objectToVerify, Object defaultObject) {
        if (defaultObject instanceof Boolean) {
            if(!(objectToVerify instanceof Boolean)){
                if (objectToVerify instanceof String) {
                    return ((String) objectToVerify).toLowerCase().contains("yes") || !((String) objectToVerify).toLowerCase().contains("no") && Boolean.parseBoolean(objectToVerify.toString());
                }
            }
        } else if (defaultObject instanceof Float) {
            if (!(objectToVerify instanceof Float)){
                return Float.parseFloat(objectToVerify.toString());
            }
        } else if (defaultObject instanceof Integer) {
            if (!(objectToVerify instanceof Integer)) {
                try {
                    return Integer.parseInt(objectToVerify.toString());
                } catch (NumberFormatException e) {
                    return defaultObject;
                }
            }
        } else if (defaultObject instanceof String) {
            if (objectToVerify instanceof Map || objectToVerify instanceof List){
                return defaultObject;
            } else if (!(objectToVerify instanceof String)){
                return objectToVerify.toString();
            }
        } else {
            return defaultObject;
        }
        return objectToVerify;
    }

    /**
     * This will insert this value and key into the data and dump to disk if dumpOnModification
     * is set
     *
     * @param key String the key to insert into the map
     * @param value Object the value to be mapped to the key
     * @return boolean if the value was successfully added
     * @throws java.io.IOException
     */
    public boolean addKeyValue(String key, Object value) throws IOException {
        synchronized (HASHMAP_LOCK_OBJECT){
            DATA.put(key, value);
        }
        dumpOnModification(DATA);
        return DATA.containsValue(value);
    }

    /**
     * This will insert this value and key into the data and dump to disk if dumpOnModification
     * is set
     *
     * @param key String the key of the value to be replaced
     * @param value Object the value to be mapped to the key
     * @return boolean if the value was successfully added
     * @throws java.io.IOException
     */
    public boolean replaceValue(String key, Object value) throws IOException {
        return addKeyValue(key, value);
    }

    /**
     * This method will take a value and then an array of keys and insert the value into a set of nested maps.
     * <p>
     * Example: addNestedValue(true, "Key1", "Key2", Key3")
     * Creates:
     * Key1:
     *   Key2: {
     *      Key3: true
     *   }
     *
     * @param value Object representing the value you want to insert into a nest of maps
     * @param keys String... list of keys to add this value you too
     * @return boolean representing if the value was added
     * @throws java.io.IOException
     */
    public boolean addNestedValue(Object value, String... keys) throws IOException {
        int index = 0;
        int indexLength = keys.length - 1;
        HashMap<String, Object> tempMap = DATA;
        Object o;
        synchronized (HASHMAP_LOCK_OBJECT) {
            while (index < indexLength) {
                String key = keys[index];
                o = tempMap.get(key);
                if (o == null || !(o instanceof Map)) {
                    tempMap.put(key, new HashMap<>());
                } else {
                    tempMap = (HashMap<String, Object>) o;
                    index++;
                }
            }
            tempMap.put(keys[index], value);
        }
        dumpOnModification(DATA);
        return hasValue(value, keys);
    }

    /**
     * This method will take a value and then an array of keys and insert the value into a set of nested maps.
     * <p>
     * Example: replaceNestedValue(false, "Key1", "Key2", Key3")
     * Changes:
     * Key1:
     *   Key2: {
     *      Key3: true
     *   }
     *
     * To:
     * Key1:
     *   Key2: {
     *      Key3: false
     *   }
     *
     * @param value Object representing the value you want to insert into a nest of maps
     * @param keys String... list of keys to add this value you too
     * @return boolean representing if the value was added
     * @throws java.io.IOException
     */
    public boolean replaceNestedValue(Object value, String... keys) throws IOException {
       return addNestedValue(value, keys);
    }










    /**
     * This will return if Data has a value in the base Map
     *
     * @param value Object value to be checked for
     * @return boolean representing if the value exist
     */
    public boolean hasValue(Object value){
        return DATA.containsValue(value);
    }

    /**
     * This will return if Data and the provided keys contain the value
     *
     * Example: hasValue("1", "Key1", "Key2", Key3")
     * Would return true if the Map is as such:
     * Key1:
     *   Key2: {
     *      Key3: "1"
     *   }
     *
     * @param value Object value to be checked for
     * @param keys String... representing the nested keys to get
     * @return boolean representing if the value exist
     */
    public boolean hasValue(Object value, String... keys){
        return value.equals(mapUnwrapper(keys));
    }

    /**
     *
     * @param keys String... representing a list of the keys to retrieve an Object from
     * @return Object the object to be returned
     */
    private Object mapUnwrapper(String... keys){
        HashMap<String, Object> tempHashMap = null;
        int index = 0;
        int keysLastIndex = keys.length-1;
        boolean firstLoop = true;
        for (String key : keys){
            synchronized (HASHMAP_LOCK_OBJECT) {
                if (index < keysLastIndex) {
                    if(firstLoop) {
                        firstLoop = false;
                        tempHashMap = (HashMap<String, Object>) DATA.get(keys[index]);
                    } else {
                        tempHashMap = (HashMap<String, Object>) tempHashMap.get(keys[index]);
                    }
                } else {
                    if (tempHashMap != null) {
                        return tempHashMap.get(keys[index]);
                    }
                }
            }
            index++;
        }
        return null;
    }



    //iterate list for get remove contains (Synrconized) Single and list merging





}


