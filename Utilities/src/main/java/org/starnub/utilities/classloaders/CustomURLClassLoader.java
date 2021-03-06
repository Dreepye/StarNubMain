package org.starnub.utilities.classloaders;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

public class CustomURLClassLoader<T> extends URLClassLoader{

    public CustomURLClassLoader(Class<T> starNubClass) {
        super(new URL[]{}, starNubClass.getClassLoader());
    }

    public void addNewUrl(URL url){
        addURL(url);
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }

    public URL getSpecificResource(URL fileURL, String resourcesPath) throws IOException {
        return getSpecificResource(fileURL.toString(), resourcesPath);
    }

    public URL getSpecificResource(String fileName, String resourcesPath) throws IOException {
        Enumeration<URL> resources = getResources(resourcesPath);
        while(resources.hasMoreElements()){
            URL url = resources.nextElement();
            if(url.toString().contains(fileName)){
                return url;
            }
        }
        return null;
    }

    public InputStream getSpecificResourceAsStream(URL fileURL, String resourcesPath) throws IOException {
        return getSpecificResourceAsStream(fileURL.toString(), resourcesPath);
    }

    public InputStream getSpecificResourceAsStream(String fileName, String resourcesPath) throws IOException {
        Enumeration<URL> resources = getResources(resourcesPath);
        while(resources.hasMoreElements()){
            URL url = resources.nextElement();
            if(url.toString().contains(fileName)){
                return url.openStream();
            }
        }
        return null;
    }
}