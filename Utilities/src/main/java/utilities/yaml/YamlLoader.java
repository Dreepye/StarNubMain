package utilities.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class YamlLoader {

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
