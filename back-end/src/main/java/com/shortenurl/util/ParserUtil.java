package com.shortenurl.util;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import ua_parser.Parser;

@Component
public class ParserUtil  {
    @Bean
    public Parser parser() {
        return new Parser();
    }
}
