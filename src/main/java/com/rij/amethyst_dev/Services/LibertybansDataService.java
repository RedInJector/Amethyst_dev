package com.rij.amethyst_dev.Services;

import com.rij.amethyst_dev.LibertybansData.LibertybansDataRepository;
import com.rij.amethyst_dev.Services.UserService;
import org.springframework.stereotype.Service;

@Service
public class LibertybansDataService {
    private final LibertybansDataRepository libertybansDataRepository;
    private final UserService userService;

    public LibertybansDataService(LibertybansDataRepository libertybansDataRepository, UserService userService) {
        this.libertybansDataRepository = libertybansDataRepository;
        this.userService = userService;
    }

    public void pardon(String name){
        libertybansDataRepository.unBann(name);
    }


    public String getNameFromUUID(String uuid){
        return libertybansDataRepository.getNameFromUUID(uuid);
    }
}
