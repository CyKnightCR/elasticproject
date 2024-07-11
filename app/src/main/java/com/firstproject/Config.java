package com.firstproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Config {
    private static Map<String, Object> config;

    static {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            config = mapper.readValue(new File("/Users/chatraraj.regmi/Desktop/FirstProject/app/src/main/resources/config.yaml"), Map.class);
            System.out.println("Configuration loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> tempMap = config;
        for (int i = 0; i < keys.length - 1; i++) {
            tempMap = (Map<String, Object>) tempMap.get(keys[i]);
        }
        return (String) tempMap.get(keys[keys.length - 1]);
    }

}
