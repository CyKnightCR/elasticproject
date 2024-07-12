package com.firstproject;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Looper {
    private static List<Instant> timestamps = new ArrayList<>();

//    disk anomaly time
//    private static Instant startTime = Instant.parse("2024-06-25T18:10:00.000Z");
//    private static Instant endTime = Instant.parse("2024-06-25T18:40:00.000Z");

//    write rejected
//    private static Instant startTime = Instant.parse("2024-06-23T10:20:00.000Z");
//    private static Instant endTime = Instant.parse("2024-06-23T10:40:00.000Z");

//    old gc
    private static Instant startTime = Instant.parse("2024-06-24T03:00:00.000Z");
    private static Instant endTime = Instant.parse("2024-06-24T03:10:00.000Z");

    public static void loop() {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try {
                AnomalyDetector ad = new AnomalyDetector();
                ad.setRange(startTime,endTime);

//                timestamps =  ad.checkWriteRejected();
//                if(!timestamps.isEmpty()) {
//                    Trigger.timestamps = timestamps;
//                    Trigger.writeRejectedTrigger();
//                }
//                else System.out.println("no Write Rejected anomaly");

//                timestamps = ad.checkDiskUsage();
//                if(!timestamps.isEmpty()) {
//                    Trigger.timestamps = timestamps;
//                    Trigger.diskUsageTrigger();
//                }
//                else System.out.println("no diskUsage anomaly");


                timestamps = ad.checkOldGc();
                if(!timestamps.isEmpty()) {
                    Trigger.timestamps = timestamps;
                    Trigger.oldGcTrigger();
                }
                else System.out.println("no old gc anomaly");

                //move the window to next range
                startTime = startTime.plus(Duration.ofMinutes(5));
                endTime = endTime.plus(Duration.ofMinutes(5));

                //old gc time..
                if( startTime.isAfter(Instant.parse("2024-06-24T03:10:00.000Z")) ){
                    System.out.println("time jump");
                    startTime = Instant.parse("2024-07-02T13:30:00.000Z");
                    endTime = Instant.parse("2024-07-02T13:40:00.000Z");
                }


            } catch (Exception e) {
                System.err.println("Exception in anomaly detection: " + e.getMessage());
            }
            scheduler.shutdown();
        };
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);

    }
}
