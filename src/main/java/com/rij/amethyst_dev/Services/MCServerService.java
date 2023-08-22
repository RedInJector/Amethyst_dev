package com.rij.amethyst_dev.Services;

import com.rij.amethyst_dev.Configuration.URLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;

@Service
public class MCServerService {

    @Value("${minecraft.server.url}")
    public String MINECRAFT_SERVER_IP;
    @Value("${minecraft.server.APIKEY}")
    public String MINECRAFT_SERVER_API_KEY;
    Logger logger = LoggerFactory.getLogger(MCServerService.class);

    private Set<String> onlinePlayers = new HashSet<>();

    public MCServerService(){
        URLS.setServerIPPort(MINECRAFT_SERVER_IP);
    }

    public void setOnline(String name, boolean status){
        if(status)
            onlinePlayers.add(name);
        else
            onlinePlayers.remove(name);
    }

    public void removeAllOnline(){
        onlinePlayers.clear();
    }

    public boolean isOnline(String name){
        return onlinePlayers.contains(name);
    }



    public void addToWhitelist(String username){
        RestTemplate restTemplate = new RestTemplate();


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", MINECRAFT_SERVER_API_KEY);
        headers.add("name", username);
        headers.setContentType(MediaType.APPLICATION_JSON);


        String requestBody = "";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity( URLS.WhitelistAdd, requestEntity, String.class);
    }

    public void permaBan(String name, String reason){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", MINECRAFT_SERVER_API_KEY);
        headers.add("name", name);
        headers.add("reason", reason);
        headers.add("time", "0");
        headers.setContentType(MediaType.APPLICATION_JSON);


        ResponseEntity<String> response = restTemplate.getForEntity(URLS.LibertyBansBan, String.class, headers);

        System.out.println("Response: " + response);
    }

    public void ban(String name, String reason){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", MINECRAFT_SERVER_API_KEY);
        headers.add("name", name);
        headers.add("reason", reason);
        headers.add("time", "0");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(URLS.LibertyBansBan, HttpMethod.GET, requestEntity, String.class);

        if(response.getStatusCode().is2xxSuccessful())
            logger.info(name + " Was successfully Banned");

        System.out.println("Response: " + response.getBody());
    }

    public void unban(String name){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", MINECRAFT_SERVER_API_KEY);
        headers.add("name", name);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(URLS.LibertyBansUnBan, HttpMethod.GET, requestEntity, String.class);

        if(response.getStatusCode().is2xxSuccessful())
            logger.info(name + " Was successfully unBanned");

        System.out.println("Response: " + response);
    }




}
