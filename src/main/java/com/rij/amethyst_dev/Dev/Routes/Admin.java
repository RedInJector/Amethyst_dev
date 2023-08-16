package com.rij.amethyst_dev.Dev.Routes;


import com.rij.amethyst_dev.Dev.DTO.Admin.AuthDataDTO;
import com.rij.amethyst_dev.Dev.DTO.User.Builder.UserDataDTOBuilder;
import com.rij.amethyst_dev.Dev.DTO.User.UserDataDTO;
import com.rij.amethyst_dev.Helpers.Authorizator;
import com.rij.amethyst_dev.Services.MCserverAuthService;
import com.rij.amethyst_dev.Services.UserService;
import com.rij.amethyst_dev.models.Userdb.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/admin")
public class Admin {

    private final MCserverAuthService mCserverAuthService;
    ResponseEntity UNAUTHORIZED = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    private final Authorizator authorizator;
    private final UserService userService;

    public Admin(MCserverAuthService mCserverAuthService, Authorizator authorizator, UserService userService) {
        this.mCserverAuthService = mCserverAuthService;
        this.authorizator = authorizator;
        this.userService = userService;
    }


    @GetMapping("/authData")
    public ResponseEntity<Object> authData() {
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
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        if (!user.isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");


        return ResponseEntity.ok(userService.getUserPages(page));
    }


    @PostMapping("banplayer")
    public ResponseEntity<Object> banplayer(@CookieValue(value = "_dt", defaultValue = "") String cookie,
                                            @RequestParam(defaultValue = "0") int id,
                                            @RequestParam(defaultValue = "false") boolean banstatus) {
        User user = authorizator.authorizedUser(cookie);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        if (!user.isAdmin())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        if(id == 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST");

        userService.getById(id).ifPresent(user1 -> {
            user1.setBanned(banstatus);
            userService.saveUser(user1);
        });

        return ResponseEntity.ok("Ok");
    }

}
