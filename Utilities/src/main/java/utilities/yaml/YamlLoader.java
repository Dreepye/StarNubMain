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

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * Represents a YAML loader
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class YamlLoader {

    //CHANGE TO LOADER // DUMPER // CONTANOR

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This will load a YAML file from a file path
     * <p/>
     * @param filePath String representing the file path on disk
     * @return Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> filePathYamlLoader (String filePath) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = null;
        try {
            data = (Map<String, Object>) yaml.load(new FileInputStream(new File(filePath)));
        } catch (FileNotFoundException e) {
            return null;
        }
        return data;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This will load a YAML file from a file path inside of a jar
     * <p/>
     * @param fileInClassPath
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> resourceYamlLoader(String fileInClassPath) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = null;
        try {
            data = (Map<String, Object>) yaml.load(this.getClass().getResourceAsStream("/"+fileInClassPath));
    } catch (Exception e) {
            return null;
    }
    return data;
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p/>
     * Recommended: For internal StarNub usage.
     * <p/>
     * Uses: This will load a YAML file from a resource stream pertaining to a jar
     * <p/>
     * @param inputStreamLoader
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> resourceStreamYamlLoader(InputStream inputStreamLoader) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = null;
        try {
            data = (Map<String, Object>) yaml.load(inputStreamLoader);
        } catch (Exception e) {
            return null;
        }
        return data;
    }

}
