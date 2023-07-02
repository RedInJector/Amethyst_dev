package com.rij.amethyst_dev;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rij.amethyst_dev.Helpers.Authorizator;
import com.rij.amethyst_dev.Helpers.RandomStringGenerator;
import com.rij.amethyst_dev.MinecraftAuth.MCserverAuthService;
import com.rij.amethyst_dev.jsons.Donation;
import com.rij.amethyst_dev.jsons.Submitname;
import com.rij.amethyst_dev.jsons.minecraftAuth.MinecraftSession;
import com.rij.amethyst_dev.models.Userdb.MinecraftPlayer;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.models.Userdb.UserService;
import com.rij.amethyst_dev.models.Views;
import com.rij.amethyst_dev.models.oAuth.Oauth;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.redinjector.discord.oAuth2.DiscordOAuth2;
import org.redinjector.discord.oAuth2.models.DiscordUser;
import org.redinjector.discord.oAuth2.models.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;



@RestController
@RequestMapping("/api/v1")
public class APIRoutes {


    private final Authorizator authorizator;
    private final UserService userService;
    private static oAuthConfig oauthConfig;
    private final MCserverAuthService mCserverAuthService;
    Logger logger = LoggerFactory.getLogger(APIRoutes.class);


    public APIRoutes(Authorizator authorizator, UserService userService, oAuthConfig oAuthConfig, MCserverAuthService mCserverAuthService){
        this.authorizator = authorizator;
        this.userService = userService;
        oauthConfig = oAuthConfig;
        this.mCserverAuthService = mCserverAuthService;
    }


    @GetMapping("/redirect")
    public RedirectView  oAuth2redirect(@RequestParam String code, HttpServletResponse response) {
        Token token = DiscordOAuth2.getToken(code);

        if(token == null)
            return new RedirectView("/error");

        DiscordUser discordUser = DiscordOAuth2.getDiscordUser(token);

        User user = userService.createUserFromDiscordUser(discordUser);
        user = userService.saveUserIfNotExists(user);

        userService.saveOauth(user, token);
        //userService.SaveUseroAuth(user, token);
        String accessToken = RandomStringGenerator.generate(64);
        userService.saveNewAccessToken(user, accessToken);

        Cookie cookie = new Cookie("_dt", accessToken);
        cookie.setPath("/");
        cookie.setMaxAge(604000);

        response.addCookie(cookie);

        return new RedirectView(oauthConfig.redirecturl);
    }

