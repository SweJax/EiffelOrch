package com.eiffeltrigger.interfaces;

import java.io.Serializable;

public interface IEventTriggeredJob extends Serializable {

    public int getJobId();
    public String getJobTriggerMatches();
    
}