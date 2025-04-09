package com.eiffelorch;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.*;

import com.eiffeltrigger.EventTriggeredJob;
import com.eiffeltrigger.Message;

public class EiffelOrch {
    private final ConcurrentLinkedQueue<com.eiffeltrigger.EventTriggeredJob> jobEventMatches = new ConcurrentLinkedQueue<>();
    private static final Logger LOGGER = Logger.getLogger(EiffelOrch.class.getName());

    public EiffelOrch() throws Exception {
        startSocketListener();
    }

    // We might have to implement a socket queue for communication with multiple sockets
    private void startSocketListener() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(8090)) {
                while (true) {
                    Socket clientSocket = serverSocket.accept();

                    try (ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                         ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream())) {
                        Message receivedJobs = (Message) objectInputStream.readObject();

                        List<EventTriggeredJob> jobList = receivedJobs.getJobList().values().stream().toList();
                        for (EventTriggeredJob job : jobList) {
                            jobEventMatches.add(job);
                            LOGGER.info("Received job: " + job.getTriggerMatches());
                        }
                        clientSocket.close();
                    } catch (ClassNotFoundException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Socket error", e);
            }
        }).start();
    }

    private void match(String eiffelEvent, EventTriggeredJob eventTriggeredJob) {
        // Find out if the job should be triggered
        // Then send back a list with jobId:s to the controller for immediately execute
    }
}
