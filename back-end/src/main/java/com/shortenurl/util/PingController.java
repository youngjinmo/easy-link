package com.shortenurl.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PingController {
    @GetMapping("/.ping")
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "pong!";
    }
}
