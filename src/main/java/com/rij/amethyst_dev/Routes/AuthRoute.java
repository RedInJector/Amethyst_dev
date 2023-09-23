package com.rij.amethyst_dev.Routes;

import com.rij.amethyst_dev.Configuration.oAuthConfig;
import com.rij.amethyst_dev.Services.DiscordOauthService;
import com.rij.amethyst_dev.Services.Authorizator;
import com.rij.amethyst_dev.Helpers.RandomStringGenerator;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.Services.UserService;
import io.mokulu.discord.oauth.DiscordAPI;
import io.mokulu.discord.oauth.model.TokensResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;


@RestController
@RequestMapping("/api/v2")
public class AuthRoute {

    private final UserService userService;
    private final oAuthConfig oauthConfig;
    private final Authorizator authorizator;
    private final ApplicationEventPublisher eventPublisher;
    private final DiscordOauthService discordOauthService;
    Logger logger = LoggerFactory.getLogger(AuthRoute.class);

    @Autowired
    public AuthRoute(UserService userService, oAuthConfig oauthConfig, Authorizator authorizator, ApplicationEventPublisher eventPublisher, DiscordOauthService discordOauthService) {
        this.userService = userService;
        this.oauthConfig = oauthConfig;
        this.authorizator = authorizator;
        this.eventPublisher = eventPublisher;
        this.discordOauthService = discordOauthService;
    }


    @GetMapping("/redirect")
    public RedirectView oAuth2redirect(@RequestParam(value = "code", defaultValue = "") String code, HttpServletResponse response) {
        if(code.isEmpty())
            return new RedirectView(oauthConfig.redirecturl);

        TokensResponse tokensResponse;
        try {
            tokensResponse = discordOauthService.getDiscordOAuth().getTokens(code);
        } catch (IOException e) {
            logger.error(String.valueOf(e));
            return new RedirectView(oauthConfig.redirecturl);
        }

        String token = tokensResponse.getAccessToken();

        io.mokulu.discord.oauth.model.User discordUser;
        try {
            discordUser = new DiscordAPI(token).fetchUser();
        } catch (IOException e) {
            logger.error(String.valueOf(e));
            return new RedirectView(oauthConfig.redirecturl);
        }

        User user = User.getFromMokuluDiscordAPIUser(discordUser);

        user = userService.saveUserIfNotExists(user);


        userService.saveOauth(user, tokensResponse);


        String accessToken = RandomStringGenerator.generateAccessKey();
        userService.saveNewAccessToken(user, accessToken);

        userService.removeExpiredtokens(user);

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
