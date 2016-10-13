package org.arig.robot.model.system;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.io.IOException;

/**
 * @author gdepuille on 29/04/15.
 */
public class NetworkInfo {

    @JsonGetter
    public String getHostname() throws IOException, InterruptedException {
        return com.pi4j.system.NetworkInfo.getHostname();
    }

    @JsonGetter
    public String [] getFqdns() throws IOException, InterruptedException {
        return com.pi4j.system.NetworkInfo.getFQDNs();
    }

    @JsonGetter
    public String[] getDNSs() throws IOException, InterruptedException {
        return com.pi4j.system.NetworkInfo.getNameservers();
    }

    @JsonGetter
    public String [] getIPs() throws IOException, InterruptedException {
        return com.pi4j.system.NetworkInfo.getIPAddresses();
    }
}
