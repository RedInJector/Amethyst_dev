package com.rij.amethyst_dev.Routes;


import com.rij.amethyst_dev.events.UserBanned;
import com.rij.amethyst_dev.events.UserPardoned;
import com.rij.amethyst_dev.Helpers.StringComparator;
import com.rij.amethyst_dev.Services.*;
import com.rij.amethyst_dev.PlanData.PlanDataService;
import com.rij.amethyst_dev.MinecraftAuth.MinecraftSession;
import com.rij.amethyst_dev.models.Userdb.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v2/server")
public class ServerAPI {

    @Value("${API_KEY}")
    private String APIKEY;

    private final UserService userService;
    private final MCserverAuthService mCserverAuthService;
    private final PlanDataService planDataService;
    private final LibertybansDataService libertybansDataService;
    private final ApplicationEventPublisher eventPublisher;
    private final MCServerService mcServerService;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public ServerAPI(UserService userService, MCserverAuthService mCserverAuthService, PlanDataService planDataService, LibertybansDataService libertybansDataService, ApplicationEventPublisher eventPublisher, MCServerService mcServerService) {
        this.userService = userService;
        this.mCserverAuthService = mCserverAuthService;
        this.planDataService = planDataService;
        this.libertybansDataService = libertybansDataService;
        this.eventPublisher = eventPublisher;
        this.mcServerService = mcServerService;
    }

    @PostMapping("authorize-player")
    public CompletableFuture<ResponseEntity<String>> minecraftAuth2(@RequestHeader("Key") String apikey, @RequestBody MinecraftSession session) {
        CompletableFuture<ResponseEntity<String>> future = new CompletableFuture<>();

        if (!StringComparator.compareAPIKeys(this.APIKEY, apikey)){
            future.complete(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong"));
            return future;
        }

        User user = userService.getUserWithMinecraftname(session.getName());
        if (user == null){
            future.complete(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong"));
            return future;
        }
        if(!user.isHasPayed()){
            future.complete(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User has not payed yet"));
            return future;
        }

        if(mCserverAuthService.isValid(session)) {
            future.complete(ResponseEntity.ok("Something happened successfully"));
        }else{
            mCserverAuthService.addToAuthQueue(user, future, session);
            executorService.schedule(() -> future.complete(ResponseEntity.status(500).body("Timeout occurred")), 60, TimeUnit.SECONDS);
        }

        return future.exceptionally(throwable -> ResponseEntity.status(500).body("Something went wrong"));
    }

    @GetMapping("player-left")
    public ResponseEntity<String> minecraftauthleftServer(@RequestHeader("Key") String apikey, @RequestParam(defaultValue = "") String name){
        if (!StringComparator.compareAPIKeys(this.APIKEY, apikey)){
            return ResponseEntity.badRequest().body("Bad Request");
        }

        User user = userService.getUserWithMinecraftname(name);
        System.out.println("Player left " + name);
        mCserverAuthService.PlayerLeft(user);
        return ResponseEntity.ok().body("Ok");
    }


    @PostMapping("/set-banned")
    public ResponseEntity<String> banPlayer(@RequestHeader("Authorization") String apikey, @RequestBody String uuid){

        if(!StringComparator.compareAPIKeys(APIKEY, apikey))
            return ResponseEntity.badRequest().body("Bad Request");

        String name = libertybansDataService.getNameFromUUID(uuid);
        User user = userService.getUserWithMinecraftname(name);
        if(user == null)
            return ResponseEntity.badRequest().body("No player with that name");

        user.setBanned(true);
        userService.saveUser(user);

        UserBanned event = new UserBanned(this, user);
        eventPublisher.publishEvent(event);

        return ResponseEntity.ok("Ok");

    }

    @PostMapping("/set-pardonned")
    public ResponseEntity<String> pardonPlayer(@RequestHeader("Authorization") String apikey, @RequestBody String uuid){

        if(!StringComparator.compareAPIKeys(APIKEY, apikey))
            return ResponseEntity.badRequest().body("Bad Request");

        String name = libertybansDataService.getNameFromUUID(uuid);
        User user = userService.getUserWithMinecraftname(name);
        if(user == null)
            return ResponseEntity.badRequest().body("No player with that name");

        user.setBanned(false);
        userService.saveUser(user);

        UserPardoned event = new UserPardoned(this, user);
        eventPublisher.publishEvent(event);

        return ResponseEntity.ok("Ok");
    }


    @GetMapping("/register-join")
    public ResponseEntity<Object> Onjoin(@RequestHeader("name") String name,
                                           @RequestHeader("Authorization") String key){
        if(!StringComparator.compareAPIKeys(APIKEY, key))
            return ResponseEntity.badRequest().body("Bad Request");

        mcServerService.setOnline(name, true);
        System.out.println(name + " Joined");
        return ResponseEntity.ok("Ok");
    }

    @GetMapping("/register-quit")
    public ResponseEntity<Object> OnQuit(@RequestHeader("name") String name,
                                           @RequestHeader("Authorization") String key){

        if(!StringComparator.compareAPIKeys(APIKEY, key))
            return ResponseEntity.badRequest().body("Bad Request");

        System.out.println(name + " left");
        mcServerService.setOnline(name, false);
        return ResponseEntity.ok("Ok");
    }


    @GetMapping("/all-quit")
    public ResponseEntity<Object> OnAllQuit( @RequestHeader("Authorization") String key ){
        if(!StringComparator.compareAPIKeys(APIKEY, key))
            return ResponseEntity.badRequest().body("Bad Request");

        System.out.println("All left");
        mcServerService.removeAllOnline();
        return ResponseEntity.ok("Ok");
    }



}
