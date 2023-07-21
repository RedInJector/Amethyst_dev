package com.rij.amethyst_dev.Dev.Routes;



import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/p")
public class PlayerREST {
    @GetMapping("/{name}")
    public void getPublicPlayer(@PathVariable("name") String name){

    }






}
