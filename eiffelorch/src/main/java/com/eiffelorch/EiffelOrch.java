package com.eiffelorch;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;
import java.util.logging.*;

import com.eiffeltrigger.EventTriggeredJob;
import com.eiffeltrigger.Message;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.BuiltinExchangeType;

public class EiffelOrch {
    private final ConcurrentLinkedQueue<com.eiffeltrigger.EventTriggeredJob> jobEventMatches = new ConcurrentLinkedQueue<>();
    private static final Logger LOGGER = Logger.getLogger(EiffelOrch.class.getName());
    private static final ConnectionFactory factory = new ConnectionFactory();
    private static final String EXCHANGE = System.getenv("EXCHANGE");
    private static final String ROUTINGKEY = System.getenv("ROUTINGKEY");

    public EiffelOrch() throws Exception {
        factory.setUsername(System.getenv("USERNAME"));
        factory.setPassword(System.getenv("PASSWORD"));
        factory.setHost(System.getenv("HOST"));
        String portStr = System.getenv("PORT");
        factory.setPort(Integer.parseInt(portStr));

        startRabbitMQConsumer();
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

        private void startRabbitMQConsumer() {
        new Thread(() -> {
            try {
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
                channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT);
                String queueName = channel.queueDeclare().getQueue();
                channel.queueBind(queueName, EXCHANGE, ROUTINGKEY);

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), "UTF-8");
                    LOGGER.info("NEW MESSAGE RMQ: " + message);
                };

                LOGGER.info("STARTING RMQ");

                channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
            } catch (IOException | TimeoutException e) {
                LOGGER.log(Level.SEVERE, "RabbitMQ error", e);
            }
        }).start();
    }
}
