package com.rij.amethyst_dev.models.Userdb;

import com.rij.amethyst_dev.Helpers.TimeTester;
import com.rij.amethyst_dev.events.UserRegisteredEvent;
import com.rij.amethyst_dev.models.Userdb.MinecraftPlayers.MinecraftPlayer;
import com.rij.amethyst_dev.models.Userdb.MinecraftPlayers.MinecraftPlayerRepository;
import com.rij.amethyst_dev.models.Userdb.Tokens.AccessToken;
import com.rij.amethyst_dev.models.Userdb.Tokens.AccessTokensRepository;
import com.rij.amethyst_dev.models.oAuth.Oauth;
import com.rij.amethyst_dev.models.oAuth.oAuth2Repository;
import org.redinjector.discord.oAuth2.models.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private List<String> autoban = new ArrayList<>();

    @Autowired
    public UserService(UserRepository userRepository, oAuth2Repository oAuth2Repository, ApplicationEventPublisher eventPublisher, MinecraftPlayerRepository minecraftPlayerRepository, AccessTokensRepository accessTokensRepository){
        this.userRepository = userRepository;
        this.oAuth2Repository = oAuth2Repository;
        this.eventPublisher = eventPublisher;
        this.minecraftPlayerRepository = minecraftPlayerRepository;
        this.accessTokensRepository = accessTokensRepository;

        //autoban.add("");
    }



    public void saveUser(User user){
        userRepository.save(user);
    }

    public User getUser(User user){
        return  userRepository.findByDiscordUserDiscordId(user.getDiscordUser().getDiscordId());
    }

    public User saveUserIfNotExists(User user){

        User existinguser = getUser(user);
        if(existinguser == null){
            if(autoban.contains(user.getDiscordUser().getDiscordId()))
                user.setBanned(true);

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

    public MinecraftPlayer getMinecraftPlayer(String name){
        return minecraftPlayerRepository.findByPlayerName(name);
    }

    public User getUserWithMinecraftname(String name){
        User u = userRepository.findByMinecraftPlayerPlayerName(name);

        return u;
    }
    public Optional<User> getById(int id){
        Optional<User> user = userRepository.findById(id);
        return user;
    }

    public List<User> allUsers(){
        return userRepository.findAll();
    }

    public User getUserByDiscordId(String discordid){
        return userRepository.findByDiscordUserDiscordId(discordid);
    }

    public Page<User> getUserPages(int page, int amount){
        Sort sortByUserId = Sort.by(Sort.Direction.ASC, "id");


        Pageable pageable = PageRequest.of(page, amount, sortByUserId);
        Page<User> u =  userRepository.findUsersWithNonNullMinecraftNameAndHasPayed(pageable);

        return u;
    }

    public List<User> Search(String name){
        Pageable maxpage = PageRequest.of(0, 10);
        return userRepository.findUsersWithMinecraftNameAndHasPayed(name, maxpage);
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

