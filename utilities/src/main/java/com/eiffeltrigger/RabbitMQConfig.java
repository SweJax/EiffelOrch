package com.eiffeltrigger;

import java.io.Serializable;

public class RabbitMQConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private String configName;
    private String username;
    private String password;
    private String host;
    private String port;
    private String exchange;

    

    public RabbitMQConfig(String configName, String username, String password, String host, String port, String exchange) {
        this.configName = configName;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.exchange = exchange;
    }

    public String getConfigName() {
        return configName;
    }

    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getHost() {
        return host;
    }
    
    public String getPort() {
        return port;
    }
    
    public String getExchange() {
        return exchange;
    }

}
