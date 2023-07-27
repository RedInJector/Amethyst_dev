package com.rij.amethyst_dev.Routes;

import com.rij.amethyst_dev.Helpers.StringComparator;
import com.rij.amethyst_dev.LibertybansData.LibertybansDataService;
import com.rij.amethyst_dev.MinecraftAuth.MCserverAuthService;
import com.rij.amethyst_dev.PlanData.PlanDataService;
import com.rij.amethyst_dev.MinecraftAuth.MinecraftSession;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.models.Userdb.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
public class minecraftServerAPI {

    @Value("${minecraft.server.url}")
    public String MINECRAFT_SERVER_IP;
    @Value("${minecraft.server.APIKEY}")
    public String MINECRAFT_SERVER_API_KEY;

    @Value("${API_KEY}")
    private String APIKEY;
    private final UserService userService;
    private final MCserverAuthService mCserverAuthService;
    private final PlanDataService planDataService;
    private final LibertybansDataService libertybansDataService;


    public minecraftServerAPI(UserService userService, MCserverAuthService mCserverAuthService, PlanDataService planDataService, LibertybansDataService libertybansDataService) {
        this.userService = userService;
        this.mCserverAuthService = mCserverAuthService;
        this.planDataService = planDataService;
        this.libertybansDataService = libertybansDataService;
    }

    @PostMapping("authorize-player2")
    public CompletableFuture<ResponseEntity<String>> minecraftAuth2(@RequestHeader("Key") String apikey, @RequestBody MinecraftSession session) {
        CompletableFuture<ResponseEntity<String>> future = new CompletableFuture<>();

        if (!StringComparator.compareAPIKeys(this.APIKEY, apikey)){
            future.complete(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong"));
            return future;
        }

        System.out.println("Player joined " + session.getName());


        User user = userService.getUserWithMinecraftname(session.getName());
        if (user == null){
            future.complete(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong"));
            return future;
        }
        if(!user.isHasPayed()){
            future.complete(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User has not payed yet"));
            return future;
        }


        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        if(mCserverAuthService.getSessionManager().isValid(session)) {
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



    @GetMapping(value = "/p/head/{name}",
            produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("name") String name) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        String mcserverUrl = "http://" + MINECRAFT_SERVER_IP + "/skin/" + name;

        String imageUrl = "https://mc-heads.net/skin/" + name + ".png";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(mcserverUrl, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                imageUrl = response.getBody();
            }
        } catch (Exception ex) {

        }

        Files.createDirectories(Paths.get("tmp/bufferedheads/"));
        String outputFilePath = "tmp/bufferedheads/" + name + "_head.png"; // Replace with your desired output file path

        try {
            // Download the image from the URL
            BufferedImage originalImage = ImageIO.read(new URL(imageUrl));

            // Crop a 8x8 region from the image
            BufferedImage croppedImage = originalImage.getSubimage(8, 8, 8, 8);

            // Upscale the cropped image to 100x100 pixels
            BufferedImage upscaledImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = upscaledImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            graphics.drawImage(croppedImage, 0, 0, 128, 128, null);
            graphics.dispose();

            // Save the upscaled image to disk
            File outputFile = new File(outputFilePath);
            ImageIO.write(upscaledImage, "png", outputFile);

            // Serve the image as a resource
            Path outputFilePathPath = Path.of(outputFilePath);

            byte[] imageBytes = Files.readAllBytes(Path.of(outputFilePathPath.toString()));

            return new ResponseEntity<>(imageBytes, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping(value = "/p/skin/{name}",
            produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getskin(@PathVariable("name") String point){

        String name = point.endsWith(".png") ? point.substring(0, point.length() - 4) : point;

        RestTemplate restTemplate = new RestTemplate();
        String mcserverUrl = "http://" + MINECRAFT_SERVER_IP + "/skin/" + name;

        String imageUrl = "https://mc-heads.net/skin/" + name + ".png";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(mcserverUrl, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                imageUrl = response.getBody();
            }
        } catch (Exception ex) {

        }

        try {
            BufferedImage originalImage = ImageIO.read(new URL(imageUrl));
            Files.createDirectories(Paths.get("tmp/bufferedskins/"));
            String outputFilePath = "tmp/bufferedskins/" + name + "_skin.png";

            File outputFile = new File(outputFilePath);
            ImageIO.write(originalImage, "png", outputFile);

            Path outputFilePathPath = Path.of(outputFilePath);

            byte[] imageBytes = Files.readAllBytes(Path.of(outputFilePathPath.toString()));

            return new ResponseEntity<>(imageBytes, HttpStatus.OK) ;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping(value = "/p/isonline/{name}")
    public ResponseEntity<String> isOnline(@PathVariable("name") String name){
        RestTemplate restTemplate = new RestTemplate();
        String mcserverUrl = "http://" + MINECRAFT_SERVER_IP + "/isonline/" + name;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(mcserverUrl, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok("Now");
            }
        } catch (Exception ignored) {}

        User user = userService.getUserWithMinecraftname(name);
        if (user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }

        return ResponseEntity.ok().body(String.valueOf(planDataService.getLastOnline(user)));
    }

    @PostMapping("/ban")
    public ResponseEntity<String> banPlayer(@RequestHeader("Key") String apikey, @RequestBody String uuid){

        System.out.println(uuid);

        if(!StringComparator.compareAPIKeys(APIKEY, apikey))
            return ResponseEntity.badRequest().body("Bad Request");

        String name = libertybansDataService.getNameFromUUID(uuid);
        User user = userService.getUserWithMinecraftname(name);
        if(user == null)
            return ResponseEntity.badRequest().body("No player with that name");

        user.setBanned(true);
        userService.saveUser(user);

        return ResponseEntity.ok("Ok");

    }

    @PostMapping("/pardon")
    public ResponseEntity<String> pardonPlayer(@RequestHeader("Key") String apikey, @RequestBody String uuid){

        if(!StringComparator.compareAPIKeys(APIKEY, apikey))
            return ResponseEntity.badRequest().body("Bad Request");

        String name = libertybansDataService.getNameFromUUID(uuid);
        User user = userService.getUserWithMinecraftname(name);
        if(user == null)
            return ResponseEntity.badRequest().body("No player with that name");

        user.setBanned(false);
        userService.saveUser(user);

        return ResponseEntity.ok("Ok");
    }

}
