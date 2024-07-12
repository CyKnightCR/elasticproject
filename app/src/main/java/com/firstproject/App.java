package com.firstproject;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
public class App {

    public static void main(String[] args) {
        System.out.println("started.....");


        //Write rejected
//        AnomalyDetector wr = new AnomalyDetector();
//        Instant startw = Instant.parse("2024-06-23T10:25:00.000Z");
//        Instant endw = Instant.parse("2024-06-23T10:36:00.000Z");
//        wr.setRange(startw,endw);
//        List<Instant> instantw = wr.checkWriteRejected();
//
//        //Kibana part... query get and filter
//        if(!instantw.isEmpty()){
//
//            WriteRejectedAlert fil = new WriteRejectedAlert();
//            fil.setTimestampList(instantw);
//            fil.filter();
//        }
//
//        //disk usage peak with refresh inline also peaked
//        AnomalyDetector ad = new AnomalyDetector();
//        Instant start = Instant.parse("2024-06-25T17:47:00.000Z");
//        Instant end = Instant.parse("2024-06-25T18:22:00.000Z");
//        ad.setRange(start,end);
//
//        List<Instant> instantd = ad.checkDiskUsage();
//        if(!instantd.isEmpty()){
//            DiskAlert da = new DiskAlert();
//            da.setTimestampList(instantd);
//            da.filter();
//        }


        String reportFile = "/Users/chatraraj.regmi/Desktop/FirstProject/app/src/main/resources/report.txt";

        try (FileWriter fw = new FileWriter(reportFile, false)) {
            fw.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Looper.loop();

//        Instant start = Instant.parse("2024-07-02T17:55:00.000Z");
//        Instant end = Instant.parse("2024-07-02T18:00:00.000Z");
//        Elastic.getLogs(start,end,"paid1-es7");

    }
}
