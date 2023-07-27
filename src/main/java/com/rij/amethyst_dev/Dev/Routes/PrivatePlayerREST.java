package com.rij.amethyst_dev.Dev.Routes;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rij.amethyst_dev.Dev.Events.UserAddedMinecraftName;
import com.rij.amethyst_dev.Dev.DTO.User.Builder.UserDataDTOBuilder;
import com.rij.amethyst_dev.Helpers.Authorizator;
import com.rij.amethyst_dev.Helpers.MinecraftNameValidator;
import com.rij.amethyst_dev.PlanData.PlanDataService;
import com.rij.amethyst_dev.bot.DiscordBotService;
import com.rij.amethyst_dev.jsons.Submitname;
import com.rij.amethyst_dev.models.Userdb.MinecraftPlayers.MinecraftPlayer;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.models.Userdb.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;

@RestController
@RequestMapping("/api/v2/me")
public class PrivatePlayerREST {

    ResponseEntity<String> UNAUTHORIZED = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    ResponseEntity<String> BAD_REQUEST = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request");


    private final Authorizator authorizator;
    private final UserService userService;
    private final PlanDataService planDataService;
    private final DiscordBotService discordBotService;
    private final ApplicationEventPublisher eventPublisher;
    Logger logger = LoggerFactory.getLogger(PrivatePlayerREST.class);

    public PrivatePlayerREST(Authorizator authorizator, UserService userService, PlanDataService planDataService, DiscordBotService discordBotService, ApplicationEventPublisher eventPublisher) {
        this.authorizator = authorizator;
        this.userService = userService;
        this.planDataService = planDataService;
        this.discordBotService = discordBotService;
        this.eventPublisher = eventPublisher;
    }

    private Function<Object, ResponseEntity<String>> mapjson = obj -> {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return ResponseEntity.ok().body(objectMapper.writeValueAsString(obj));
                } catch (JsonProcessingException e) {
                    //TODO: logger.error(String.valueOf(e));
                    return ResponseEntity.internalServerError().body("Internal Error");
                }
            };


    @GetMapping("/check-minecraft-name")
    public ResponseEntity<String> checkMinecraftName(@RequestParam(defaultValue = "") String name) {
        if (name.equals(""))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("name is not acceptable 1");

        if(!MinecraftNameValidator.check(name))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("name is not acceptable 2");

        MinecraftPlayer minecraftPlayer = userService.getMinecraftPlayer(name);
        if (minecraftPlayer != null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("username was already taken");

        return ResponseEntity.status(HttpStatus.OK).body("Ok");
    }

    @GetMapping("/submit-minecraft-name")
    public ResponseEntity<String> addMinecraftName(@CookieValue(value = "_dt", defaultValue = "") String cookie, @RequestBody Submitname body){
        MinecraftPlayer mp = userService.getMinecraftPlayer(body.getName());
        if(mp != null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("username was already taken");

        User user = authorizator.authorizedUser(cookie);
        if(user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        if(user.getMinecraftPlayer() != null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Name was already set");

        MinecraftPlayer minecraftPlayer = new MinecraftPlayer();
        minecraftPlayer.setPlayerName(body.getName());

        user.setMinecraftPlayer(minecraftPlayer);

        userService.saveUser(user);

        UserAddedMinecraftName event = new UserAddedMinecraftName(this, user);
        eventPublisher.publishEvent(event);

        return ResponseEntity.status(HttpStatus.OK).body("Ok");
    }

    @GetMapping
    public ResponseEntity<String> getPrivatePlayer(
            @CookieValue(value = "_dt", defaultValue = "a") String token,
            @RequestParam(value = "type", defaultValue = "base") String type
    ) {

        //logger.info("Request: " + token + "   " + type);

        if(!type.equals("base") && !type.equals("all") && !type.equals("user-only")) return BAD_REQUEST;

        User user = authorizator.authorizedUser(token);
        if(user == null)
            return UNAUTHORIZED;

        switch (type) {
            case "", "base" -> {
                return mapjson.apply(new UserDataDTOBuilder()
                        .addPrivateUserData(user)
                        .addRoles(discordBotService, user)
                        .build());
            }
            case "user-only" -> {
                return mapjson.apply(new UserDataDTOBuilder()
                        .addPrivateUserData(user)
                        .build());
            }
            case "all" -> {
                return mapjson.apply(new UserDataDTOBuilder()
                        .addPrivateUserData(user)
                        .addStatisticsData(planDataService, user)
                        .addRoles(discordBotService, user)
                        .build());
            }
            default -> {
                return BAD_REQUEST;
            }
        }

    }

}
