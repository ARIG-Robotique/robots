package org.arig.eurobot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gdepuille on 13/01/15.
 */
@Slf4j
@RestController
@RequestMapping(value = "/robot")
public class CheckController {

    @RequestMapping()
    public Map<String, String> check() {
        Map<String, String> v = new HashMap<>();
        v.put("nom", "Main Robot 2K13");
        v.put("version", "1.0");

        return v;
    }
}
