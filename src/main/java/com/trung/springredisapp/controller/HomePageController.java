package com.trung.springredisapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/")
public class RootController {
    public RedirectView homepage() {
        return new RedirectView("/index.html");
    }
    public RedirectView aboutpage() {
        return new RedirectView("/about.html");
    }
}