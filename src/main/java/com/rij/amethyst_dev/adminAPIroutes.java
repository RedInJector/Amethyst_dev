package com.rij.amethyst_dev;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rij.amethyst_dev.models.Userdb.UserService;
import com.rij.amethyst_dev.models.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class adminAPIroutes {

    private final UserService userService;
    ObjectMapper objectMapper = new ObjectMapper();

    public adminAPIroutes(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/players")
    public ResponseEntity<String> getallplayers(){

        String jsoPrivateView;

        try {
            jsoPrivateView = objectMapper.writerWithView(Views.Private.class).writeValueAsString(userService.allUsers());
        } catch (JsonProcessingException e) {
            jsoPrivateView = "";
        }

        return ResponseEntity.ok().body(jsoPrivateView);
    }

}
