package com.firstproject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App {

    public static void main(String[] args) {
        System.out.println("started.....");

        String reportFile = "/Users/chatraraj.regmi/Desktop/FirstProject/app/src/main/resources/report.txt";

        try (FileWriter fw = new FileWriter(reportFile, false)) {
            fw.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Looper.loop();



    }
}
