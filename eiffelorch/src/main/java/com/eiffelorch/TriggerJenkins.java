package com.eiffelorch;

import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.logging.*;
import io.github.cdimascio.dotenv.Dotenv;

public class TriggerJenkins {

    private static final Logger LOGGER = Logger.getLogger(EiffelOrch.class.getName());
    private static String JENKINS_IP;
    private static String JENKINS_PORT;
    private static String JENKINS_URL;
    private static String JENKINS_USER;
    private static String JENKINS_JOB_NAME;
    private static String JENKINS_API_TOKEN;
    private static String JENKINS_JOB_TOKEN;

    public static void triggerJenkinsJob(String ip, String port, String user, String jobName, String jobToken) {

        Dotenv dotenv = Dotenv.load();
        JENKINS_IP = ip;
        JENKINS_PORT = port;
        JENKINS_USER = user;
        JENKINS_JOB_NAME = jobName;
        JENKINS_API_TOKEN = dotenv.get("API_SECRET_TOKEN");
        JENKINS_JOB_TOKEN = jobToken;
        JENKINS_URL = "http://" + JENKINS_IP + ":" + JENKINS_PORT + "/job/" + JENKINS_JOB_NAME + "/build?token="
                + JENKINS_JOB_TOKEN;

        new Thread(() -> {
            // Wait until the Jenkins hostname is resolvable
            while (true) {
                try {
                    InetAddress.getByName(JENKINS_IP);
                    LOGGER.info("🌐 Jenkins host resolved: " + JENKINS_IP);
                    break;
                } catch (UnknownHostException e) {
                    LOGGER.warning("⛔ Jenkins host not resolvable. Retrying in 3s...");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            // Try to connect and trigger Jenkins
            while (true) {
                try {
                    LOGGER.info("📡 Attempting to trigger Jenkins job at: " + JENKINS_URL);
                    URL url = new URL(JENKINS_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    String auth = JENKINS_USER + ":" + JENKINS_API_TOKEN;
                    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                    connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(0); // POST body placeholder
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

                    break; // success, exit loop

                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "❌ Failed to trigger Jenkins job. Retrying in 5s...", e);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }).start();
    }

}