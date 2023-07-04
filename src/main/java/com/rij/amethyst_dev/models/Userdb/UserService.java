package com.rij.amethyst_dev.models.Userdb;

import com.rij.amethyst_dev.events.UserRegisteredEvent;
import com.rij.amethyst_dev.models.oAuth.Oauth;
import org.redinjector.discord.oAuth2.models.DiscordUser;
import com.rij.amethyst_dev.models.oAuth.oAuth2Repository;
import org.redinjector.discord.oAuth2.models.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final oAuth2Repository oAuth2Repository;
    private final ApplicationEventPublisher eventPublisher;
    private final MinecraftPlayerRepository minecraftPlayerRepository;
    private final AccessTokensRepository accessTokensRepository;

    @Autowired
    public UserService(UserRepository userRepository, oAuth2Repository oAuth2Repository, ApplicationEventPublisher eventPublisher, MinecraftPlayerRepository minecraftPlayerRepository, AccessTokensRepository accessTokensRepository){
        this.userRepository = userRepository;
        this.oAuth2Repository = oAuth2Repository;
        this.eventPublisher = eventPublisher;
        this.minecraftPlayerRepository = minecraftPlayerRepository;
        this.accessTokensRepository = accessTokensRepository;
    }



    public void saveUser(User user){
        userRepository.save(user);
    }

    public User getUser(User user){
        return  userRepository.findByDiscordUserDiscordId(user.getDiscordUser().getDiscordId());
    }
    public User createUserFromDiscordUser(DiscordUser discordUser){
        User user = new User();
        com.rij.amethyst_dev.models.Userdb.DiscordUser duser = new com.rij.amethyst_dev.models.Userdb.DiscordUser();
        duser.setDiscordId(discordUser.getId());
        duser.setDiscordVerified(discordUser.isVerified());
        duser.setAvatarUrl(discordUser.getAvatar());
        duser.setEmail(discordUser.getEmail());
        duser.setPublicUsername(discordUser.getUsername());
        duser.setDiscriminator(discordUser.getDiscriminator());

        user.setDiscordUser(duser);
        //duser.setUser(user);

        return user;
    }


    public User saveUserIfNotExists(User user){

        User existinguser = getUser(user);
        if(existinguser == null){
            saveUser(user);
            UserRegisteredEvent event = new UserRegisteredEvent(this, user);
            eventPublisher.publishEvent(event);
            existinguser = user;
        }
        return existinguser;
    }
    public void saveOauth(User user, Token token){
        if(user.getOauth() != null){
            user.getOauth().setAccessToken(token.getAccessToken());
            user.getOauth().setRefreshToken(token.getRefreshToken());
            user.getOauth().setExpiresOn(LocalDateTime.now().plusDays(7));
        }
        else {
            Oauth oauth = new Oauth();
            oauth.setAccessToken(token.getAccessToken());
            oauth.setRefreshToken(token.getRefreshToken());
            oauth.setExpiresOn(LocalDateTime.now().plusDays(7));
            user.setOauth(oauth);
        }

        userRepository.save(user);
    }
    public void saveNewAccessToken(User user, String token){
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(token);
        accessToken.setExpiresOn(LocalDateTime.now().plusDays(7));

        user.addAccessToken(accessToken);

        userRepository.save(user);
    }
    public User getUserByAccessToken(String token){
        return userRepository.findByAccessTokensToken(token);
    }
    public void removeUserAccessToken(User user, String accessToken){
        user.RemoveAccessToken(accessToken);
        userRepository.save(user);
    }
    public User getUserWithToken(String token){
        Oauth oauth = oAuth2Repository.findByAccessToken(token);
        if (oauth != null) {
            if(oauth.getExpiresOn().isAfter(LocalDateTime.now()))
                return oauth.getUser();

            oAuth2Repository.delete(oauth);
        }
        return null;
    }
    public List<Oauth> getUsersoAuths(User user){
        return oAuth2Repository.findAllByUser_Id(user.getId());
    }

    public User getUserByoAuthToken(String Token){
        User user = userRepository.findByOauthAccessToken(Token);
        return user;

        /*

        oAuth2 oAuth2 = oAuth2Repository.findByAccessToken(Token);
        if(oAuth2 != null)
            return oAuth2.getUser();

        return null;*/
    }
    public void deteleToken(String Token){
        Oauth oauth = oAuth2Repository.findByAccessToken(Token);
        oAuth2Repository.delete(oauth);
    }
    public MinecraftPlayer getMinecraftPlayer(String name){
        return minecraftPlayerRepository.findByPlayerName(name);
    }

    public User getUserWithMinecraftname(String name){
        return userRepository.findByMinecraftPlayerPlayerName(name);
    }
    public Optional<User> getById(int id){
        Optional<User> user = userRepository.findById(id);
        return user;
    }

    public List<User> allUsers(){
        return userRepository.findAll();
    }

    public void purgeUnusedAccessTokens(){
        List<AccessToken> accessTokens = accessTokensRepository.findAll();

        Iterator<AccessToken> iterator = accessTokens.iterator();
        while (iterator.hasNext()) {
            AccessToken obj = iterator.next();
            // Apply your condition here
            if (obj.getExpiresOn().isBefore(LocalDateTime.now())) {
                // Execute your function here
                accessTokensRepository.delete(obj);
                // Remove the object from the list
                iterator.remove();
            }
        }
    }
}

