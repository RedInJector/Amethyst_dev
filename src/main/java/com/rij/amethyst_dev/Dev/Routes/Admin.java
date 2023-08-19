package com.rij.amethyst_dev.Dev.Routes;


import com.rij.amethyst_dev.Dev.DTO.Admin.AuthDataDTO;
import com.rij.amethyst_dev.Helpers.Authorizator;
import com.rij.amethyst_dev.Services.DiscordBotService;
import com.rij.amethyst_dev.Services.MCServerService;
import com.rij.amethyst_dev.Services.MCserverAuthService;
import com.rij.amethyst_dev.Services.UserService;
import com.rij.amethyst_dev.models.Userdb.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/admin")
public class Admin {

    private final MCserverAuthService mCserverAuthService;
    ResponseEntity UNAUTHORIZED = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    private final Authorizator authorizator;
    private final UserService userService;
    private final DiscordBotService discordBotService;
    private final MCServerService mcServerService;

    public Admin(MCserverAuthService mCserverAuthService, Authorizator authorizator, UserService userService, DiscordBotService discordBotService, MCServerService mcServerService) {
        this.mCserverAuthService = mCserverAuthService;
        this.authorizator = authorizator;
        this.userService = userService;
        this.discordBotService = discordBotService;
        this.mcServerService = mcServerService;
    }


    @GetMapping("/authData")
    public ResponseEntity<Object> authData(@CookieValue(value = "_dt", defaultValue = "") String cookie) {
        User user = authorizator.authorizedUser(cookie);
        if (user == null || !user.isAdmin())
            return UNAUTHORIZED;


        return ResponseEntity.ok(
                new AuthDataDTO(
                        mCserverAuthService.getAuthqueue(),
                        mCserverAuthService.getSessionManager().getSessions()
                )
        );
    }


    @GetMapping("/players")
    public ResponseEntity<Object> getallplayers(@CookieValue(value = "_dt", defaultValue = "") String cookie,
                                                @RequestParam(defaultValue = "0") int page) {
        User user = authorizator.authorizedUser(cookie);
        if (user == null || !user.isAdmin())
            return UNAUTHORIZED;


        return ResponseEntity.ok(userService.getUserPages(page));
    }


    @GetMapping("searchByName")
    public ResponseEntity<Object> getPlayersByName(@CookieValue(value = "_dt", defaultValue = "") String cookie,
                                                   @RequestParam(defaultValue = "") String name) {
        User user = authorizator.authorizedUser(cookie);
        if (user == null || !user.isAdmin())
            return UNAUTHORIZED;

        if (name.isEmpty())
            return ResponseEntity.ok("");


        return ResponseEntity.ok(userService.getLikeName(name));
    }


    @PostMapping("banplayer")
    public ResponseEntity<Object> banplayer(@CookieValue(value = "_dt", defaultValue = "") String cookie,
                                            @RequestParam(defaultValue = "0") int id,
                                            @RequestParam(defaultValue = "false") boolean banstatus,
                                            @RequestParam(defaultValue = "bannned by admin") String reason) {

        User user = authorizator.authorizedUser(cookie);
        if (user == null || !user.isAdmin())
            return UNAUTHORIZED;

        if (id == 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST");

        userService.getById(id).ifPresent(user1 -> {
            if (banstatus) {
                System.out.println(reason);
            }

            user1.setBanned(banstatus);
            userService.saveUser(user1);
        });

        return ResponseEntity.ok("Ok");
    }

    @PostMapping("perma-banplayer")
    public ResponseEntity<Object> unPermaBanplayer(@CookieValue(value = "_dt", defaultValue = "") String cookie,
                                                   @RequestParam(defaultValue = "0") int id,
                                                   @RequestParam(defaultValue = "false") boolean banstatus,
                                                   @RequestParam(defaultValue = "bannned by admin") String reason) {
        User user = authorizator.authorizedUser(cookie);
        if (user == null || !user.isAdmin())
            return UNAUTHORIZED;

        if (id == 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST");

        userService.getById(id).ifPresent(user1 -> {
            if (banstatus) {
                System.out.println(reason);
            }
            user1.setUnbannable(!banstatus);
            user1.setBanned(banstatus);
            userService.saveUser(user1);
        });

        return ResponseEntity.ok("Ok");
    }


    @PostMapping("/update-user")
    public ResponseEntity<Object> updateUserData(@CookieValue(value = "_dt", defaultValue = "") String cookie,
                                                 @RequestBody Map<String, String> body) {

        User adm = authorizator.authorizedUser(cookie);
        if (adm == null || !adm.isAdmin())
            return UNAUTHORIZED;


        System.out.println(body);

        if (body.containsKey("id") && Integer.parseInt(body.get("id")) == 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST");

        int uid = Integer.parseInt(body.get("id"));
        User user = userService.getById(uid).orElse(null);
        if (user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST");


        body.forEach((key, value) -> {
            switch (key) {
                case "setBan":
                    boolean status = Boolean.parseBoolean(value);
                    user.setBanned(status);

                    if (status) {
                        String reason;
                        if (!body.containsKey("banReason"))
                            reason = "banned by admin";
                        else
                            reason = body.get("banReason");

                        mcServerService.ban(user.getMinecraftPlayer().getPlayerName(), reason);
                    } else
                        mcServerService.unban(user.getMinecraftPlayer().getPlayerName());
                    break;
                case "setUnbannable":
                    status = Boolean.parseBoolean(value);
                    user.setBanned(!status);
                    user.setUnbannable(status);
                    if (status) {
                        String reason;
                        if (!body.containsKey("banReason"))
                            reason = "banned by admin";
                        else
                            reason = body.get("banReason");

                        mcServerService.ban(user.getMinecraftPlayer().getPlayerName(), reason);
                    } else
                        mcServerService.unban(user.getMinecraftPlayer().getPlayerName());


                    break;
                case "setHasPayed":
                    status = Boolean.parseBoolean(value);
                    user.setHasPayed(status);

                    if (status) {
                        discordBotService.givePlayerRole(user.getDiscordUser().getDiscordId());
                        mcServerService.addToWhitelist(user.getMinecraftPlayer().getPlayerName());
                    }

                    break;

            }
        });


        userService.saveUser(user);


        return ResponseEntity.ok("Ok");
    }


}
