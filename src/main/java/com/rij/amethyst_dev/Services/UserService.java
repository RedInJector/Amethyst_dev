package com.rij.amethyst_dev.Services;

import com.rij.amethyst_dev.Dev.DTO.Admin.UserPages;
import com.rij.amethyst_dev.Dev.DTO.User.Builder.UserDataDTOBuilder;
import com.rij.amethyst_dev.Dev.DTO.User.UserDataDTO;
import com.rij.amethyst_dev.Helpers.TimeTester;
import com.rij.amethyst_dev.events.UserRegisteredEvent;
import com.rij.amethyst_dev.models.Userdb.MinecraftPlayers.MinecraftPlayer;
import com.rij.amethyst_dev.models.Userdb.MinecraftPlayers.MinecraftPlayerRepository;
import com.rij.amethyst_dev.models.Userdb.Tokens.AccessToken;
import com.rij.amethyst_dev.models.Userdb.Tokens.AccessTokensRepository;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.models.Userdb.UserRepository;
import com.rij.amethyst_dev.models.oAuth.Oauth;
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
    private final ApplicationEventPublisher eventPublisher;
    private final MinecraftPlayerRepository minecraftPlayerRepository;
    private final AccessTokensRepository accessTokensRepository;
    private List<String> autoban = new ArrayList<>();

    @Autowired
    public UserService(UserRepository userRepository, ApplicationEventPublisher eventPublisher, MinecraftPlayerRepository minecraftPlayerRepository, AccessTokensRepository accessTokensRepository){
        this.userRepository = userRepository;
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

    public UserPages getUserPages(int page){
        TimeTester time1 = new TimeTester();
        time1.start();
        Pageable pageable = PageRequest.of(page, 20);
        Page<User> a = userRepository.findAll(pageable);
        time1.end();
        List<UserDataDTO> userDTOs = new ArrayList<>();
        a.getContent().forEach(user1 ->
                userDTOs.add(new UserDataDTOBuilder().addPrivateUserData(user1).build()));

        return new UserPages(userDTOs, a.getNumber(), a.getTotalPages());

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

    public void removeExpiredtokens(User user){
        user.getAccessTokens().forEach(accessToken -> {
            if(accessToken.getExpiresOn().isBefore(LocalDateTime.now())){
                accessToken.setUser(null);
            }
        });

        userRepository.save(user);
    }
}

