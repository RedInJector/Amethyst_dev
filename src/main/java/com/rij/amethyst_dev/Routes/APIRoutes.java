package com.rij.amethyst_dev.Routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rij.amethyst_dev.DTO.DTOMapper;
import com.rij.amethyst_dev.Helpers.Authorizator;
import com.rij.amethyst_dev.Helpers.StringComparator;
import com.rij.amethyst_dev.Services.LibertybansDataService;
import com.rij.amethyst_dev.PlanData.PlanDataService;
import com.rij.amethyst_dev.Services.DiscordBotService;
import com.rij.amethyst_dev.events.UserPayedEvent;
import com.rij.amethyst_dev.jsons.Donation;
import com.rij.amethyst_dev.jsons.Submitname;
import com.rij.amethyst_dev.models.Payments.Payment;
import com.rij.amethyst_dev.Services.PaymentService;
import com.rij.amethyst_dev.models.Userdb.MinecraftPlayer;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.Services.UserService;
import com.rij.amethyst_dev.webSockets.PaymentSocket.PaymentSocketService;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1")
public class APIRoutes {



    private final Authorizator authorizator;
    private final UserService userService;
    private final DiscordBotService discordBotService;
    private final PlanDataService planDataService;
    private final PaymentSocketService paymentSocketService;
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentService paymentService;
    Logger logger = LoggerFactory.getLogger(APIRoutes.class);
    @Value("${minecraft.server.url}")
    public String MINECRAFT_SERVER_IP;
    @Value("${donatello.api.key}")
    public String DONATELLO_API_KEY;
    @Value("${MINECRAFT_API_KEY}")
    public String MINECRAFT_API_KEY;
    private final LibertybansDataService libertybansDataService;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private Set<String> autobanMinecraftname = new HashSet<>();
    private Set<String> autoWhitelistdiscordId = new HashSet<>();


