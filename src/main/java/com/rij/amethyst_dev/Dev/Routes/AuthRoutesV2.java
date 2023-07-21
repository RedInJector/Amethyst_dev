package com.rij.amethyst_dev.Dev.Routes;

import com.rij.amethyst_dev.Configuration.oAuthConfig;
import com.rij.amethyst_dev.Dev.Events.UserRegistered;
import com.rij.amethyst_dev.Helpers.Authorizator;
import com.rij.amethyst_dev.Helpers.RandomStringGenerator;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.models.Userdb.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.redinjector.discord.oAuth2.DiscordOAuth2;
import org.redinjector.discord.oAuth2.models.DiscordUser;
import org.redinjector.discord.oAuth2.models.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v2")
public class AuthRoutesV2 {

    private final UserService userService;
    private final oAuthConfig oauthConfig;
    private final Authorizator authorizator;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public AuthRoutesV2(UserService userService, oAuthConfig oauthConfig, Authorizator authorizator, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.oauthConfig = oauthConfig;
        this.authorizator = authorizator;
        this.eventPublisher = eventPublisher;
    }


    @GetMapping("/redirect")
    public RedirectView oAuth2redirect(@RequestParam(value = "code", defaultValue = "") String code, HttpServletResponse response) {
        if(code.equals(""))
            return new RedirectView(oauthConfig.redirecturl);

        Token token = DiscordOAuth2.getToken(code);

        if(token == null)
            return new RedirectView(oauthConfig.redirecturl);

        DiscordUser discordUser = DiscordOAuth2.getDiscordUser(token);

        User user = User.getUserFromDiscordUser(discordUser);


        User existingUser = userService.getUser(user);
        if(existingUser == null){
            userService.saveUser(user);

            UserRegistered event = new UserRegistered(this, user);
            eventPublisher.publishEvent(event);
        }


        user = userService.saveUserIfNotExists(user);

        userService.saveOauth(user, token);

        String accessToken = RandomStringGenerator.generate(64);
        userService.saveNewAccessToken(user, accessToken);

        Cookie cookie = new Cookie("_dt", accessToken);
        cookie.setPath("/");
        cookie.setMaxAge(604000);

        response.addCookie(cookie);

        return new RedirectView(oauthConfig.redirecturl);
    }
    @DeleteMapping("/logout")
    public ResponseEntity<String> LogOut2(@CookieValue(value = "_dt", defaultValue = "") String cookie, HttpServletResponse response){
        User user = authorizator.authorizedUser(cookie);
        if(user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        userService.removeUserAccessToken(user, cookie);

        Cookie cook = new Cookie("_dt", null);
        cook.setMaxAge(0);
        cook.setPath("/");

        response.addCookie(cook);

        return new ResponseEntity<>("Ok", HttpStatus.OK);
    }

}
