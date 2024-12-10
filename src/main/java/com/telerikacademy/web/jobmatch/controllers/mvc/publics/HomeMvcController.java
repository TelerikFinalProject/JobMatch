package com.telerikacademy.web.jobmatch.controllers.mvc.publics;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    @GetMapping
    public String homeView() {
        return "index";
    }
}