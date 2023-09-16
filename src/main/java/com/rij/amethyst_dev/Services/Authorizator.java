package com.rij.amethyst_dev.Services;

import com.rij.amethyst_dev.Enums.UserRoles;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class Authorizator {
    private UserService userService;
    ResponseEntity<Object> UNAUTHORIZED = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

    public Authorizator(UserService userService) {
        this.userService = userService;
    }

    public User authorizedUser(String accessToken){
        if(accessToken.isEmpty())
            return null;

        User user = userService.getUserByAccessToken(accessToken);
        if(user == null)
            return null;

        if(!user.isAccessTokenValid(accessToken))
            return null;

        return user;
    }


    public ResponseEntity<Object> RoleBased(String accessToken, UserRoles role, Function<User, ResponseEntity<Object>> callback){
        User user = authorizedUser(accessToken);
        if(user != null && user.getRole().hasPermission(role))
            return callback.apply(user);

        return UNAUTHORIZED;
    }
}
