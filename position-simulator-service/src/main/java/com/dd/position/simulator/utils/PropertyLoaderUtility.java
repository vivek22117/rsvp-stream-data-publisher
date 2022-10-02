package com.dd.position.simulator.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

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

    public Optional<List<String>> getVehiclePositions(String key) {
        List<String> thisVehicleReports = new ArrayList<>();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = loader.getResourceAsStream(key);

            if (inputStream != null) {
                Scanner sc = new Scanner(inputStream);
                while (sc.hasNextLine()) {
                    String nextReport = sc.nextLine();
                    thisVehicleReports.add(nextReport);
                }
            } else {
                throw new FileNotFoundException("property file '"
                        + key + "' not found in the classpath");
            }

            return Optional.of(thisVehicleReports);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
