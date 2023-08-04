package com.rij.amethyst_dev.Routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rij.amethyst_dev.DTO.DTOMapper;
import com.rij.amethyst_dev.Helpers.Authorizator;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.models.Userdb.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
public class adminAPIroutes {

    private final UserService userService;
    private final Authorizator authorizator;
    ObjectMapper objectMapper = new ObjectMapper();


    public adminAPIroutes(UserService userService, Authorizator authorizator) {
        this.userService = userService;
        this.authorizator = authorizator;
    }
    private Function<Object, ResponseEntity<String>> mapjson =
            obj -> {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return ResponseEntity.ok().body(objectMapper.writeValueAsString(obj));
                } catch (JsonProcessingException e) {
                    return ResponseEntity.internalServerError().body("Internal Error");
                }
            };

    @GetMapping("/players")
    public ResponseEntity<String> getallplayers(@CookieValue(value = "_dt", defaultValue = "") String cookie){
        User user = authorizator.authorizedUser(cookie);
        if(user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        if(!user.isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        return mapjson.apply(
                userService.allUsers(0)
                        .stream().map(DTOMapper.PublicDTOFromUser
                ).collect(Collectors.toList()));
/*
        String jsoPrivateView;

        try {
            jsoPrivateView = objectMapper.writerWithView(Views.Private.class).writeValueAsString(userService.allUsers());
        } catch (JsonProcessingException e) {
            jsoPrivateView = "";
        }

        return ResponseEntity.ok().body(jsoPrivateView);*/
    }
}
