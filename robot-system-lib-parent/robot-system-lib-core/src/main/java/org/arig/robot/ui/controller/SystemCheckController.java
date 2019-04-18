package org.arig.robot.ui.controller;

import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import io.datafx.controller.ViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import sun.nio.ch.Net;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@ViewController(value = "/fxml/systemCheck.fxml", title = "System check")
public class SystemCheckController extends AbstractSpringDataFxController {

    @FXML
    public Label lblIp;

    @FXML
    public GridPane glyphCodeurDroit;

    @PostConstruct
    public void init() throws Exception {
        super.init();

        // Get Network IP
        List<String> networkInfo = new ArrayList<>();
        Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
        while (nis.hasMoreElements()) {
            NetworkInterface ni = nis.nextElement();
            if (!ni.isLoopback() && !ni.isVirtual()) {
                String lbl = ni.getDisplayName() + " ";
                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                boolean first = true;
                while(inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!first) {
                        lbl += ", ";
                        first = false;
                    }
                    lbl += inetAddress.getHostAddress();
                }
                networkInfo.add(lbl);
            }
        }
        lblIp.setText(StringUtils.join(networkInfo, ", "));

        // Check
        String name = SVGGlyphLoader.getAllGlyphsIDs().stream().filter(n -> n.contains("close")).findFirst().get();
        SVGGlyph glyph = SVGGlyphLoader.getIcoMoonGlyph(name);
        glyph.setFill(Paint.valueOf("0xFF0000"));
        glyph.setSize(12);
        glyphCodeurDroit.getChildren().add(glyph);
    }
}
