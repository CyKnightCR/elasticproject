package com.firstproject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestBot {
    public static void main() {
        String url = "http://localhost:3978/api/notification";
        String resultContent = "ddd";
        try {
            resultContent = new String(Files.readAllBytes(Paths.get("/Users/chatraraj.regmi/Desktop/FirstProject/app/src/main/resources/report.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String jsonInputString = "{ \"message\": \"" + resultContent + "\" }";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");

            StringEntity entity = new StringEntity(jsonInputString);
            post.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(post)) {
                int responseCode = response.getStatusLine().getStatusCode();
                System.out.println("Response code: " + responseCode);

                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Response: " + responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
