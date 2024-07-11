package com.firstproject;

import java.time.Instant;
import java.util.List;

//calls kibana part (getting logs and then query filtering)
public class Trigger {
    public static List<Instant> timestamps;

    public static void writeRejectedTrigger(){
        System.out.println("write rej trigger ");
        WriteRejectedAlert alert = new WriteRejectedAlert();
        alert.setTimestampList(timestamps);
        alert.kibana();
    }
    public static void oldGcTrigger(){
        System.out.println("old gc trigger ");
        OldGcAlert alert = new OldGcAlert();
        alert.setTimestampList(timestamps);
        alert.kibana();
    }

    public static void diskUsageTrigger(){
        System.out.println("disk alert trigger");
        DiskAlert alert = new DiskAlert();
        alert.setTimestampList(timestamps);
        alert.kibana();

    }


}
