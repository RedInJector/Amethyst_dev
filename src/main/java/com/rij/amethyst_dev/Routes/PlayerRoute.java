package com.rij.amethyst_dev.Routes;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rij.amethyst_dev.Configuration.URLS;
import com.rij.amethyst_dev.DTO.AllPlaytime;
import com.rij.amethyst_dev.DTO.AllPlaytime2;
import com.rij.amethyst_dev.DTO.User.Builder.UserDataDTOBuilder;
import com.rij.amethyst_dev.DTO.User.UserDataDTO;
import com.rij.amethyst_dev.PlanData.PlanDataService;
import com.rij.amethyst_dev.Services.DiscordBotService;
import com.rij.amethyst_dev.Services.MCServerService;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RestController
@RequestMapping("/api/v2/p")
@EnableScheduling
public class PlayerRoute {

    @Value("${minecraft.server.url}")
    public String MINECRAFT_SERVER_IP;
    @Value("${minecraft.server.APIKEY}")
    public String MINECRAFT_SERVER_API_KEY;
    ResponseEntity<String> BAD_REQUEST = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request");

    private final UserService userService;
    private final PlanDataService planDataService;
    private final DiscordBotService discordBotService;
    private final MCServerService mcServerService;
    Logger logger = LoggerFactory.getLogger(DiscordBotService.class);

