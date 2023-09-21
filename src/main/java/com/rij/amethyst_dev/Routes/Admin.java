package com.rij.amethyst_dev.Routes;


import com.rij.amethyst_dev.DTO.Admin.AuthDataDTO;
import com.rij.amethyst_dev.DTO.User.IUserDTO;
import com.rij.amethyst_dev.Enums.UserRoles;
import com.rij.amethyst_dev.Services.Authorizator;
import com.rij.amethyst_dev.Services.DiscordBotService;
import com.rij.amethyst_dev.Services.MCServerService;
import com.rij.amethyst_dev.Services.MCserverAuthService;
import com.rij.amethyst_dev.Services.UserService;
import com.rij.amethyst_dev.jsons.AllowOnServer;
import com.rij.amethyst_dev.jsons.BanUser;
import com.rij.amethyst_dev.models.Userdb.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/admin")
public class Admin {

    private final MCserverAuthService mCserverAuthService;
    ResponseEntity UNAUTHORIZED = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    private final Authorizator authorizator;
    private final UserService userService;
    private final DiscordBotService discordBotService;
    private final MCServerService mcServerService;
    Logger logger = LoggerFactory.getLogger(Admin.class);

    public Admin(MCserverAuthService mCserverAuthService, Authorizator authorizator, UserService userService, DiscordBotService discordBotService, MCServerService mcServerService) {
        this.mCserverAuthService = mCserverAuthService;
        this.authorizator = authorizator;
        this.userService = userService;
        this.discordBotService = discordBotService;
        this.mcServerService = mcServerService;
    }


    @GetMapping("/authData")
    public ResponseEntity<Object> authData(@CookieValue(value = "_dt", defaultValue = "") String cookie) {
        return authorizator.RoleBased(cookie, UserRoles.ADMIN, adm -> ResponseEntity.ok(
                new AuthDataDTO(
                        mCserverAuthService.getAuthqueue(),
                        mCserverAuthService.getSessionManager().getSessions()
                )
        ));
    }


    @GetMapping("/players")
    public ResponseEntity<Object> getallplayers(@CookieValue(value = "_dt", defaultValue = "") String cookie,
                                                @RequestParam(defaultValue = "0") int page) {
        return authorizator.RoleBased(cookie, UserRoles.MODERATOR, adm -> ResponseEntity.ok(userService.getUserPages(page)));
    }


    @GetMapping("/searchByName")
    public ResponseEntity<Object> getPlayersByName(@CookieValue(value = "_dt", defaultValue = "") String cookie,
                                                   @RequestParam(defaultValue = "") String name,
                                                   @RequestParam(defaultValue = "0") int page) {
        return authorizator.RoleBased(cookie, UserRoles.MODERATOR, adm -> {
            if (name.isEmpty())
                return ResponseEntity.ok("");


            List<IUserDTO> users = userService.getLikeName(name, page).stream().map(User::toPrivateDTO).collect(Collectors.toList());

            return ResponseEntity.ok(users);
        });
    }


    @PostMapping("/modifyuser/allowonserver")
    public ResponseEntity<Object> AllowOnServer(@CookieValue(value = "_dt", defaultValue = "") String cookie, @RequestBody AllowOnServer body) {
        return authorizator.RoleBased(cookie, UserRoles.MODERATOR, PaddingMatcher -> {
            User user = userService.getById(body.getId()).orElse(null);
            if (user == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST");

            discordBotService.givePlayerRole(user.getDiscordUser().getDiscordId());
            if(body.isStatus())
                mcServerService.addToWhitelist(user.getMinecraftPlayer().getPlayerName());

            user.setHasPayed(body.isStatus());
            userService.saveUser(user);
            logger.info("allowed On Server: " + user.getId() + ". " + user.getMinecraftPlayer().getPlayerName());

            return ResponseEntity.ok("Banned " + user.getId());
        });
    }

    @PostMapping("/modifyuser/ban")
    public ResponseEntity<Object> BanUser(@CookieValue(value = "_dt", defaultValue = "") String cookie, @RequestBody BanUser body) {
        return authorizator.RoleBased(cookie, UserRoles.MODERATOR, PaddingMatcher -> {
            User user = userService.getById(body.getId()).orElse(null);
            if (user == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST");

            user.setBanned(true);
            if (body.isPermaban())
                user.setUnbannable(false);

            mcServerService.ban(user.getMinecraftPlayer().getPlayerName(), body.getReason(), body.getTime());
            userService.saveUser(user);
            logger.info("banned: " +
                    user.getId() +
                    ". " +
                    user.getMinecraftPlayer().getPlayerName() +
                    " reason:" +
                    body.getReason());

            return ResponseEntity.ok("Banned " + user.getId());
        });
    }

    @PostMapping("/modifyuser/unban")
    public ResponseEntity<Object> unBanUser(@CookieValue(value = "_dt", defaultValue = "") String cookie, @RequestBody BanUser body) {
        return authorizator.RoleBased(cookie, UserRoles.MODERATOR, adm -> {
            User user = userService.getById(body.getId()).orElse(null);
            if (user == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST");

            user.setBanned(false);
            user.setUnbannable(true);
            mcServerService.unban(user.getMinecraftPlayer().getPlayerName());
            userService.saveUser(user);
            logger.info("unbanned: " +
                    user.getId() +
                    ". " +
                    user.getMinecraftPlayer().getPlayerName());


            return ResponseEntity.ok("Banned " + user.getId());
        });
    }
}




