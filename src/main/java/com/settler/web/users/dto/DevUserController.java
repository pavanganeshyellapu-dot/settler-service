package com.settler.web.users;

import com.settler.domain.users.UserService;
import com.settler.web.users.dto.CreateUserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/dev/users")
public class DevUserController {
    private final UserService users;
    public DevUserController(UserService users) { this.users = users; }

    @GetMapping("/ping")
    public String ping(){ return "dev-users-ok"; }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateUserRequest req){
        if (req.getEmail()==null || req.getDisplayName()==null)
            return ResponseEntity.badRequest().body(Map.of("error","invalid_input"));
        UUID id = users.createUser(req.getEmail(), req.getDisplayName());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
    }
}
