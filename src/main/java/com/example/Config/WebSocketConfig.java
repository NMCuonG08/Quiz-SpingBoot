package com.example.Config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class WebSocketConfig {

    @Value("${socketio.host:localhost}")
    private String host;

    @Value("${socketio.port:3333}")
    private Integer port;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        // Cần bind vào 0.0.0.0 hoặc localhost
        config.setHostname(host);
        config.setPort(port);
        // Có thể config thêm context: /api/socket.io
        config.setContext("/api/socket.io");

        // Cấu hình CORS
        com.corundumstudio.socketio.SocketConfig socketConfig = new com.corundumstudio.socketio.SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);
        config.setOrigin("*");

        return new SocketIOServer(config);
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}
