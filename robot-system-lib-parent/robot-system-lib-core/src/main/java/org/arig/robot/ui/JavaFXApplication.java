package org.arig.robot.ui;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.config.spring.BootifullApplication;
import org.arig.robot.ui.events.StageReadyEvent;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

@Slf4j
public class JavaFXApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
        super.init();

        ApplicationContextInitializer<GenericApplicationContext> initializer =
            ac -> {
                ac.registerBean(Application.class, () -> JavaFXApplication.this);
                ac.registerBean(Parameters.class, this::getParameters);
                ac.registerBean(HostServices.class, this::getHostServices);
            };

        this.context = new SpringApplicationBuilder()
                .sources(BootifullApplication.class)
                .initializers(initializer)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.context.publishEvent(new StageReadyEvent(primaryStage));

        // Configuration a faire pour chaque match (gestion sans redemarrage programme)
        // Définition d'un ID unique pour le nommage des fichiers
        //final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        //System.setProperty(IConstantesConfig.keyExecutionId, execId);

        // Initialisation du logger après définition de la property pour prise en compte
        // lors de la création du fichier de log.
        //log.info("Demarrage de Nerell {} ...", execId);

        // A placer dans un thread a part
        // Ordonanceur.getInstance().run();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // Ecriture d'un fichier identifiant une execution termine.
//        final String execId = System.getProperty(IConstantesConfig.keyExecutionId);
//        final File execFile = new File("./logs/" + execId + ".exec");
//        log.info("Création du fichier de fin d'éxécution {} : {}", execFile.getAbsolutePath(), execFile.createNewFile());

        this.context.close();
        Platform.exit();
    }
}
