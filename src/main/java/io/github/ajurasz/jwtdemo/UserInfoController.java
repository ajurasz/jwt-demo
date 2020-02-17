package io.github.ajurasz.jwtdemo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
class UserInfoController {

    @GetMapping("/api/user-info")
    ResponseEntity<?> userInfo(Principal principal) {
        return ResponseEntity.ok(Map.of("username", principal.getName()));
    }
}
