package com.sejong.elasticservice.client;

import com.sejong.elasticservice.client.response.UserNameInfo;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", path = "/internal")
public interface
UserClient {
    @GetMapping("/un-info")
    ResponseEntity<Map<String, UserNameInfo>> getUserNameInfos(@RequestParam("usernames") List<String> usernames);
}
