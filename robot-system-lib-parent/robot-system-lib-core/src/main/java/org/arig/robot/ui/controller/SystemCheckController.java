package org.arig.robot.ui.controller;

import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import io.datafx.controller.ViewController;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;

@Slf4j
@ViewController(value = "/fxml/systemCheck.fxml", title = "System check")
public class SystemCheckController extends AbstractSpringDataFxController {

    @FXML
    public GridPane glyphCodeurDroit;

    @PostConstruct
    public void init() throws Exception {
        super.init();

        String name = SVGGlyphLoader.getAllGlyphsIDs().stream().filter(n -> n.contains("close")).findFirst().get();
        SVGGlyph glyph = SVGGlyphLoader.getIcoMoonGlyph(name);
        glyph.setFill(Paint.valueOf("0xFF0000"));
        glyphCodeurDroit.getChildren().add(glyph);
    }
}
