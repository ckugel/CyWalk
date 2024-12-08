package com.cywalk.spring_boot.Leaderboard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


@Configuration
public class LeaderboardWebSocketConfig {

    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}