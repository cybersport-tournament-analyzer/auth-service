package com.vkr.user_service.client;

import com.vkr.user_service.dto.server.Server;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@FeignClient(name = "matchmaking-service", url = "http://localhost:8081/")
public interface MatchmakingServiceClient {
    @GetMapping("/server")
    Page<Server> findAll(Pageable pageable) throws IOException, InterruptedException ;
}
