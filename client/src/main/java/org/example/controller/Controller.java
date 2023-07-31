package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class Controller {

    @GetMapping("/hallo")
    String hallo(@RequestHeader("Authorization") String auth){
        return "Hallo " + auth;
    }

    @GetMapping("/test")
    String test(){
        return "Test without auth ";
    }
}
