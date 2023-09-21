package com.rij.amethyst_dev.PlanData;

import com.rij.amethyst_dev.DTO.AllPlaytime;
import com.rij.amethyst_dev.DTO.AllPlaytime2;
import com.rij.amethyst_dev.DTO.User.PlayTimeDateDTO;
import com.rij.amethyst_dev.models.Userdb.User;
import com.rij.amethyst_dev.Services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlanDataService {
    private final PlanDataRepository planDataRepository;
    private final UserService userService;

    @Value("${minecraft.server.url}")
    public String MINECRAFT_SERVER_IP;
    @Value("${minecraft.server.APIKEY}")
    public String MINECRAFT_SERVER_API_KEY;


    public PlanDataService(PlanDataRepository planDataRepository, UserService userService) {
        this.planDataRepository = planDataRepository;
        this.userService = userService;
    }

    public void checkPlanUser(User user){
        if(user.getPlanUserId() != null)
            return;

        try {
            Integer palnuserid = planDataRepository.getPlanPlayerId(user.getMinecraftPlayer().getPlayerName());
            user.setPlanUserId(palnuserid);
            userService.saveUser(user);
        }catch (Exception any){
            return;
        }
    }

    public List<PlayTimeDateDTO> getHeatmapTime(User user){
        checkPlanUser(user);
        if(user.getPlanUserId() == null)
            return new ArrayList<>();

        return planDataRepository.getHeatmapData(user.getPlanUserId());
    }

    public long getLastOnline(User user){
        checkPlanUser(user);
        if(user.getPlanUserId() == null)
            return -1;

        Date lastOnline = planDataRepository.getLastOnline(user.getPlanUserId());
        if(lastOnline == null)
            return -1;

        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(lastOnline);

        long timeDifferenceInMillis =  calendar.getTimeInMillis() - targetCalendar.getTimeInMillis();

        long Difference = timeDifferenceInMillis / 1000;

        return Difference;
    }


    public AllPlaytime getPlayTime(User user){
        checkPlanUser(user);
        if(user.getPlanUserId() == null)
            return new AllPlaytime("0", "0", "0", "0");

       return planDataRepository.getAllPlaytime(user.getPlanUserId());
    }



    public List<AllPlaytime2> getAllPlaytimeForusers(List<User> users){
        List<Integer> userids = new ArrayList<>(users.size());
        users.forEach(user -> {
            checkPlanUser(user);
            userids.add(user.getPlanUserId());
        });
        List<AllPlaytime2> allPlaytime = planDataRepository.getAllPlaytimeForUsers(userids);

        List<AllPlaytime2> result = new ArrayList<>(allPlaytime.size());
        for(int i = 0; i < allPlaytime.size(); i++){
            result.add(allPlaytime.get(i).planuserid(), allPlaytime.get(i));
        }


        return result;
    }
}
