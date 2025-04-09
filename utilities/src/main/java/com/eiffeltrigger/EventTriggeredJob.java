package com.eiffeltrigger;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import org.json.JSONObject;

public class EventTriggeredJob implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int podKey;

    @JsonProperty("jobId")
    private final int jobId;

    @JsonProperty("triggerMatches")
    private final String triggerMatches;

    private final RabbitMQConfig rabbitMQConfig;

    public EventTriggeredJob(RabbitMQConfig rmqConf, int jobId, JSONObject triggerMatches) {
        this.podKey = PodKey.createPodKey(rmqConf, triggerMatches);
        this.jobId = jobId;
        this.triggerMatches = triggerMatches.toString();
        this.rabbitMQConfig = rmqConf;
    }

    public int getPodKey() {
        return podKey;
    }

    public int getJobId() {
        return jobId;
    }

    public JSONObject getTriggerMatches() {
        return new JSONObject(triggerMatches);
    }

    public RabbitMQConfig getRabbitMQConfig() {
        return rabbitMQConfig;
    }

    @Override
    public String toString() {
        return "EventTriggeredJob{jobId=" + jobId + ", triggerMatches='" + triggerMatches + "'}";
    }
}
