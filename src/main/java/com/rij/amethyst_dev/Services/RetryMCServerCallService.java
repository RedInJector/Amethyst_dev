package com.rij.amethyst_dev.Services;


import com.rij.amethyst_dev.Configuration.URLS;
import com.rij.amethyst_dev.DTO.PostCall;
import com.rij.amethyst_dev.Routes.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
@EnableScheduling
public class RetryMCServerCallService {

    private List<PostCall> unsuccsessfullCalls = new LinkedList<>();
    Logger logger = LoggerFactory.getLogger(RetryMCServerCallService.class);


    public void add(PostCall call){
        unsuccsessfullCalls.add(call);
    }

    @Scheduled(fixedDelayString = "60000")
    public void resync(){
        if(unsuccsessfullCalls.isEmpty())
            return;

        Iterator<PostCall> iterator = unsuccsessfullCalls.iterator();
        RestTemplate restTemplate = new RestTemplate();
        while (iterator.hasNext()) {
            PostCall element = iterator.next();

            // Call your function here, and check if it throws an error
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(element.url(), element.requestEntity(), String.class);
                if(!response.getStatusCode().equals("200"))
                    return;
            }catch (Exception any){
                return;
            }

            logger.info("resync: called " + element.url());
            iterator.remove();

        }
    }






}
