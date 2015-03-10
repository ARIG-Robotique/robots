package org.arig.prehistobot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.encoders.ARIG2WheelsEncoders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gdepuille on 22/12/14.
 */
@Slf4j
@Profile("raspi")
@RestController
@RequestMapping("/codeurs")
public class CodeursController {

    @Autowired
    private ARIG2WheelsEncoders encoders;

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Double> showValues() {
        encoders.lectureValeurs();
        Map<String, Double> v = new HashMap<>();
        v.put("distance", encoders.getDistance());
        v.put("orientation", encoders.getOrientation());

        return v;
    }
}
