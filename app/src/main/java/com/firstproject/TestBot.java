package com.firstproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestBot {

    public static void notify(NotificationJsonObject jsonObject) {
        // Define the URL
        String urlString = "http://localhost:3978/api/notification";

        // Create a JSON object
//        NotificationJsonObject jsonObject = new NotificationJsonObject();
//        jsonObject.setTitle("New Anomaly Occurred!");
//        jsonObject.setNotificationUrl("https://aka.ms/teamsfx-notification-new");

        // Convert the JSON object to a JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Send the HTTP POST request
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = connection.getInputStream()) {
                    String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("Response: " + response);
                }
            } else {
                System.out.println("POST request did not work.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class NotificationJsonObject {
    private String title;
    private String notificationUrl;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }
}
