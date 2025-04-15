package com.eiffelorch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JenkinsTrigger {

    private static final Logger LOGGER = Logger.getLogger(JenkinsTrigger.class.getName());

    private static String JENKINS_IP;
    private static String JENKINS_URL;
    private static final String USER = "123";
    private static String API_TOKEN;
    private static final int RETRY_INTERVAL_MS = 5000; // 5 seconds

    public static void send(String jenkinsIP, String jenkinsJobURL, String apiToken) {
        JENKINS_IP = jenkinsIP;
        JENKINS_URL = jenkinsJobURL;
        API_TOKEN = apiToken;
        while (true) {
            try {
                InetAddress.getByName(JENKINS_IP);
                LOGGER.info("🌐 Jenkins host resolved: " + JENKINS_IP);
                break;
            } catch (Exception e) {
                LOGGER.warning("⛔ Jenkins host not resolvable. Retrying in 5s...");
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        // Step 2: Try to send the POST request with retry on failure
        while (true) {
            try {
                LOGGER.info("📡 Attempting to trigger Jenkins job at: " + JENKINS_URL);
                URL url = new URL(JENKINS_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                String auth = USER + ":" + API_TOKEN;
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(0); // No payload required
                }

                int responseCode = connection.getResponseCode();
                LOGGER.info("✅ Jenkins job triggered. Response Code: " + responseCode);

                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine).append("\n");
                    }
                    LOGGER.info("📨 Jenkins Response:\n" + response);
                }

                break; // Exit loop on success

            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "❌ Failed to trigger Jenkins job. Retrying in 5s...", e);
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
