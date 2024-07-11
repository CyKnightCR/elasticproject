package com.firstproject;

import java.io.IOException;
import java.time.Instant;

import java.util.Map;

public class InfluxConnection {
    public String token = "jPa9gGIFZRXQuZNMT7EYXeL5Lvq95K4e04UITFNiQbneep7wkjM7dgZs7JLzDGXP-crXxFPQb2K8CNHGCfXBdA==";
    public String bucket = "refined";
    public String org = "sprinklr";
    public String url = "http://localhost:8086";
    public Instant startTime;
    public Instant endTime;
    private Map<String, Object> config;

    public InfluxConnection(){
        setInflux();
    }

    public void setRange(Instant start, Instant end){
        startTime = start;
        endTime = end;
    }
    public void setInflux(){
        token = Config.getProperty("influx.token");
        bucket = Config.getProperty("influx.bucket");
        org = Config.getProperty("influx.org");
        url = Config.getProperty("influx.url");
    }

}
