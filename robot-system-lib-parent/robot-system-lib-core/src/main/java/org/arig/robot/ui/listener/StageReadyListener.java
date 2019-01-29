package org.arig.robot.ui.listener;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.ui.controller.RootController;
import org.arig.robot.ui.events.StageReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Profile(IConstantesConfig.profileUI)
public class StageReadyListener implements ApplicationListener<StageReadyEvent> {

    private final String appTitle;
    private final ApplicationContext applicationContext;
    private final ViewFlowContext flowContext;

    public StageReadyListener(@Value("${robot.ui.title}") String appTitle, ApplicationContext applicationContext) {
        this.appTitle = appTitle;
        this.applicationContext = applicationContext;
        this.flowContext = new ViewFlowContext();
        this.flowContext.register(IConstantesConfig.keySpringContext, applicationContext);
    }

    @Override
    public void onApplicationEvent(final StageReadyEvent event) {
        try {
            new Thread(() -> {
                try {
                    SVGGlyphLoader.loadGlyphsFont(
                            applicationContext.getResource("classpath:/fonts/icomoon.svg").getInputStream(),
                            "icomoon.svg"
                    );
                } catch (IOException e) {
                    log.error("Erreur de chargement du svg icomoon", e);
                }
            }).start();

            Flow flow = new Flow(RootController.class);
            DefaultFlowContainer container = new DefaultFlowContainer();
            Stage stage = event.getStage();
            flowContext.register("Stage", stage);
            flow.createHandler(flowContext).start(container);

            JFXDecorator decorator = new JFXDecorator(stage, container.getView());
            decorator.setCustomMaximize(true);
            decorator.setGraphic(new SVGGlyph(""));

            Scene scene = new Scene(decorator, 800, 480);
            final ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.addAll(
                applicationContext.getResource("classpath:/css/jfoenix-fonts.css").getURL().toExternalForm(),
                applicationContext.getResource("classpath:/css/jfoenix-design.css").getURL().toExternalForm(),
                applicationContext.getResource("classpath:/css/root.css").getURL().toExternalForm()
            );
            stage.setScene(scene);
            stage.setTitle(this.appTitle);
            stage.show();
        } catch (IOException | FlowException e) {
            log.error("Erreur de chargement de l'écran principal", e);
        }
    }
}