    public PlayerRoute(UserService userService, PlanDataService planDataService, DiscordBotService discordBotService, MCServerService mcServerService) {
        this.userService = userService;
        this.planDataService = planDataService;
        this.discordBotService = discordBotService;
        this.mcServerService = mcServerService;
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


    @GetMapping("/{name}")
    //@Cacheable(value = "publicuser", key = "#name + '-' + #type")
    public ResponseEntity<String> getPublicPlayer(@PathVariable("name") String name, @RequestParam(value = "type", defaultValue = "base") String type){
        if(!type.equals("base") && !type.equals("all") && !type.equals("") && !type.equals("user-only")) return BAD_REQUEST;

        User user = userService.getUserWithMinecraftname(name);
        if(user == null)
            return BAD_REQUEST;


        switch (type) {
            case "", "base" -> {
                return mapjson.apply(new UserDataDTOBuilder()
                        .addPublicUserData(user)
                        .addRoles(discordBotService, user)
                        .build());
            }
            case "user-only" -> {
                return mapjson.apply(new UserDataDTOBuilder()
                        .addPublicUserData(user)
                        .build());
            }
            case "all" -> {
                return mapjson.apply(new UserDataDTOBuilder()
                        .addPublicUserData(user)
                        .addStatisticsData(planDataService, user)
                        .addRoles(discordBotService, user)
                        .build());
            }
            default -> {
                return BAD_REQUEST;
            }
        }


    }

    @GetMapping("/all")
    //@Cacheable(value = "PagableUsers", key = "#page + '-' + #amount")
    public ResponseEntity<String> getPlayerList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "amount", defaultValue = "10") int amount){
        List<UserDataDTO> userDTOs = new ArrayList<>();
        userService.getUserPages(page, amount).getContent().forEach(user -> {
            userDTOs.add(
                    new UserDataDTOBuilder()
                            .addLastTimeOnServer(planDataService, mcServerService, user)
                            .addPublicUserData(user)
                            .addRoles(discordBotService, user)
                            .build());
        });

        return mapjson.apply(userDTOs);
    }



    @GetMapping("/find")
    @Cacheable(value = "find", key = "#name")
    public ResponseEntity<String> getAllPlayerList(@RequestParam(value = "name") String name){

        List<UserDataDTO> userDTOs = new ArrayList<>();
        userService.Search(name).forEach(user -> {
            userDTOs.add(
                    new UserDataDTOBuilder()
                            .addLastTimeOnServer(planDataService, mcServerService, user)
                            .addPublicUserData(user)
                            .build());
        });

        if(userDTOs.isEmpty())
            emptyCertainUsersCache(name);

        return mapjson.apply(userDTOs);
    }

    @GetMapping(value = "/{name}/head.png",
            produces = MediaType.IMAGE_PNG_VALUE)
    @Cacheable(value = "skins", key = "#name")
    public ResponseEntity<byte[]> getImage(@PathVariable("name") String name) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        String mcserverUrl = URLS.Skin + "/" + name;

        String imageUrl = "";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(mcserverUrl, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                if(response.getBody() != null)
                    imageUrl = response.getBody();
            }
            else imageUrl = "https://mc-heads.net/skin/" + name + ".png";
        } catch (Exception ex) {
            imageUrl = "https://mc-heads.net/skin/" + name + ".png";
        }

        Files.createDirectories(Paths.get("tmp/bufferedheads/"));
        String outputFilePath = "tmp/bufferedheads/" + name + "_head.png"; // Replace with your desired output file path

        try {
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

        } catch (Exception e) {
            String steveurl = "https://www.ametis.xyz/files/steve.png";
            BufferedImage originalImage = ImageIO.read(new URL(steveurl));

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
        }
    }


    // TODO WHF??? java.lang.IllegalArgumentException: image == null!
    @GetMapping(value = "/{name}/skin.png",
            produces = MediaType.IMAGE_PNG_VALUE)
    @Cacheable(value = "heads", key = "#name")
    public ResponseEntity<byte[]> getskin(@PathVariable("name") String name){

        RestTemplate restTemplate = new RestTemplate();
        String mcserverUrl = URLS.Skin + "/" + name;

        String imageUrl = "https://mc-heads.net/skin/" + name + ".png";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(mcserverUrl, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                imageUrl = response.getBody();
            }
            else imageUrl = "https://mc-heads.net/skin/" + name + ".png";
        } catch (Exception ex) {
            imageUrl = "https://mc-heads.net/skin/" + name + ".png";
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

        } catch (Exception e) {
            try {
                String steveurl = "https://www.ametis.xyz/files/steve.png";

                BufferedImage originalImage = ImageIO.read(new URL(steveurl));
                Files.createDirectories(Paths.get("tmp/bufferedskins/"));
                String outputFilePath = "tmp/bufferedskins/" + name + "_skin.png";

                File outputFile = new File(outputFilePath);
                ImageIO.write(originalImage, "png", outputFile);

                Path outputFilePathPath = Path.of(outputFilePath);

                byte[] imageBytes = Files.readAllBytes(Path.of(outputFilePathPath.toString()));

                return new ResponseEntity<>(imageBytes, HttpStatus.OK);
            }
            catch (Exception ignored){
                return null;
            }
        }
    }


    @GetMapping(value = "/{name}/isonline")
    public ResponseEntity<String> isOnline(@PathVariable("name") String name){

        User user = userService.getUserWithMinecraftname(name);
        if (user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
        if(mcServerService.isOnline(user.getMinecraftPlayer().getPlayerName()))
            return ResponseEntity.ok().body("0");

        return ResponseEntity.ok().body(String.valueOf(planDataService.getLastOnline(user)));
    }



    @CacheEvict(value = "find", allEntries = true)
    @Scheduled(fixedRateString = "1000000")
    public void emptyAllUsersCache() {
        logger.info("Emptying: find cache");
    }

    @CacheEvict(value = "find", key = "#name")
    public void emptyCertainUsersCache(String name) {

    }

    @CacheEvict(value = "PagableUsers", allEntries = true)
    @Scheduled(fixedRateString = "1400000")
    public void emptyPagableUsersCache() {
        logger.info("Emptying: PagableUsers cache");
    }


    @CacheEvict(value = "publicuser", allEntries = true)
    @Scheduled(fixedRateString = "1100000")
    public void emptyPublicUserCache() {
        logger.info("Emptying: publicuser cache");
    }

    @CacheEvict(value = "skins", allEntries = true)
    @Scheduled(fixedRateString = "1200000")
    public void emptySkinCache() {
        logger.info("Emptying: skins cache");
    }
    @CacheEvict(value = "heads", allEntries = true)
    @Scheduled(fixedRateString = "1300000")
    public void emptyHeadCache() {
        logger.info("Emptying: heads cache");
    }

}