    public APIRoutes(Authorizator authorizator, UserService userService, DiscordBotService discordBotService, PlanDataService planDataService, PaymentSocketService paymentSocketService, ApplicationEventPublisher eventPublisher, PaymentService paymentService, LibertybansDataService libertybansDataService){
        this.authorizator = authorizator;
        this.userService = userService;
        this.discordBotService = discordBotService;
        this.planDataService = planDataService;
        this.paymentSocketService = paymentSocketService;
        this.eventPublisher = eventPublisher;
        this.paymentService = paymentService;
        this.libertybansDataService = libertybansDataService;


        autobanMinecraftname.add("Vfuga");
        autobanMinecraftname.add("maksi_kola");
        autobanMinecraftname.add("NightReL1x");
        autobanMinecraftname.add("_TouchMe");
        autobanMinecraftname.add("quiescent");
        autobanMinecraftname.add("Mr_roma000");
        autobanMinecraftname.add("DJ_ZAP");
        autobanMinecraftname.add("Concen_");
        autobanMinecraftname.add("Solvet_Unior");
        autobanMinecraftname.add("Arymurk");
        autobanMinecraftname.add("Dim4igD");
        autobanMinecraftname.add("AntiPapik");
        autobanMinecraftname.add("Kartoplya337");
        autobanMinecraftname.add("Prostoludina");
        autobanMinecraftname.add("Raytomilk");
        autobanMinecraftname.add("FyZzZen");
        autobanMinecraftname.add("Korusant");
        autobanMinecraftname.add("TimohaP");
        autobanMinecraftname.add("zxanreal");
        autobanMinecraftname.add("deadmau5");
        autobanMinecraftname.add("mozilo4ka");
        autobanMinecraftname.add("Linego_2");
        autobanMinecraftname.add("G_Freeman_HL");
        autobanMinecraftname.add("F1eND_FN");
        autobanMinecraftname.add("arad568");
        autobanMinecraftname.add("2shonka");
        autobanMinecraftname.add("0sol");
        autobanMinecraftname.add("nemi80808");


        autoWhitelistdiscordId.add("681234309285544084");
        autoWhitelistdiscordId.add("835016286115201104");
        autoWhitelistdiscordId.add("343960894801444864");
        autoWhitelistdiscordId.add("836224799772114965");
        autoWhitelistdiscordId.add("325186158952710145");
        autoWhitelistdiscordId.add("429716996943249408");
        autoWhitelistdiscordId.add("889947316126105651");
        autoWhitelistdiscordId.add("452518902186901504");
        autoWhitelistdiscordId.add("600030648568381460");
        autoWhitelistdiscordId.add("702944188336963796");
        autoWhitelistdiscordId.add("987433906091466852");
        autoWhitelistdiscordId.add("435826614228746240");
        autoWhitelistdiscordId.add("576119828012859409");
        autoWhitelistdiscordId.add("380360849786929182");
        autoWhitelistdiscordId.add("938828885615853668");
        autoWhitelistdiscordId.add("876131343933771856");
        autoWhitelistdiscordId.add("400659867922333696");
        autoWhitelistdiscordId.add("1007704276430229515");
        autoWhitelistdiscordId.add("739512694318825474");
        autoWhitelistdiscordId.add("611603418838204417");
        autoWhitelistdiscordId.add("918941548396302386");
        autoWhitelistdiscordId.add("681137478442156032");
        autoWhitelistdiscordId.add("440869432764661770");
        autoWhitelistdiscordId.add("783781173432025108");
        autoWhitelistdiscordId.add("690163105883619414");
        autoWhitelistdiscordId.add("591210117412814859");
        autoWhitelistdiscordId.add("738827964506177640");
        autoWhitelistdiscordId.add("776742849647083540");
        autoWhitelistdiscordId.add("861708637776379964");
        autoWhitelistdiscordId.add("428582412700876801");
        autoWhitelistdiscordId.add("700407229970382880");
        autoWhitelistdiscordId.add("730703209403908106");
        autoWhitelistdiscordId.add("619213669276057600");
        autoWhitelistdiscordId.add("822500080879403090");
        autoWhitelistdiscordId.add("527893620104101888");
        autoWhitelistdiscordId.add("860171068010004530");
        autoWhitelistdiscordId.add("905489814583918683");
        autoWhitelistdiscordId.add("1069540413909762071");
        autoWhitelistdiscordId.add("761453861658820608");
        autoWhitelistdiscordId.add("700052510278287432");
        autoWhitelistdiscordId.add("586530419504185346");
        autoWhitelistdiscordId.add("564003652390617098");
        autoWhitelistdiscordId.add("876136330671820870");
        autoWhitelistdiscordId.add("607842539327717376");
        autoWhitelistdiscordId.add("878301883247231006");
        autoWhitelistdiscordId.add("957997105115430913");
        autoWhitelistdiscordId.add("914130331957882930");
        autoWhitelistdiscordId.add("775406023712309249");
        autoWhitelistdiscordId.add("682618063241347124");
        autoWhitelistdiscordId.add("293085708795641857");
        autoWhitelistdiscordId.add("521064786403000320");
        autoWhitelistdiscordId.add("801717450806132747");
        autoWhitelistdiscordId.add("514751771147501579");
        autoWhitelistdiscordId.add("520668115579961355");
        autoWhitelistdiscordId.add("535811043155378239");
        autoWhitelistdiscordId.add("593433075858538547");
        autoWhitelistdiscordId.add("479666336956547072");
        autoWhitelistdiscordId.add("438349355276042281");
    }


    @PostMapping("/donatellopayment")
    public ResponseEntity<String> test2(@RequestBody Donation donate, @RequestHeader("X-Key") String header){
        if(!StringComparator.compareAPIKeys(DONATELLO_API_KEY, header))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");


        logger.info("Incoming payment: " + donate.toString());

        Payment payment = new Payment();
        payment.fromDonate(donate);
        paymentService.savePayment(payment);

        User user;
        switch (donate.getGoal().toLowerCase()){
            case "перепустка на сервер":
                if(donate.getAmount().compareTo(new BigDecimal("35.99")) < 0 ) break;
                if(!isMinecraftNameValid(donate.getClientName())) break;

                user = userService.getUserWithMinecraftname(donate.getClientName());
                if(user == null) break;

                user.setHasPayed(true);
                userService.saveUser(user);

                addToWhitelist(donate.getClientName());

                UserPayedEvent event = new UserPayedEvent(this, user);
                eventPublisher.publishEvent(event);

                break;
            case "підтримка сервера":
                break;
            case "розбан на сервері":

                if(donate.getAmount().compareTo(new BigDecimal("100.00")) < 0 ) break;

                user = userService.getUserWithMinecraftname(donate.getClientName());
                if(user == null) break;

                user.setBanned(false);
                user.setHasPayed(true);
                userService.saveUser(user);
                try {
                    libertybansDataService.pardon(user.getMinecraftPlayer().getPlayerName());
                }catch (Exception any){}
                addToWhitelist(donate.getClientName());
                break;
        }
        return ResponseEntity.ok("Ok");
    }

