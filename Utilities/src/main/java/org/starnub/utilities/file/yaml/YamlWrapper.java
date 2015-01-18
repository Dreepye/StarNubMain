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

package org.starnub.utilities.file.yaml;

import org.starnub.utilities.cache.exceptions.CollectionDoesNotExistException;
import org.starnub.utilities.events.EventRouter;
import org.starnub.utilities.events.types.ObjectEvent;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Represents a YAMLWrapper. The YAML Wrapper is constructed and then contains methods for manipulating
 * YAML files. This class will load, dump(save), add and remove data from the YAML. If you do not specify
 * DUMP_ON_MODIFICATION you will have to manually dump(save) the file to disk.
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class YamlWrapper extends YamlFile {

    private final Object LOCK_OBJECT = new Object();
    private final ConcurrentHashMap<String, Object> DATA = new ConcurrentHashMap<>();

    /**
     * This will construct a YAML file, YAML dumper, YAML auto dumper
     *
     * Note: Absolute file paths are not support and can only be references as such "" - For base starbound directory or StarNub/ for starnub directory, ect...
     * Resource paths are references by "/" or /StarNub/, we will build out the full path using the file and path you supply.
     *
     * @param EVENT_ROUTER         EventRouter in which to notify events
     * @param OWNER                  String owner of this YAMLFile
     * @param FILE_NAME              String file name of the file
     * @param DEFAULT_FILE_PATH      Object default path to the file
     * @param DISK_FILE_PATH         String default path to file on the disk
     * @param defaultPathResource    boolean is this a resource or file path
     * @param DUMP_ON_MODIFICATION   boolean are we dumping on modification
     * @param loadOnConstruct        boolean load the file on construction of this wrapper
     * @param validateOnConstruction boolean validate the Map against the Default Map on construction
     * @param dumpToDisk             boolean representing if we are going to save the file to disk, this is usually if the file is only used internally
     */
    public YamlWrapper(EventRouter EVENT_ROUTER, String OWNER, String FILE_NAME, Object DEFAULT_FILE_PATH, String DISK_FILE_PATH, boolean defaultPathResource, boolean DUMP_ON_MODIFICATION, boolean loadOnConstruct, boolean validateOnConstruction, boolean dumpToDisk) {
        super(EVENT_ROUTER, OWNER, FILE_NAME, DEFAULT_FILE_PATH, DISK_FILE_PATH, defaultPathResource, DUMP_ON_MODIFICATION);
        try {
            if (loadOnConstruct) {
                DATA.putAll(loadOnConstruct(dumpToDisk));
            }
            if (validateOnConstruction && !isFirstLoad()) {
                mapVerifyInternally();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        getEVENT_ROUTER().eventNotifyNullCheck(new ObjectEvent("YAMLWrapper_Loaded_" + getOWNER() + "_" + getFILE_NAME(), this));
    }

    /**
     * This will construct a YAML file, YAML dumper, YAML auto dumper
     *
     * Note: This is for temp files
     *
     * Note: Absolute file paths are not support and can only be references as such "" - For base starbound directory or StarNub/ for starnub directory, ect...
     * Resource paths are references by "/" or /StarNub/, we will build out the full path using the file and path you supply.
     *
     * @param EVENT_ROUTER         EventRouter in which to notify events
     * @param OWNER                  String owner of this YAMLFile
     * @param FILE_NAME              String file name of the file
     * @param DEFAULT_FILE_PATH      Object default path to the file
     * @param DISK_FILE_PATH         String default path to file on the disk
     */
    public YamlWrapper(EventRouter EVENT_ROUTER, String OWNER, String FILE_NAME, Object DEFAULT_FILE_PATH, String DISK_FILE_PATH) {
        super(EVENT_ROUTER, OWNER, FILE_NAME, DEFAULT_FILE_PATH, DISK_FILE_PATH, true, false);
        try {
            DATA.putAll(loadOnConstruct(false));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Map<String, Object> getDATA() {
        return DATA;
    }

    public void reload() throws Exception {
        DATA.putAll(loadOnConstruct(false));
        mapVerifyInternally();
        getEVENT_ROUTER().eventNotifyNullCheck(new ObjectEvent("YAMLWrapper_Reloaded_" + getOWNER() + "_" + getFILE_NAME(), this));
    }

    /**
     * This will make sure the Map set here is valid
     */
    protected void mapVerifyInternally() throws Exception {
        Map<String, Object> stringObjectMap = loadFromDefault();
        DATA.putAll(mapVerify(DATA, stringObjectMap));
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
     * @param defaultMap  Map the default map to be used as the verification map*/
    @SuppressWarnings("unchecked")
    public static Map<String, Object> mapVerify(Map<String, Object> mapToVerify, Map<String, Object> defaultMap) {
        if (defaultMap == null){
            defaultMap = new HashMap<>();
            defaultMap.put("NO_DEFAULT_CONFIGURATION", "PLEASE HAVE PLUGIN DEVELOPER SET THE CONFIGURATION VALUE TO FALSE IN THE PLUGIN.YML.");
            return defaultMap;
        }
        mapToVerify = mapPurge(mapToVerify, defaultMap);
        for (Map.Entry <String, Object> entrySet : defaultMap.entrySet()) {
            String s = entrySet.getKey();
            Object objectToVerify = mapToVerify.get(s);
            Object defaultObject = entrySet.getValue();
            if (objectToVerify == null) {
                mapToVerify.put(s, defaultObject);
            } else if (defaultObject instanceof Map) {
                if (objectToVerify instanceof Map) {
                    mapVerify((Map<String, Object>) objectToVerify, (Map<String, Object>) defaultObject);
                } else {
                    mapToVerify.put(s, defaultObject);
                }
            } else if (defaultObject instanceof List) {
                if (objectToVerify instanceof List) {
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
     * @param defaultMap  Map the map that is to be used as the basis for this purge method
     * @return Map returns the purged map
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> mapPurge(Map<String, Object> mapToVerify, Map<String, Object> defaultMap) {
        HashSet<String> keysToRemove = new HashSet<>();
        for (Map.Entry <String, Object> entrySet : mapToVerify.entrySet()) {
            String s = entrySet.getKey();
            if (!(defaultMap.containsKey(s))) {
                keysToRemove.add(s);
            } else {
                Object objectToVerify = mapToVerify.get(s);
                Object defaultObject = entrySet.getValue();
                if (objectToVerify instanceof Map && defaultObject instanceof Map) {
                    mapPurge((Map<String, Object>) objectToVerify, ((Map<String, Object>) defaultObject));
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
     * @param defaultList  List the default list to use in checking the list element types
     */
    public static void listVerify(List listToVerify, List defaultList) {
        try {
            Object defaultListObject = defaultList.get(0);
            Class<?> aClass = defaultListObject.getClass();
            Iterator it = listToVerify.iterator();
            while (it.hasNext()) {
                Object elementToCheck = it.next();
                if (!elementToCheck.getClass().equals(aClass)) {
                    it.remove();
                }
            }
        } catch (IndexOutOfBoundsException e) {
            /* Silent Catch */
        }
    }

    /**
     * This method will check the Object to be verified against the default object and insure they are the same
     * if they are not the same then it will try to correct the data type.
     * <p>
     *
     * @param objectToVerify Object representing the object type to be checked
     * @param defaultObject  Object representing the default object type to be used
     * @return Object return the correct object
     */
    private static Object allElseVerify(Object objectToVerify, Object defaultObject) {
        if (defaultObject instanceof Boolean) {
            if (!(objectToVerify instanceof Boolean)) {
                if (objectToVerify instanceof String) {
                    return ((String) objectToVerify).toLowerCase().contains("yes") || !((String) objectToVerify).toLowerCase().contains("no") && Boolean.parseBoolean(objectToVerify.toString());
                } else {
                    return defaultObject;
                }
            }
        } else if (defaultObject instanceof Float) {
            if (!(objectToVerify instanceof Float)) {
                try {
                    return Float.parseFloat(objectToVerify.toString());
                } catch (NumberFormatException e) {
                    return defaultObject;
                }
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
            if (objectToVerify instanceof Map || objectToVerify instanceof List) {
                return defaultObject;
            } else if (!(objectToVerify instanceof String)) {
                return objectToVerify.toString();
            }
        } else {
            return defaultObject;
        }
        return objectToVerify;
    }

    /**
     * This method will reload a YAML file from the file path set when this class was constructed.
     * <p>
     *
     */
    @SuppressWarnings("unchecked")
    public void reloadFromDisk() throws Exception {
        DATA.putAll(mapVerify(super.loadFromDisk(), loadFromDefault()));
        super.dumpOnModification(DATA);
    }

    /**
     * This method will dump the YAMLWrapper Data Map to file
     *
     * @return boolean if the file exist
     * @throws java.io.IOException
     */
    public boolean dumpToFile() throws IOException {
        return super.dumpToFile(DATA);
    }

    /**
     * This method will print the YAMLWrapper Data Map to console
     */
    protected void printToConsole() {
        System.out.println(new Yaml(super.getYAML_DUMPER().getDUMPER_OPTIONS()).dump(DATA));
    }

    /**
     * This method will return YAMLWrapper Data Map string
     * <p>
     *
     * @return String representing a YAML String
     */
    protected String getYAMLString() {
        return new Yaml(super.getYAML_DUMPER().getDUMPER_OPTIONS()).dump(DATA);
    }

    /**
     * This method will return all of the keys at the base layer of the map
     *
     * @return Set of Strings containing all the keys
     */
    public Set<String> getAllKeys(){
        return DATA.keySet().stream().collect(Collectors.toSet());
    }

    /**
     * This methos will return if this YAMLWrapper contains a specified key
     *
     * @return boolean representing if the key is contained
     */
    public boolean hasKey(String key){
        return DATA.containsKey(key.toLowerCase());
    }

    /**
     * This will insert this value and key into the base data.
     * <p>
     * Example: addKeyValue(true, "Key1")
     * Creates:
     * {
     * Key1: true
     * }
     *
     * @param value Object the value to be mapped to the key
     * @param key   String the key to insert into the map
     * @return boolean if the value was successfully added
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean addKeyValue(Object value, String key) throws IOException {
        DATA.put(key, value);
        dumpOnModification(DATA);
        return DATA.containsValue(value);
    }

    /**
     * This will insert this value and key into the base data
     * <p>
     * Example: replaceValue(false, "Key1")
     * Creates:
     * {
     * Key1: true
     * }
     * <p>
     * To:
     * {
     * Key1: false
     * }
     *
     * @param value Object the value to be mapped to the key
     * @param key   String the key of the value to be replaced
     * @return boolean if the value was successfully added
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean replaceValue(Object value, String key) throws IOException {
        return addKeyValue(value, key);
    }

    /**
     * This method will take a value and then an array of keys and insert the value into a set of nested maps.
     * <p>
     * Example: addNestedValue(true, "Key1", "Key2", Key3")
     * Creates:
     * Key1:
     * Key2: {
     * Key3: true
     * }
     *
     * @param value Object representing the value you want to insert into a nest of maps
     * @param keys  String... list of keys to add this value you too
     * @return boolean representing if the value was added
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    @SuppressWarnings("unchecked")
    public boolean addNestedValue(Object value, String... keys) throws IOException {
        int index = 0;
        int indexLength = keys.length - 1;
        Map<String, Object> tempMap = DATA;
        Object o;
        while (index < indexLength) {
            String key = keys[index];
            o = tempMap.get(key);
            if (o == null || !(o instanceof Map)) {
                tempMap.put(key, new HashMap<>());
            } else {
                tempMap = (Map<String, Object>) o;
                index++;
            }
        }
        tempMap.put(keys[index], value);

        dumpOnModification(DATA);
        return hasValue(value, keys);
    }

    /**
     * This method will take a value and then an array of keys and insert the value into a set of nested maps.
     * <p>
     * Example: replaceNestedValue(false, "Key1", "Key2", Key3")
     * Changes:
     * Key1:
     * Key2: {
     * Key3: true
     * }
     * <p>
     * To:
     * Key1:
     * Key2: {
     * Key3: false
     * }
     *
     * @param value Object representing the value you want to insert into a nest of maps
     * @param keys  String... list of keys to add this value you too
     * @return boolean representing if the value was added
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean replaceNestedValue(Object value, String... keys) throws IOException {
        return addNestedValue(value, keys);
    }

    /**
     * This will remove a key and value from the the base data
     * Example: removeValue("Key1")
     * From:
     * {
     * Key1: true
     * Key2: false
     * }
     * <p>
     * To:
     * {
     * Key2: false
     * }
     *
     * @param key String representing the key search for this value in
     * @return boolean representing if the value was added
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean removeValue(String key) throws IOException {
        DATA.remove(key);
        dumpOnModification(DATA);
        return DATA.containsKey(key);
    }

    /**
     * This will remove a key and value from the base map
     * Example: removeNestedValue("Key1", "Key2", Key3")
     * From:
     * Key1:
     * Key2: {
     * Key3: true
     * }
     * <p>
     * To:
     * Key1:
     * Key2: {
     * }
     *
     * @param keys String... list of keys search for this value in
     * @return boolean representing if the value was added
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    @SuppressWarnings("unchecked")
    public boolean removeNestedValue(String... keys) throws IOException {
        Map<String, Object> tempMap = DATA;
        int index = 0;
        int indexLength = keys.length - 1;
        while (index < indexLength) {
            String key = keys[index];
            tempMap = (HashMap<String, Object>) tempMap.get(key);
            index++;
        }
        String key = keys[index];
        if (tempMap.containsKey(key)) {
            tempMap.remove(key);
            dumpOnModification(DATA);
            return tempMap.containsKey(key);
        }
        return false;
    }

    /**
     * This will return if Data has a value in the base Map
     *
     * @param value Object value to be checked for
     * @return boolean representing if the value exist
     */
    public boolean hasValue(Object value) {
        return DATA.containsValue(value);
    }

    /**
     * This will return if Data and the provided keys contain the value
     * <p>
     * Example: hasValue("1", "Key1", "Key2", Key3")
     * Would return true if the Map is as such:
     * Key1:
     * Key2: {
     * Key3: "1"
     * }
     *
     * @param value Object value to be checked for
     * @param keys  String... representing the nested keys to get
     * @return boolean representing if the value exist
     */
    public boolean hasValue(Object value, String... keys) {
        return value.equals(mapUnwrapper(keys));
    }

    /**
     * This will return a value from the base Data Map
     *
     * @param key representing the key for the value to be retrieved
     * @return Object representing if the value exist
     */
    public Object getValue(String key){
        return DATA.get(key);
    }

    /**
     * This will return a nested value from the base Data Map
     *
     * @param keys representing the keys for the value to be retrieved
     * @return Object representing if the value exist
     */
    public Object getNestedValue(String... keys){
        return mapUnwrapper(keys);
    }

    /**
     * This method will unwrap a nested value and return it
     *
     * @param keys String... representing a list of the keys to retrieve an Object from
     * @return Object the object to be returned
     */
    @SuppressWarnings("unchecked")
    private Object mapUnwrapper(String... keys) {
        Object tempObject = getDATA();
        for(String key : keys){
            tempObject = ((Map<String, Object>) tempObject).get(key);
        }
        return tempObject;
    }

    /**
     * This will create a list value with the key provided
     *
     * @param key String representing the key that you want to create a list with
     * @return boolean if the List creation was successful
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean createList(String key) throws IOException {
        return addKeyValue(new ArrayList<>(), key);
    }

    /**
     * This will create a list value with the keys provided
     *
     * @param keys String... representing the key that you want to create a list with
     * @return boolean if the List creation was successful
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean createNestedList(String... keys) throws IOException {
        return addNestedValue(new ArrayList<>(), keys);
    }

    /**
     * This will create a set value with the key provided
     *
     * @param key String representing the key that you want to create a set with
     * @return boolean if the List creation was successful
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean createSet(String key) throws IOException {
        return addKeyValue(new HashSet<>(), key);
    }

    /**
     * This will create a set value with the key provided
     *
     * @param keys String... representing the key that you want to create a set with
     * @return boolean if the List creation was successful
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean createNestedSet(String... keys) throws IOException {
        return addNestedValue(new HashSet<>(), keys);
    }

    /**
     * This method will add a value to your List or Set
     *
     * @param value the Object that you would like to add to your list or set
     * @param key   the key that the list or set belongs to
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    @SuppressWarnings("unchecked")
    public boolean addToCollection(Object value, String key) throws IOException, CollectionDoesNotExistException {
        Collection collection = (Collection) DATA.get(key);
        if (collection != null) {
            synchronized (LOCK_OBJECT) {
                collection.add(value);
            }
        } else {
            throw new CollectionDoesNotExistException();
        }
        dumpOnModification(DATA);
        return collection.contains(value);
    }

    /**
     * This method will add a value to your nested List or Set
     *
     * @param value the Object that you would like to add to your list or set
     * @param keys  the keys that the list or set belongs to
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    @SuppressWarnings("unchecked")
    public boolean addToCollection(Object value, String... keys) throws IOException, CollectionDoesNotExistException {
        Collection collection = (Collection) mapUnwrapper(keys);
        if (collection != null) {
            synchronized (LOCK_OBJECT) {
                collection.add(value);
            }
        } else {
            throw new CollectionDoesNotExistException();
        }
        dumpOnModification(DATA);
        return collection.contains(value);
    }

    /**
     * This method will add a value to your List or Set
     *
     * @param value the Collection of elements that you would like to add to your list or set
     * @param key   the key that the list or set belongs to
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    @SuppressWarnings("unchecked")
    public boolean addCollectionToCollection(Collection value, String key) throws IOException, CollectionDoesNotExistException {
        Collection collection = (Collection) DATA.get(key);
        if (collection != null) {
            synchronized (LOCK_OBJECT) {
                collection.addAll(value);
            }
        } else {
            throw new CollectionDoesNotExistException();
        }
        dumpOnModification(DATA);
        return collection.contains(value);
    }

    /**
     * This method will add a value to your nested List or Set
     *
     * @param value the Collection of elements that you would like to add to your list or set
     * @param keys  the keys that the list or set belongs to
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    @SuppressWarnings("unchecked")
    public boolean addCollectionToCollection(Collection value, String... keys) throws IOException, CollectionDoesNotExistException {
        Collection collection = (Collection) mapUnwrapper(keys);
        if (collection != null) {
            synchronized (LOCK_OBJECT) {
                collection.addAll(value);
            }
        } else {
            throw new CollectionDoesNotExistException();
        }
        dumpOnModification(DATA);
        return collection.contains(value);
    }

    /**
     * This method will add a value to your List or Set and if the collection does not exist, create it
     *
     * @param value the Object that you would like to add to your list or set
     * @param hashSet boolean representing if the collection does not exist do we want to create a Set (true), or a List (false)
     * @param key   the key that the list or set belongs to
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    @SuppressWarnings("unchecked")
    public boolean addToCollection(Object value, boolean hashSet, boolean allowDuplicates, String key) throws IOException, CollectionDoesNotExistException {
        Collection collection = (Collection) DATA.get(key);
        if (collection != null) {
            synchronized (LOCK_OBJECT) {
                if (allowDuplicates) {
                    collection.add(value);
                } else if (!collection.contains(value)){
                    collection.add(value);
                }
            }
        }  else {
            if (hashSet){
                createNestedSet(key);
            } else {
                createNestedList(key);
            }
            collection = (Collection) DATA.get(key);
            if (collection !=null) {
                synchronized (LOCK_OBJECT) {
                    collection.add(value);
                }
            } else {
                throw new CollectionDoesNotExistException();
            }
        }
        dumpOnModification(DATA);
        return collection.contains(value);
    }

    /**
     * This method will add a value to your nested List or Set and if the collection does not exist, create it
     *
     * @param value the Object that you would like to add to your list or set
     * @param hashSet boolean representing if the collection does not exist do we want to create a Set (true), or a List (false)
     * @param keys  the keys that the list or set belongs to
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    @SuppressWarnings("unchecked")
    public boolean addToCollection(Object value, boolean hashSet, boolean allowDuplicates, String... keys) throws IOException, CollectionDoesNotExistException {
        Collection collection = (Collection) mapUnwrapper(keys);
        if (collection != null) {
            synchronized (LOCK_OBJECT) {
                if (allowDuplicates) {
                    collection.add(value);
                } else if (!collection.contains(value)){
                    collection.add(value);
                }
            }
        }  else {
            if (hashSet){
                createNestedSet(keys);
            } else {
                createNestedList(keys);
            }
            collection = (Collection) mapUnwrapper(keys);
            if (collection !=null) {
                synchronized (LOCK_OBJECT) {
                    collection.add(value);
                }
            } else {
                throw new CollectionDoesNotExistException();
            }
        }
        dumpOnModification(DATA);
        return collection.contains(value);
    }

    /**
     * This method will add a value to your List or Set and if the collection does not exist, create it
     *
     * @param value the Collection of elements that you would like to add to your list or set
     * @param hashSet boolean representing if the collection does not exist do we want to create a Set (true), or a List (false)
     * @param key   the key that the list or set belongs to
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    @SuppressWarnings("unchecked")
    public boolean addCollectionToCollection(Collection value, boolean hashSet, String key) throws IOException, CollectionDoesNotExistException {
        Collection collection = (Collection) DATA.get(key);
        if (collection != null) {
            synchronized (LOCK_OBJECT) {
                collection.addAll(value);
            }
        }  else {
            if (hashSet){
                createNestedSet(key);
            } else {
                createNestedList(key);
            }
            collection = (Collection) DATA.get(key);
            if (collection !=null) {
                synchronized (LOCK_OBJECT) {
                    collection.addAll(value);
                }
            } else {
                throw new CollectionDoesNotExistException();
            }
        }
        dumpOnModification(DATA);
        return collection.contains(value);
    }

    /**
     * This method will add a value to your List or Set and if the collection does not exist, create it
     *
     * @param value the Collection of elements that you would like to add to your list or set
     * @param hashSet boolean representing if the collection does not exist do we want to create a Set (true), or a List (false)
     * @param key   the key that the list or set belongs to
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    @SuppressWarnings("unchecked")
    public boolean addCollectionToCollectionUnique(Collection value, boolean hashSet, String key) throws IOException, CollectionDoesNotExistException {
        Collection collection = (Collection) DATA.get(key);
        if (collection != null) {
            synchronized (LOCK_OBJECT) {
                HashSet<Object> objects = new HashSet<>(collection);
                objects.addAll(value);
                collection.clear();
                collection.addAll(value);
            }
        }  else {
            if (hashSet){
                createNestedSet(key);
            } else {
                createNestedList(key);
            }
            collection = (Collection) DATA.get(key);
            if (collection !=null) {
                synchronized (LOCK_OBJECT) {
                    HashSet<Object> objects = new HashSet<>(collection);
                    objects.addAll(value);
                    collection.clear();
                    collection.addAll(value);
                }
            } else {
                throw new CollectionDoesNotExistException();
            }
        }
        dumpOnModification(DATA);
        return collection.contains(value);
    }

    /**
     * This method will add a value to your List or Set and if the collection does not exist, create it
     *
     * @param value the Collection of elements that you would like to add to your list or set
     * @param hashSet boolean representing if the collection does not exist do we want to create a Set (true), or a List (false)
     * @param keys   the key that the list or set belongs to
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    @SuppressWarnings("unchecked")
    public boolean addCollectionToCollectionUnique(Collection value, boolean hashSet, String... keys) throws IOException, CollectionDoesNotExistException {
        Collection collection = (Collection) mapUnwrapper(keys);
        if (collection != null) {
            synchronized (LOCK_OBJECT) {
                HashSet<Object> objects = new HashSet<>(collection);
                objects.addAll(value);
                collection.clear();
                collection.addAll(value);
            }
        }  else {
            if (hashSet){
                createNestedSet(keys);
            } else {
                createNestedList(keys);
            }
            collection = (Collection) DATA.get(keys);
            if (collection !=null) {
                synchronized (LOCK_OBJECT) {
                    HashSet<Object> objects = new HashSet<>(collection);
                    objects.addAll(value);
                    collection.clear();
                    collection.addAll(value);
                }
            } else {
                throw new CollectionDoesNotExistException();
            }
        }
        dumpOnModification(DATA);
        return collection.contains(value);
    }

    /**
     * This method will add a value to your nested List or Set and if the collection does not exist, create it
     *
     * @param value the Collection of elements that you would like to add to your list or set
     * @param hashSet boolean representing if the collection does not exist do we want to create a Set (true), or a List (false)
     * @param keys  the keys that the list or set belongs to
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    @SuppressWarnings("unchecked")
    public boolean addCollectionToCollection(Collection value, boolean hashSet ,String... keys) throws IOException, CollectionDoesNotExistException {
        Collection collection = (Collection) mapUnwrapper(keys);
        if (collection != null) {
            synchronized (LOCK_OBJECT) {
                collection.addAll(value);
            }
        } else {
            if (hashSet){
                createNestedSet(keys);
            } else {
                createNestedList(keys);
            }
            collection = (Collection) mapUnwrapper(keys);
            if (collection !=null) {
                synchronized (LOCK_OBJECT) {
                    collection.addAll(value);
                }
            } else {
                throw new CollectionDoesNotExistException();
            }
        }
        dumpOnModification(DATA);
        return collection.contains(value);
    }

    /**
     * This method will remove a value to your List or Set
     *
     * @param value the Object that you would like to remove to your list or set
     * @param key   the key that the list or set belongs to
     * @return boolean if the items is in the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean removeFromCollection(Object value, String key) throws IOException {
        Collection collection = (Collection) DATA.get(key);
        if (collection != null) {
            synchronized (LOCK_OBJECT) {
                collection.remove(value);
            }
        } else {
            return false;
        }
        dumpOnModification(DATA);
        return !collection.contains(value);
    }

    /**
     * This method will remove a value to your nested List or Set
     *
     * @param value the Object that you would like to remove to your list or set
     * @param keys  the keys that the list or set belongs to
     * @return boolean if the items is in the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean removeFromCollection(Object value, String... keys) throws IOException {
        Collection collection= (Collection) mapUnwrapper(keys);
        if (collection != null) {
            synchronized (LOCK_OBJECT) {
                collection.remove(value);
            }
        } else {
            return false;
        }
        dumpOnModification(DATA);
        return !collection.contains(value);
    }

    /**
     * This method will check to see if a List or Set has a specific value
     *
     * @param value Object to check the list or set for
     * @param key   the key that the list or set belongs to
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean collectionContains(Object value, String key) throws IOException, NullPointerException {
        synchronized (LOCK_OBJECT) {
            return ((Collection) DATA.get(key)).contains(value);
        }
    }

    /**
     * This method will check to see if a nested List or Set has a specific value
     *
     * @param value Object to check the list or set for
     * @param keys  keys that the list or set belongs to
     * @return boolean if the items was added to the list or set
     * @throws java.io.IOException throws an exception if an issue happens with the YAML or File - Only if DUMP_ON_MODIFICATION is turned on
     */
    public boolean collectionContains(Object value, String... keys) throws IOException, NullPointerException {
        synchronized (LOCK_OBJECT) {
            return ((Collection) mapUnwrapper(keys)).contains(value);
        }
    }

    /**
     * This will return a value from a List at the specific index from the base Data Map
     *
     * @param key representing the key for the value to be retrieved
     * @return Object representing if the value exist
     */
    public Object getListValue(int index, String key){
        return ((List) DATA.get(key)).get(index);
    }

    /**
     * This will return a value from a List at the specific index from the base Data Map
     *
     * @param keys representing the keys for the value to be retrieved
     * @return Object representing if the value exist
     */
    public Object getListNestedValue(int index, String... keys){
        return ((List) mapUnwrapper(keys)).get(index);
    }
}