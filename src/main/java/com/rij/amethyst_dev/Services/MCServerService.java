package com.rij.amethyst_dev.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    private Set<String> onlinePlayers = new HashSet<>();

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

        ResponseEntity<String> response = restTemplate.postForEntity(MINECRAFT_SERVER_IP + "/whitelist/add", requestEntity, String.class);
    }

    public void  permaBan(String name, String reason){

    }

    public void ban(String name, String reason){

    }

    public void unban(String name){

    }




}