    public boolean isMinecraftNameValid(String input) {
        // Check for null or empty string
        if (input == null || input.isEmpty()) {
            return false;
        }

        // Check for length greater than 20
        if (input.length() > 20 || input.length() < 3) {
            return false;
        }

        // Check for non-alphabetic characters or spaces
        String pattern = "^[A-Za-z0-9_]+$";
        return input.matches(pattern);

    }

    private void addToWhitelist(String name){
        RestTemplate restTemplate = new RestTemplate();
        String mcserverUrl = "http://" + MINECRAFT_SERVER_IP + "/register";

        User user = userService.getUserWithMinecraftname(name);
        if (user == null){
            logger.info("user does not exists");
            return;
        }

        user.setHasPayed(true);
        paymentSocketService.sendMessage(String.valueOf(user.getId()));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Key", MINECRAFT_API_KEY);
            headers.set("name", user.getMinecraftPlayer().getPlayerName());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(mcserverUrl, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Added to Whitelist " + name);
            }
        } catch (Exception e) {
            logger.info("Unable to add to Whitelist " + name + " trying again in 30 seconds");
            executorService.schedule(() -> addToWhitelist(name), 30, TimeUnit.SECONDS);
        }


    }

    private Function<Object, ResponseEntity<String>> mapjson =
            obj -> {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return ResponseEntity.ok().body(objectMapper.writeValueAsString(obj));
                } catch (JsonProcessingException e) {
                    logger.error(String.valueOf(e));
                    return ResponseEntity.internalServerError().body("Internal Error");
                }
            };

    @GetMapping("/getuserdata2")
    public ResponseEntity<String> GetUserData2(@CookieValue(value = "_dt", defaultValue = "") String cookie){

        User user = authorizator.authorize.apply(cookie);
        if(user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        return DTOMapper.PublicDTOFromUser.andThen(mapjson).apply(user);
    }

    @GetMapping("check-minecraft-name")
    public ResponseEntity<String> MinecraftNameExists(@RequestParam(defaultValue = "") String name){
        if(name.equals(""))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD REQUEST");

        MinecraftPlayer minecraftPlayer = userService.getMinecraftPlayer(name);
        if(minecraftPlayer != null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("username was already taken");



        return ResponseEntity.status(HttpStatus.OK).body("Ok");
    }

    @PostMapping("submit-minecraft-name")
    public ResponseEntity<String> SubmitMinecraftName(@CookieValue(value = "_dt", defaultValue = "") String cookie, @RequestBody Submitname body){
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

        if(autobanMinecraftname.contains(minecraftPlayer.getPlayerName())) {
            user.setBanned(true);
            userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Banned");
        }

        if(autoWhitelistdiscordId.contains(user.getDiscordUser().getDiscordId())){
            user.setHasPayed(true);
            userService.saveUser(user);
            addToWhitelist(user.getMinecraftPlayer().getPlayerName());

        }

        userService.saveUser(user);


        return ResponseEntity.status(HttpStatus.OK).body("Ok");
    }


    @GetMapping("/p/roles/{name}")
    public ResponseEntity<String> getRoles(@PathVariable("name") String name){
        User user = userService.getUserWithMinecraftname(name);
        if(user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        List<Role> roles = discordBotService.getGuildRoles(user.getDiscordUser().getDiscordId());
        if(roles == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");

        return mapjson.apply(roles.stream()
                .map(DTOMapper.DTOFromRole)
                .collect(Collectors.toList())
        );
    }
/*
    @GetMapping("/p/playtime/{name}")
    public ResponseEntity<String> getPlaytime(@PathVariable("name") String name){
        User user = userService.getUserWithMinecraftname(name);
        if(user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No user");

        List<PlayTimeDateDTO> playtime = planDataService.getHeatmapTime(user);
        if(playtime != null)
            return mapjson.apply(planDataService.getHeatmapTime(user));
        return ResponseEntity.ok( "null");
    }
*/
    @GetMapping("/p/allplaytime/{name}")
    public ResponseEntity<String> getAllPlaytime(@PathVariable("name") String name){
        User user = userService.getUserWithMinecraftname(name);
        if(user == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No user");

        return mapjson.apply(planDataService.getPlayTime(user));
    }


}



