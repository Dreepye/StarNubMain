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

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public class YamlUtilities {

    public static void toFileYamlDump (Map<String, Object> data, String fileName) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setAllowUnicode(true);
        Yaml yaml = new Yaml(options);
        try (Writer writer = new FileWriter(fileName)){
            yaml.dump(data, writer);
        }
    }

    public static void toFileYamlDump (Map<String, Object> data, String directory, String fileName) throws IOException {
        File file = new File(directory);
        if(!file.exists()){
            file.mkdir();
        }
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setAllowUnicode(true);
        Yaml yaml = new Yaml(options);
        try (Writer writer = new FileWriter(directory + fileName)){
            yaml.dump(data, writer);
        }
    }

    public void toScreenYamlDump(Map<String, Object> data){
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setAllowUnicode(true);
        Yaml yaml = new Yaml(options);
        System.out.println(yaml.dump(data));
    }

    public String toStringYamlDump(Map<String, Object> data){
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setAllowUnicode(true);
        Yaml yaml = new Yaml(options);
        return yaml.dump(data);
    }


    @SuppressWarnings("unchecked")
    public Map<String, Object> filePathYamlLoader (String filePath) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = null;
        try {
            data = (Map<String, Object>) yaml.load(new FileInputStream(new File(filePath)));
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> resourceYamlLoader(String fileInClassPath) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = null;
        try {
            data = (Map<String, Object>) yaml.load(this.getClass().getResourceAsStream("/"+fileInClassPath));
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> resourceStreamYamlLoader(InputStream inputStreamLoader) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = null;
        try {
            data = (Map<String, Object>) yaml.load(inputStreamLoader);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return data;
    }
}
