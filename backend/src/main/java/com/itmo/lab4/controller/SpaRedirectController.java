package com.itmo.lab4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaRedirectController {
    // If Spring doesn't find a file/API for these paths,
    // it forwards index.html and let Angular's router figure it out.

    @RequestMapping(value = { "/login", "/main", "/registration" })
    public String forward() {
        return "forward:/";
    }
}
