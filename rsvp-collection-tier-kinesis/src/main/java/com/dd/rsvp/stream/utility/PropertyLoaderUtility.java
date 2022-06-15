package com.dd.rsvp.stream.utility;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoaderUtility {

    private static final String ENVIRONMENT = "Environment";
    private static final String SUFFIX = ".properties";
    private static final String PREFIX = "/application";

    private static PropertyLoaderUtility propertyLoaderUtility = new PropertyLoaderUtility();

    public PropertyLoaderUtility() {
    }

    public static PropertyLoaderUtility getInstance() {
        return propertyLoaderUtility;
    }

    public String getProperty(String key) {
        String propFileName = null;
        try {
            String environment = System.getenv(ENVIRONMENT);
            if (environment != null) {
                environment = "-" + environment;
                propFileName = PREFIX + environment + SUFFIX;
            }
            Properties prop = new Properties();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = loader.getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '"
                        + propFileName + "' not found in the classpath");
            }
            // get the property value and print it out
            return prop.getProperty(key);
        } catch (Exception e) {
            return null;
        }
    }
}
