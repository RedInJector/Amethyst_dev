package com.rij.amethyst_dev.LibertybansData;

import com.rij.amethyst_dev.DTO.AllPlaytime;
import com.rij.amethyst_dev.DTO.PlayTimeDateDTO;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.models.Userdb.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
