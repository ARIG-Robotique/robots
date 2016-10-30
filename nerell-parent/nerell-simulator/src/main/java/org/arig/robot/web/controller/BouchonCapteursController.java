package org.arig.robot.web.controller;

import org.arig.robot.services.IOServiceBouchon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gdepuille on 31/10/16.
 */
@RequestMapping("/capteurs")
@RestController
public class BouchonCapteursController {

    @Autowired
    private IOServiceBouchon ioServiceBouchon;

    @RequestMapping(value = "/tirette", method = RequestMethod.POST)
    public void setTirette(@RequestBody Boolean value) {
        ioServiceBouchon.setTirette(value);
    }
}
