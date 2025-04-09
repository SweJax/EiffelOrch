package com.eiffeltrigger;

import java.io.Serializable;
import java.util.HashMap;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final EventTriggeredJob eventTriggeredJob;
    private final HashMap<Integer, EventTriggeredJob> jobList;


    public Message(EventTriggeredJob eventTriggeredJob, HashMap<Integer, EventTriggeredJob> jobList) {
        this.eventTriggeredJob = eventTriggeredJob;
        this.jobList = jobList;
    }

    public EventTriggeredJob getEventTriggeredJob() {
        return this.eventTriggeredJob;
    }

    public HashMap<Integer, EventTriggeredJob> getJobList() {
        return this.jobList;
    }
    
}