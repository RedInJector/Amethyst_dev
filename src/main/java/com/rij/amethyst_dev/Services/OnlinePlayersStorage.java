package com.rij.amethyst_dev.Services;


import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class OnlinePlayersStorage {

    private Set<String> onlinePlayers = new HashSet<>();

    public void setOnline(String name, boolean status){
        if(status)
            onlinePlayers.add(name);
        else
            onlinePlayers.remove(name);
    }

    public boolean isOnline(String name){
        return onlinePlayers.contains(name);
    }
}