    @GetMapping("/test1")
    public ResponseEntity<String> geta(@CookieValue("_dt") String cookie){
        User user = userService.getUserWithToken(cookie);
        if(user == null){
            return ResponseEntity.ok("asdasd");
        }
        List<Oauth> o = userService.getUsersoAuths(user);

        ObjectMapper objectMapper = new ObjectMapper();

        String jsonPublicView;
        //String jsonPrivateView;
        try {
            jsonPublicView = objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok(jsonPublicView);
    }

    @PostMapping("/donatellotest1")
    public ResponseEntity<String> test2(@RequestBody Donation donate, @RequestHeader("X-Key") String header){

        return ResponseEntity.ok("Ok");
    }

    @GetMapping("/getuserdata")
    public ResponseEntity<String> GetUserData(@RequestHeader("Authorization") String authToken){

        if(!authorizator.isAuthorized(authToken))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        User user = userService.getUserByAccessToken(authToken);

        //User user = userService.getUserByoAuthToken(authToken);


        ObjectMapper objectMapper = new ObjectMapper();


        try {
            String jsonPublicView = objectMapper.writerWithView(Views.Private.class).writeValueAsString(user);
            return ResponseEntity.ok(jsonPublicView);
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().body("Internal Error");
        }
    }

    /*logger.trace("A TRACE Message");
    logger.debug("A DEBUG Message");
    logger.info("An INFO Message");
    logger.warn("A WARN Message");
    logger.error("An ERROR Message");*/


    @GetMapping("/getuserdata2")
    public ResponseEntity<String> GetUserData2(@CookieValue(value = "_dt", defaultValue = "") String cookie){
        if(cookie.equals(""))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        if(!authorizator.isAuthorized(cookie))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        User user = userService.getUserByAccessToken(cookie);

        if(user.getMinecraftPlayer() != null)
            user.getMinecraftPlayer().setSkinUrl("https://mc-heads.net/skin/c5ef3347-4593-4f39-8bb1-2eaa40dd986e");

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonPublicView = objectMapper.writerWithView(Views.Private.class).writeValueAsString(user);

            return ResponseEntity.ok(jsonPublicView);
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().body("Internal Error");
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> LogOut(@RequestHeader("Authorization") String authToken){
        User user = userService.getUserByoAuthToken(authToken);

        if(user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        userService.deteleToken(authToken);

        return ResponseEntity.ok("Ok");
    }

    @DeleteMapping("/logout2")
    public ResponseEntity<String> LogOut2(@CookieValue(value = "_dt", defaultValue = "") String cookie, HttpServletResponse response){
        if(cookie.equals(""))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        if(!authorizator.isAuthorized(cookie))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        User user = userService.getUserByAccessToken(cookie);
        userService.removeUserAccessToken(user, cookie);


        Cookie cook = new Cookie("_dt", null);
        cook.setMaxAge(0);
        cook.setPath("/");

        response.addCookie(cook);


        //userService.deteleToken(cookie);

        return new ResponseEntity<>("Ok", HttpStatus.OK);
    }

    @GetMapping("authorize-player")
    public CompletableFuture<ResponseEntity<String>> minecraftAuth(@RequestParam(defaultValue = "") String name) {

        CompletableFuture<ResponseEntity<String>> future = new CompletableFuture<>();

        if(name.equals(""))
            return future.exceptionally(throwable -> ResponseEntity.status(500).body("Something went wrong"));

        User user = userService.getUserWithMinecraftname(name);
        if(user == null)
            return future.exceptionally(throwable -> ResponseEntity.status(500).body("Something went wrong"));



        mCserverAuthService.addToAuthQueue(user, future);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> future.complete(ResponseEntity.status(500).body("Timeout occurred")), 60, TimeUnit.SECONDS);

        return future.exceptionally(throwable -> ResponseEntity.status(500).body("Something went wrong"));
    }
    @GetMapping("authorize-player2")
    public CompletableFuture<ResponseEntity<String>> minecraftAuth2(@RequestParam(defaultValue = "") MinecraftSession session) {
        return null;
    }

    @GetMapping("player-left")
    public ResponseEntity<String> minecraftauthleftServer(@RequestParam(defaultValue = "") String name){
        User user = userService.getUserWithMinecraftname(name);
        System.out.println("Player left " + name);
        mCserverAuthService.PlayerLeft(user);
        return ResponseEntity.ok().body("Ok");
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
    public ResponseEntity<String> SubmitMinecraftName(@CookieValue(value = "_dt", defaultValue = "null") String cookie, @RequestBody Submitname body){
        MinecraftPlayer mp = userService.getMinecraftPlayer(body.getName());
        if(mp != null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("username was already taken");

        if(!authorizator.isAuthorized(cookie))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        User user = userService.getUserByAccessToken(cookie);

        if(user.getMinecraftPlayer() != null)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Name was already set");

        MinecraftPlayer minecraftPlayer = new MinecraftPlayer();
        minecraftPlayer.setPlayerName(body.getName());
        minecraftPlayer.setAllowedOnServer(true);

        user.setMinecraftPlayer(minecraftPlayer);
        userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.OK).body("Ok");
    }

    @GetMapping(value = "/player/head/{name}",
            produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("name") String name) throws IOException {
        String imageUrl = "https://mc-heads.net/skin/maksutko.png"; // Replace with your image URL
        Files.createDirectories(Paths.get("./bufferedskins/"));
        String outputFilePath = "./bufferedskins/" + name + "_skin.png"; // Replace with your desired output file path

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
            /*
            // Delete the image from disk after serving
            resource.onClose(() -> {
                try {
                    Files.delete(outputFilePathPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
*/

            byte[] imageBytes = Files.readAllBytes(Path.of(outputFilePathPath.toString()));

            return new ResponseEntity<>(imageBytes, HttpStatus.OK) ;

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping(value = "/player/skin/{name}",
            produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getskin(@PathVariable("name") String name){
        String imageUrl = "https://mc-heads.net/skin/maksutko.png";

        try {
            BufferedImage originalImage = ImageIO.read(new URL(imageUrl));
            Files.createDirectories(Paths.get("./bufferedheads/"));
            String outputFilePath = "./bufferedheads/" + name + "_head.png";

            File outputFile = new File(outputFilePath);
            ImageIO.write(originalImage, "png", outputFile);

            Path outputFilePathPath = Path.of(outputFilePath);

            byte[] imageBytes = Files.readAllBytes(Path.of(outputFilePathPath.toString()));

            return new ResponseEntity<>(imageBytes, HttpStatus.OK) ;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}





/*
    @JsonView(Views.Public.class)
    @GetMapping("/getuserdata")
    public ResponseEntity GetUserData(@RequestHeader("Authorization") String authToken){

        System.out.println(authToken);
        User user = userService.getUser(authToken);
        if(user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or out of date");

        return ResponseEntity.ok(user);
    }

    @PostMapping("/images")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Validate the file and perform necessary checks
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Invalid file type. Only image files are allowed.");
            }

            // Generate a unique filename for the image
            String filename = UUID.randomUUID().toString();

            // Save the file to the database
            Image image = new Image();
            image.setImageData(file.getBytes());
            image.setFilename(filename);
            // Set other image properties if needed
            // ...

            // Save the image to the database
            imageService.Save(image);

            return ResponseEntity.ok("Image uploaded successfully!");
        } catch (IOException e) {
            // Handle specific exceptions and provide detailed error messages
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image: " + e.getMessage());
        }
    }

    @PostMapping("/createpost")
    public ResponseEntity<String> createpost(@RequestBody CreatePost newPost) {

        Post post = new Post();
        User author = userService.getUser(newPost.getAuthor_Token());
        if(author == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        post.setAuthor(author);
        post.setContent(newPost.getContent());

        postService.Save(post);
        return ResponseEntity.ok("Success!");
    }

    @PostMapping("/addcomment")
    public ResponseEntity<String> addcomment(@RequestBody addComment newComment) {
        Comment comment = new Comment();
        User author = userService.getUser(newComment.getAuthor_Token());
        if(author == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        comment.setAuthor(author);


        Post post = postService.getByid(newComment.getPostid());

        if(post == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request");

        comment.setPost(post);

        comment.setContent(newComment.getContent());

        commentService.Save(comment);

        return ResponseEntity.ok("Success!");
    }

    @GetMapping("/posts")
    public ResponseEntity<String> getPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Post> posts = postService.getPosts(pageable);

        ObjectMapper objectMapper = new ObjectMapper();


        //String json = null;
        String jsonPublicView;
        //String jsonPrivateView;
        try {
            //json = objectMapper.writeValueAsString(posts);
            jsonPublicView = objectMapper.writerWithView(Views.Public.class).writeValueAsString(posts);
            //jsonPrivateView = objectMapper.writerWithView(Views.Private.class).writeValueAsString(posts);
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().body("Internal Error");
        }


        //System.out.println("Private View: " + jsonPrivateView);

        return ResponseEntity.ok().body(jsonPublicView);
    }*/


