package com.eiffeltrigger;

import java.util.Objects;

import org.json.JSONObject;

public class PodKey {
    public static int createPodKey(RabbitMQConfig rmq, JSONObject matchInfo) {
        return Objects.hash(rmq, matchInfo) * 31; 
    }
}
