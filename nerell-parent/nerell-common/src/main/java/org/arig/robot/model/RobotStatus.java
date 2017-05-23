package org.arig.robot.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.springframework.beans.factory.InitializingBean;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class RobotStatus extends AbstractRobotStatus implements InitializingBean {

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

    // Pinces
    private ModuleLunaire moduleLunaireExpected;
    private ModuleLunaire moduleLunaireDroite;
    private ModuleLunaire moduleLunaireCentre;

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
    private Map<Integer, Boolean> modulesRecuperes = new HashMap<>();

    // Cratères
    private boolean cratereZoneDepartBleuRecupere = false;
    private boolean cratereBaseLunaireBleuRecupere = false;
    private boolean cratereImmenseBleuRecupere = false;

    private boolean cratereZoneDepartJauneRecupere = false;
    private boolean cratereBaseLunaireJauneRecupere = false;
    private boolean cratereImmenseJauneRecupere = false;

    @Override
    public void afterPropertiesSet() throws Exception {
        modulesRecuperes.put(1, false);
        modulesRecuperes.put(2, false);
        modulesRecuperes.put(3, false);
        modulesRecuperes.put(4, false);
        modulesRecuperes.put(5, false);
        modulesRecuperes.put(6, false);
        modulesRecuperes.put(7, false);
        modulesRecuperes.put(8, false);
        modulesRecuperes.put(9, false);
        modulesRecuperes.put(10, false);
    }

    public void setModuleRecupere(Integer numero) {
        modulesRecuperes.put(numero, true);
    }

    public Boolean isModuleRecupere(Integer numero) {
        return modulesRecuperes.get(numero);
    }
}
