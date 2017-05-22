package org.arig.robot.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesNerellConfig;

import java.util.Deque;
import java.util.LinkedList;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class RobotStatus extends AbstractRobotStatus {

    @Setter(AccessLevel.NONE)
    private boolean simulateur = false;

    public void setSimulateur() {
        simulateur = true;
    }

    private Team team = Team.UNKNOWN;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private StopWatch matchTime = new StopWatch();

    public void startMatch() {
        matchTime.start();
    }
    public void stopMatch() {
        matchTime.stop();
    }
    public long getElapsedTime() {
        return matchTime.getTime();
    }

    @Setter(AccessLevel.NONE)
    private boolean calageBordureEnabled = false;
    public void enableCalageBordure() {
        log.info("Activation calage bordure");
        calageBordureEnabled = true;
    }
    public void disableCalageBordure() {
        log.info("Désactivation calage bordure");
        calageBordureEnabled = false;
    }

    // Magasin module lunaire
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Deque<ModuleLunaire> magasinModule = new LinkedList<>();

    public void addModuleDansMagasin(ModuleLunaire m) {
        magasinModule.add(m);
    }
    public ModuleLunaire nextModule() {
        return magasinModule.peek();
    }
    public ModuleLunaire extractModule() {
        return magasinModule.poll();
    }
    public boolean hasModuleDansMagasin() {
        return CollectionUtils.isNotEmpty(magasinModule);
    }
    public boolean canAddModuleMagasin() { return  magasinModule.size() < IConstantesNerellConfig.nbModuleMax; }

    // Fusées
    private boolean fuseeMonochromeJauneRecupere = false;
    private boolean fuseePolychromeJauneRecupere = false;
    private boolean fuseeMonochromeBleuRecupere = false;
    private boolean fuseePolychromeBleuRecupere = false;

    // Modules sur table
    private boolean module0BleuRecupere = false; // Monochrome zone depart bleu
    private boolean module1BleuRecupere = false; // Polychrome proche cratère et zone départ bleu
    private boolean module2BleuRecupere = false; // Monochrome dépose fusée polychrome bleu
    private boolean module3BleuRecupere = false; // Polychrome centrale bleu
    private boolean module4BleuRecupere = false; // Polychrome base lunaire bleu
    private boolean module5BleuRecupere = false; // Monochrome entre gros et petit cratère bleu

    private boolean module0JauneRecupere = false; // Monochrome zone depart jaune
    private boolean module1JauneRecupere = false; // Polychrome proche cratère et zone départ jaune
    private boolean module2JauneRecupere = false; // Monochrome dépose fusée polychrome jaune
    private boolean module3JauneRecupere = false; // Polychrome centrale jaune
    private boolean module4JauneRecupere = false; // Polychrome base lunaire jaune
    private boolean module5JauneRecupere = false; // Monochrome entre gros et petit cratère jaune

    // Cratères
    private boolean cratereZoneDepartBleuRecupere = false;
    private boolean cratereBaseLunaireBleuRecupere = false;
    private boolean cratereImmenseBleuRecupere = false;

    private boolean cratereZoneDepartJauneRecupere = false;
    private boolean cratereBaseLunaireJauneRecupere = false;
    private boolean cratereImmenseJauneRecupere = false;

}
