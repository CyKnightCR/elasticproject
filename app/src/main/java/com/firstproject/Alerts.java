package com.firstproject;

import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

abstract class Alerts {
    public List<Instant> timestamp;
    public List<QueryLog> querySources = new ArrayList<QueryLog>();
    public List<TimeRange> ranges = new ArrayList<>();
    public Instant startTime;
    public Instant endTime;
    public void setTimestampList(@NotNull List<Instant> timestamp) {
        this.timestamp = timestamp;
        timestamp.sort(Instant::compareTo);
        timestamp = timestamp.stream().distinct().toList();

        //ranges set for kibana
        for(int i =0;i<timestamp.size();i++) {
            TimeRange r = new TimeRange();
            r.end = timestamp.get(i);
            if(i==0){
                r.start = r.end.minus(5, ChronoUnit.MINUTES);
                ranges.add(r);
                continue;
            }
            Instant prevPeak = timestamp.get(i - 1);
            if (prevPeak.isBefore(r.end.minus(5, ChronoUnit.MINUTES))) {
                r.start = r.end.minus(5, ChronoUnit.MINUTES);
            } else r.start = prevPeak;

            ranges.add(r);
        }
//
//        Instant start = Instant.parse("2024-07-02T17:55:00.000Z");
//        Instant end = Instant.parse("2024-07-02T18:05:00.000Z");
//        String clusterName = "paid1-es7";
//
//        querySources = Elastic.getLogs(start,end,clusterName);
//

    }

//    public void queryLog(){
//        //get the query log for timestampa...
//        // then get the sources in query sources array
//        List<Instant> range = new ArrayList<>();
//        for(int i=0;i<timestamp.size();i++){
////            Instant tstart =
//        }
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            // Read the JSON file as a tree
//            JsonNode rootNode = objectMapper.readTree(new File(file));
//
//            // Navigate to the "hits" array inside the JSON structure
//            JsonNode hitsNode = rootNode.path("hits").path("hits");
//
//            if (hitsNode.isArray()) {
//                for (JsonNode hitNode : hitsNode) {
//                    // Get the "source" node within each "hit" node
//                    JsonNode sourceNode = hitNode.path("_source");
//
//                    // Map the "source" node to the QueryLog class
//                    QueryLog source = objectMapper.treeToValue(sourceNode, QueryLog.class);
//                    querySources.add(source);
//
////                    System.out.println("Date: " + source.getDate());
////                    System.out.println("Level: " + source.getLevel());
////                    System.out.println("UUID: " + source.getUuid());
////                    System.out.println("Fetch Size: " + source.getFetchSize());
////                    System.out.println("Timestamp: " + source.getTimestamp());
////                    System.out.println(source.getAttributes().getQuerySize());
//                    // Print other fields as needed
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    public synchronized void report(String text) {
        try (FileWriter writer = new FileWriter("/Users/chatraraj.regmi/Desktop/FirstProject/app/src/main/resources/report"+".txt", true)) {
            writer.write(text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract void filter();
    public void kibana(){

//        int cnt=0;
        for(TimeRange range: ranges){
//            System.out.println(cnt++);
            String clusterName = Config.getProperty("es.clusterName");
            startTime = range.start;
            endTime = range.end;
            if(startTime.isAfter(endTime)) System.out.println("time range error ");
            querySources = Elastic.getLogs(startTime,endTime,clusterName);
            this.filter();
        }
    }
}
